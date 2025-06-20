import React, { useState, useEffect, useMemo, useCallback } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import 'bootstrap/dist/css/bootstrap.min.css';
import { useAuth } from '../contexts/AuthContext';

const GATEWAY_BASE_URL = 'http://localhost:8081';
const KORISNICI_DOKTORI_API_PREFIX = `${GATEWAY_BASE_URL}/api/korisnici-doktori`;
const TERMINI_PREGLEDI_API_PREFIX = `${GATEWAY_BASE_URL}/api/termini-pregledi`;

function DoctorProfilePage() {
    const { id } = useParams();
    const { token, user, loading: authLoading } = useAuth();
    const navigate = useNavigate();

    const [doctor, setDoctor] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [availableTerms, setAvailableTerms] = useState([]);
    const [selectedTermId, setSelectedTermId] = useState('');
    const [reason, setReason] = useState('');
    const [bookingMessage, setBookingMessage] = useState('');
    const [bookingMessageType, setBookingMessageType] = useState('success');

    const memoizedAuthHeaders = useMemo(() => {
        return token ? { headers: { 'Authorization': `Bearer ${token}` } } : {};
    }, [token]);

    const fetchDoctorDetails = useCallback(async () => {
        setLoading(true);
        setError(null);
        try {
            const apiUrl = `${KORISNICI_DOKTORI_API_PREFIX}/api/doktori/${id}`;
            const response = await axios.get(apiUrl);
            const data = response.data;

            const transformedDoctor = {
                doktorID: data.doktorID,
                ime: data.ime,
                prezime: data.prezime,
                fullName: `${data.ime} ${data.prezime}`,
                email: data.email,
                telefon: data.telefon,
                specijalizacije: Array.isArray(data.specijalizacije) ? data.specijalizacije.join(', ') : data.specijalizacije,
                grad: data.grad,
                radnoVrijeme: data.radnoVrijeme,
                iskustvo: data.iskustvo,
                ocjena: data.ocjena,
                profileImageBase64: data.profileImageBase64 || `https://placehold.co/100x100/CCCCCC/000000?text=${data.ime.charAt(0)}${data.prezime.charAt(0)}`
            };
            setDoctor(transformedDoctor);
        } catch (err) {
            console.error("Greška prilikom dohvaćanja detalja doktora:", err.response?.data || err.message);
            setError("Nije moguće učitati profil doktora. Molimo pokušajte ponovo.");
        } finally {
            setLoading(false);
        }
    }, [id]);

    useEffect(() => {
        const fetchAllAvailableTerms = async () => {
            if (!doctor?.doktorID || !token) {
                setAvailableTerms([]);
                return;
            }
            try {
                const response = await axios.get(`${TERMINI_PREGLEDI_API_PREFIX}/api/termini/doktor/${doctor.doktorID}`, memoizedAuthHeaders);
                const termsData = Array.isArray(response.data) ? response.data : [];

                const today = new Date();
                today.setHours(0, 0, 0, 0);

                const availableFutureTerms = termsData
                    .filter(term => {
                        const termDate = new Date(term.datum);
                        // Provjerite je li termin dostupan i je li datum u budućnosti ili je danas i vrijeme je u budućnosti
                        const [hours, minutes, seconds] = term.vrijeme.split(':').map(Number);
                        const termDateTime = new Date(termDate.getFullYear(), termDate.getMonth(), termDate.getDate(), hours, minutes, seconds || 0);

                        return term.statusTermina === 'DOSTUPAN' && termDateTime > new Date(); // Provjera za budućnost uključujući i vrijeme
                    })
                    .map(term => ({
                        terminID: term.terminID,
                        datum: term.datum,
                        vrijeme: term.vrijeme, // Čuvamo originalni format vremena
                        display: `${new Date(term.datum).toLocaleDateString('bs-BA')} - ${term.vrijeme.substring(0, 5)}`
                    }))
                    .sort((a, b) => {
                        const dateA = new Date(`${a.datum}T${a.vrijeme}`);
                        const dateB = new Date(`${b.datum}T${b.vrijeme}`);
                        return dateA.getTime() - dateB.getTime();
                    });

                setAvailableTerms(availableFutureTerms);
                if (availableFutureTerms.length > 0) {
                    setSelectedTermId(availableFutureTerms[0].terminID);
                } else {
                    setSelectedTermId('');
                }

            } catch (err) {
                console.error('Greška pri dohvaćanju svih dostupnih termina:', err.response?.data || err.message);
                setAvailableTerms([]);
                setBookingMessage('Greška pri dohvaćanju dostupnih termina.');
                setBookingMessageType('danger');
            }
        };

        if (doctor?.doktorID && token) {
            fetchAllAvailableTerms();
        }
    }, [doctor, token, memoizedAuthHeaders]);


    useEffect(() => {
        if (id) {
            fetchDoctorDetails();
        }
    }, [id, fetchDoctorDetails]);


    const handleTermChange = (e) => {
        setSelectedTermId(e.target.value);
    };

    const handleReasonChange = (e) => {
        setReason(e.target.value);
    };

    const handleBookAppointment = async (e) => {
        e.preventDefault();
        setBookingMessage('');

        if (!user || !token) {
            setBookingMessage('Morate biti prijavljeni da biste zakazali pregled.');
            setBookingMessageType('danger');
            return;
        }

        if (!user.roles || !user.roles.includes('ROLE_PACIJENT')) {
            setBookingMessage('Samo pacijenti mogu zakazivati preglede.');
            setBookingMessageType('danger');
            return;
        }

        if (!selectedTermId || !reason || !doctor?.doktorID) {
            setBookingMessage('Molimo odaberite termin i popunite razlog za pregled.');
            setBookingMessageType('danger');
            return;
        }

        const selectedTerm = availableTerms.find(term => String(term.terminID) === selectedTermId);
        if (!selectedTerm) {
            setBookingMessage('Odabrani termin nije važeći ili dostupan.');
            setBookingMessageType('danger');
            return;
        }

        // KLJUČNA IZMJENA: Osigurajte HH:MM:SS format vremena
        let formattedVrijemePregleda = selectedTerm.vrijeme;
        if (formattedVrijemePregleda.length === 5) { // Provjerite je li format HH:MM
            formattedVrijemePregleda += ':00'; // Dodajte sekunde
        } else if (formattedVrijemePregleda.length !== 8) { // Ako nije ni HH:MM ni HH:MM:SS
            console.warn("Unexpected time format from backend:", formattedVrijemePregleda);
            // Pokušaj parsiranja i reformatiranja za svaki slučaj
            const timeParts = formattedVrijemePregleda.split(':');
            const h = timeParts[0].padStart(2, '0');
            const m = timeParts[1] ? timeParts[1].padStart(2, '0') : '00';
            const s = timeParts[2] ? timeParts[2].padStart(2, '0') : '00';
            formattedVrijemePregleda = `${h}:${m}:${s}`;
        }


        try {
            const pacijentId = user?.pacijentId;
            if (!pacijentId) {
                setBookingMessage('Vaš ID pacijenta nije pronađen. Molimo osigurajte da ste prijavljeni kao pacijent i da je vaš profil pacijenta kreiran.');
                setBookingMessageType('danger');
                return;
            }

            const appointmentData = {
                pacijentID: pacijentId,
                doktorID: doctor.doktorID,
                terminID: selectedTerm.terminID,
                datumPregleda: selectedTerm.datum,
                vrijemePregleda: formattedVrijemePregleda, // Koristite formatirano vrijeme
                komentarPacijenta: reason,
                status: 'zakazan'
            };
            await axios.post(`${TERMINI_PREGLEDI_API_PREFIX}/api/pregledi`, appointmentData, memoizedAuthHeaders);
            setBookingMessage('Pregled uspješno zakazan!');
            setBookingMessageType('success');
            setReason('');
            setSelectedTermId('');
            fetchDoctorDetails(); // Ponovno učitavanje za osvježavanje liste dostupnih termina
            setLoading(true);
        } catch (err) {
            console.error("Greška pri zakazivanju pregleda:", err.response?.data || err.message);
            setBookingMessage(`Greška pri zakazivanju pregleda: ${err.response?.data?.message || err.message}`);
            setBookingMessageType('danger');
        }
    };

    if (authLoading || loading) {
        return (
            <div className="d-flex justify-content-center align-items-center" style={{ minHeight: '100vh' }}>
                <div className="spinner-border text-primary" role="status">
                    <span className="visually-hidden">Učitavanje...</span>
                </div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="container py-5 text-center">
                <p className="lead text-danger">{error}</p>
                <Link to="/service" className="btn btn-primary">Nazad na listu doktora</Link>
            </div>
        );
    }

    if (!doctor) {
        return (
            <div className="container py-5 text-center">
                <p className="lead">Doktor nije pronađen.</p>
                <Link to="/service" className="btn btn-primary">Nazad na listu doktora</Link>
            </div>
        );
    }

    const renderBookingSection = () => {
        if (!user) {
            return (
                <div className="alert alert-info text-center" role="alert">
                    Molimo <Link to="/login" className="alert-link">prijavite se</Link> ili <Link to="/sign-up" className="alert-link">registrujte se</Link> da biste zakazali pregled.
                </div>
            );
        } else if (!user.roles || !user.roles.includes('ROLE_PACIJENT')) {
            return (
                <div className="alert alert-warning text-center" role="alert">
                    Samo pacijenti mogu zakazivati preglede.
                </div>
            );
        } else {
            return (
                <form onSubmit={handleBookAppointment}>
                    <div className="row mb-3">
                        <div className="col-md-12">
                            <label htmlFor="appointmentTerm" className="form-label">Dostupni termini</label>
                            <select
                                className="form-select"
                                id="appointmentTerm"
                                value={selectedTermId}
                                onChange={handleTermChange}
                                required
                                disabled={availableTerms.length === 0}
                            >
                                <option value="">Odaberite termin</option>
                                {availableTerms.map(term => (
                                    <option key={term.terminID} value={term.terminID}>
                                        {term.display}
                                    </option>
                                ))}
                            </select>
                            {availableTerms.length === 0 && (
                                <small className="text-muted mt-2">Nema dostupnih termina za ovog doktora.</small>
                            )}
                        </div>
                    </div>
                    <div className="mb-3">
                        <label htmlFor="reason" className="form-label">Razlog za pregled</label>
                        <textarea
                            className="form-control"
                            id="reason"
                            rows="3"
                            value={reason}
                            onChange={handleReasonChange}
                            placeholder="Kratak opis razloga za pregled..."
                            required
                        ></textarea>
                    </div>
                    <button type="submit" className="btn btn-danger">Zakaži pregled</button>
                </form>
            );
        }
    };

    return (
        <div className="container my-5">
            <div className="card shadow-lg p-4 mb-4">
                <div className="row g-4">
                    <div className="col-md-4 text-center">
                        <img
                            src={doctor.profileImageBase64}
                            alt={doctor.fullName}
                            className="img-fluid rounded-circle shadow-sm mb-3"
                            style={{ width: '150px', height: '150px', objectFit: 'cover', border: '3px solid #dc3545' }}
                        />
                        <h3 className="mb-1">{doctor.fullName}</h3>
                        <p className="text-muted">{doctor.specijalizacije}</p>
                        <div className="d-flex justify-content-center align-items-center">
                            <span className="text-warning me-1">★</span>
                            <span className="fw-bold">{doctor.ocjena ? doctor.ocjena.toFixed(1) : 'N/A'}</span>
                            <span className="text-muted ms-2">({doctor.grad})</span>
                            <button
                                className="btn btn-outline-secondary btn-sm ms-3"
                                onClick={fetchDoctorDetails}
                                disabled={loading}
                            >
                                {loading ? 'Osvježavanje...' : 'Osvježi profil'}
                            </button>
                        </div>
                    </div>
                    <div className="col-md-8">
                        <h4 className="text-danger mb-3">Informacije o doktoru</h4>
                        <p><strong>Email:</strong> {doctor.email}</p>
                        <p><strong>Telefon:</strong> {doctor.telefon}</p>
                        <p><strong>Grad:</strong> {doctor.grad}</p>
                        <p><strong>Radno vrijeme:</strong> {doctor.radnoVrijeme}</p>
                        <p><strong>Godine iskustva:</strong> {doctor.iskustvo}</p>
                    </div>
                </div>
            </div>

            <div className="card shadow-lg p-4">
                <h4 className="text-danger mb-3">Zakažite pregled</h4>
                {bookingMessage && (
                    <div className={`alert alert-${bookingMessageType} mb-3`} role="alert">
                        {bookingMessage}
                    </div>
                )}
                {renderBookingSection()}
            </div>
        </div>
    );
}

export default DoctorProfilePage;
