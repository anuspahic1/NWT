import axios from 'axios';
import { useAuth } from '../contexts/AuthContext';
import 'bootstrap/dist/css/bootstrap.min.css';
import React, { useState, useEffect, useMemo } from 'react';
import { Modal, Button, Form, Alert, Spinner, Tab, Tabs } from 'react-bootstrap';
// Nema potrebe za react-datepickerom ako vaša originalna verzija ne koristi.

const GATEWAY_BASE_URL = 'http://localhost:8081';

// Definisanje API prefiksa za svaki mikroservis - KORISTIMO OVE PREFIKSE!
const KORISNICI_DOKTORI_API_PREFIX = `${GATEWAY_BASE_URL}/api/korisnici-doktori`;
const TERMINI_PREGLEDI_API_PREFIX = `${GATEWAY_BASE_URL}/api/termini-pregledi`;
const OBAVIJESTI_DOKUMENTI_API_PREFIX = `${GATEWAY_BASE_URL}/api/obavijesti-dokumentacija`;
const AUTH_API_PREFIX = `${GATEWAY_BASE_URL}/auth`; // Pretpostavljena putanja za autentifikaciju, ako se koristi direktno

function PatientDashboard() {
    // AŽURIRANO: user objekat sada sadrži authId (Long), id (email), patientId (Integer)
    const { token, user } = useAuth();
    console.log("Trenutni user objekat iz useAuth (PatientDashboard):", user);

    const authHeaders = useMemo(() => ({
        headers: {
            'Authorization': `Bearer ${token}`
        }
    }), [token]);

    const [activeTab, setActiveTab] = useState('overview');
    const [allAppointments, setAllAppointments] = useState([]); // Svi pregledi, bez obzira na status
    const [medicalHistory, setMedicalHistory] = useState([]);
    const [patientProfile, setPatientProfile] = useState({});
    const [doctors, setDoctors] = useState([]); // Ova lista doktora također treba sadržavati userId
    const [allDoctorAvailableTerms, setAllDoctorAvailableTerms] = useState([]);
    const [bills, setBills] = useState([]);
    const [messages, setMessages] = useState([]);
    const [newMessage, setNewMessage] = useState({ recipientId: '', subject: '', content: '' });
    const [replyMessage, setReplyMessage] = useState({ messageId: '', content: '' });
    const [selectedMessage, setSelectedMessage] = useState(null);

    const [showAlert, setShowAlert] = useState(false);
    const [alertMessage, setAlertMessage] = useState('');
    const [alertType, setAlertType] = useState('success');

    const [showAppointmentModal, setShowAppointmentModal] = useState(false);
    const [newAppointment, setNewAppointment] = useState({
        doctorId: '',
        appointmentDate: '',
        appointmentTime: '',
        terminId: '',
        reason: ''
    });
    const [editingAppointment, setEditingAppointment] = useState(null);

    const [loading, setLoading] = useState(true);

    // Stanje za modal za ocjenjivanje
    const [showRatingModal, setShowRatingModal] = useState(false);
    // Dodano: Čuvamo ID pregleda koji se ocjenjuje
    const [selectedAppointmentForRating, setSelectedAppointmentForRating] = useState(null);
    const [selectedDoctorForRating, setSelectedDoctorForRating] = useState(null);
    // AŽURIRANO: Uklonjen komentar iz ratingData
    const [ratingData, setRatingData] = useState({ ocjena: 5 }); // Podrazumijevana ocjena 5

    // NOVO: Stanje za modal detalja računa
    const [showBillDetailsModal, setShowBillDetailsModal] = useState(false);
    const [selectedBillDetails, setSelectedBillDetails] = useState(null);


    // --- API Pozivi ---

    // AŽURIRANA FUNKCIJA: Sada dohvaća SVE preglede za pacijenta
    const fetchPatientAppointments = async () => {
        if (!user || !user.pacijentId) {
            console.warn("ID pacijenta nije dostupan za dohvaćanje pregleda.");
            return;
        }
        try {
            // Predpostavljamo da ste dodali /api/pregledi/pacijent/{pacijentId} na backendu
            const response = await axios.get(`${TERMINI_PREGLEDI_API_PREFIX}/api/pregledi/pacijent/${user.pacijentId}`, authHeaders);
            const transformedAppointments = response.data.map(app => ({
                id: app.pregledID,
                doktorID: app.doktorID,
                pacijentID: app.pacijentID,
                datumPregleda: app.datumPregleda,
                vrijemePregleda: app.vrijemePregleda,
                komentarPacijenta: app.komentarPacijenta,
                status: app.status,
                terminID: app.terminID,
                doktorIme: app.doktorIme,
                // Ocjena doktora iz PregledDTO.
                // Ocijenjeno je ako ocjenaDoktora nije null niti undefined.
                // Vrijednost 0.0 je validna ocjena, pa je ne tretiramo kao "nije ocijenjeno".
                ocjenaDoktora: app.ocjenaDoktora, // Dohvati stvarnu ocjenu
                rated: typeof app.ocjenaDoktora === 'number' && app.ocjenaDoktora >= 1 && app.ocjenaDoktora <= 5
            }));
            setAllAppointments(transformedAppointments); // Spremamo sve preglede
            setAlertType('success');
            setShowAlert(false);
        } catch (error) {
            console.error('Greška pri dohvaćanju pacijentovih pregleda (svi):', error.response?.data || error.message);
            setAlertMessage('Nije uspjelo dohvaćanje pregleda.');
            setAlertType('danger');
            setShowAlert(true);
            setAllAppointments([]); // Osiguraj prazan niz u slučaju greške
        }
    };

    const fetchMedicalHistory = async () => {
        if (!user || !user.pacijentId) {
            console.warn("ID pacijenta nije dostupan za dohvaćanje medicinske historije.");
            return;
        }
        try {
            const response = await axios.get(`${KORISNICI_DOKTORI_API_PREFIX}/api/medicinska-historija/pacijent/${user.pacijentId}`, authHeaders);
            const transformedHistory = response.data.map(record => ({
                id: record.zapisID,
                patientId: record.pacijentID,
                doctorId: record.doktorID,
                doctorName: record.doktorIme,
                date: record.datumZapisivanja,
                diagnosis: record.dijagnoza,
                treatment: record.lijecenje,
                notes: record.napomene
            }));
            setMedicalHistory(transformedHistory);
            setAlertType('success');
            setShowAlert(false);
        } catch (error) {
            console.error('Greška pri dohvaćanju medicinske historije:', error.response?.data || error.message);
            setAlertMessage('Nije uspjelo dohvaćanje medicinske historije.');
            setAlertType('danger');
            setShowAlert(true);
        }
    };

    const fetchPatientProfile = async () => {
        if (!user || !user.pacijentId) {
            console.warn("ID pacijenta nije dostupan za dohvaćanje profila pacijenta.");
            return;
        }
        try {
            const response = await axios.get(`${KORISNICI_DOKTORI_API_PREFIX}/api/pacijenti/${user.pacijentId}`, authHeaders);
            setPatientProfile({
                id: response.data.pacijentID,
                firstName: response.data.ime,
                lastName: response.data.prezime,
                email: response.data.email,
                phone: response.data.telefon,
                userId: Number(response.data.userId)
            });
            setAlertType('success');
            setShowAlert(false);
        } catch (error) {
            console.error('Greška pri dohvaćanju profila pacijenta:', error.response?.data || error.message);
            setAlertMessage('Nije uspjelo dohvaćanje podataka profila.');
            setAlertType('danger');
            setShowAlert(true);
        }
    };

    const fetchDoctors = async () => {
        try {
            const response = await axios.get(`${KORISNICI_DOKTORI_API_PREFIX}/api/doktori`, authHeaders);
            const transformedDoctors = response.data.map(doc => ({
                id: doc.doktorID,
                userId: Number(doc.userId),
                firstName: doc.ime,
                lastName: doc.prezime,
                specialty: Array.isArray(doc.specijalizacije) ? doc.specijalizacije.join(', ') : doc.specijalizacije,
                email: doc.email,
                phone: doc.telefon,
                ocjena: doc.ocjena
            }));
            setDoctors(transformedDoctors);
            setAlertType('success');
            setShowAlert(false);
        } catch (error) {
            console.error('Greška pri dohvaćanju doktora:', error.response?.data || error.message);
            setAlertMessage('Nije uspjelo dohvaćanje liste doktora.');
            setAlertType('danger');
            setShowAlert(true);
        }
    };

    const fetchDoctorAllAvailableTerms = async (doktorId) => {
        if (!doktorId) {
            setAllDoctorAvailableTerms([]);
            return;
        }
        setLoading(true);
        try {
            const response = await axios.get(`${TERMINI_PREGLEDI_API_PREFIX}/api/termini/slobodni/${doktorId}`, authHeaders);
            const termsData = Array.isArray(response.data) ? response.data : [];
            const available = termsData
                .map(term => ({
                    terminID: term.terminID,
                    datum: term.datum,
                    vrijeme: term.vrijeme,
                    display: `${term.datum} ${term.vrijeme.substring(0, 5)}`
                }))
                .sort((a, b) => {
                    const dateTimeA = `${a.datum}T${a.vrijeme}`;
                    const dateTimeB = `${b.datum}T${b.vrijeme}`;
                    return new Date(dateTimeA) - new Date(dateTimeB);
                });
            setAllDoctorAvailableTerms(available);
            setAlertType('success');
            setShowAlert(false);
        } catch (error) {
            console.error('Greška pri dohvaćanju dostupnosti doktora:', error.response?.data || error.message);
            setAlertMessage('Nije uspjelo dohvaćanje dostupnosti doktora.');
            setAlertType('danger');
            setShowAlert(true);
            setAllDoctorAvailableTerms([]);
        } finally {
            setLoading(false);
        }
    };

    const fetchBills = async () => {
        if (!user || !user.pacijentId) {
            console.warn("ID pacijenta nije dostupan za dohvaćanje računa.");
            return;
        }
        try {
            const response = await axios.get(`${OBAVIJESTI_DOKUMENTI_API_PREFIX}/api/racuni/pacijent/${user.pacijentId}`, authHeaders);
            const transformedBills = response.data.map(bill => ({
                id: bill.racunID,
                billDate: bill.datumIzdavanja,
                amount: bill.iznos,
                status: bill.status,
                dueDate: bill.datumDospijeca,
                opis: bill.opis,
                doktorIme: bill.doktorIme
            }));
            setBills(transformedBills);
            setAlertType('success');
            setShowAlert(false);
        } catch (error) {
            console.error('Greška pri dohvaćanju računa:', error.response?.data || error.message);
            setAlertMessage('Nije uspjelo dohvaćanje informacija o naplati.');
            setAlertType('danger');
            setShowAlert(true);
        }
    };

    const fetchMessages = async () => {
        if (!user || !user.userId) {
            console.warn("ID korisnika nije dostupan za dohvaćanje poruka.");
            return;
        }
        try {
            const response = await axios.get(`${OBAVIJESTI_DOKUMENTI_API_PREFIX}/api/poruke/konverzacija/${user.userId}?userType=PACIJENT`, authHeaders);
            const transformedMessages = response.data.map(msg => ({
                id: msg.porukaID,
                senderId: Number(msg.senderId),
                senderType: msg.senderType,
                senderIme: msg.senderIme,
                receiverId: Number(msg.receiverId),
                receiverType: msg.receiverType,
                receiverIme: msg.receiverIme,
                subject: msg.subject,
                content: msg.content,
                timestamp: msg.timestamp,
                replies: msg.replies || []
            }));
            setMessages(transformedMessages);
            setAlertType('success');
            setShowAlert(false);
        } catch (error) {
            console.error('Greška pri dohvaćanju poruka:', error.response?.data || error.message);
            setAlertMessage('Nije uspjelo dohvaćanje poruka.');
            setAlertType('danger');
            setShowAlert(true);
        }
    };

    // --- useEffect Hookovi ---
    useEffect(() => {
        const loadAllData = async () => {
            if (token && user) {
                setLoading(true);
                await Promise.all([
                    fetchPatientAppointments(),
                    fetchMedicalHistory(),
                    fetchPatientProfile(),
                    fetchDoctors(),
                    fetchBills(),
                    fetchMessages()
                ]);
                setLoading(false);
            } else if (!token || (user && !user.pacijentId && !user.doktorId)) {
                setLoading(false);
                if (!token) {
                    console.warn("Token nije dostupan. Korisnik nije prijavljen.");
                } else {
                    console.warn("User objekat je dostupan, ali patientId ni doktorId nisu. Korisnik možda nije ni pacijent ni doktor.");
                }
            }
        };
        loadAllData();
    }, [token, user]);


    // --- Zakazivanje/Otkazivanje pregleda ---

    const openAppointmentModal = (appointment = null, doctorIdFromSearch = null) => {
        if (appointment) {
            setEditingAppointment(appointment);
            setNewAppointment({
                doctorId: appointment.doktorID,
                appointmentDate: appointment.datumPregleda,
                appointmentTime: appointment.vrijemePregleda,
                terminId: appointment.terminID,
                reason: appointment.komentarPacijenta
            });
            fetchDoctorAllAvailableTerms(appointment.doktorID);
        } else {
            setEditingAppointment(null);
            setNewAppointment({
                doctorId: doctorIdFromSearch || '',
                appointmentDate: '',
                appointmentTime: '',
                terminId: '',
                reason: ''
            });
            setAllDoctorAvailableTerms([]);
            if (doctorIdFromSearch) {
                fetchDoctorAllAvailableTerms(doctorIdFromSearch);
            }
        }
        setShowAppointmentModal(true);
    };

    const closeAppointmentModal = () => {
        setShowAppointmentModal(false);
        setEditingAppointment(null);
        setNewAppointment({
            doctorId: '',
            appointmentDate: '',
            appointmentTime: '',
            terminId: '',
            reason: ''
        });
        setAllDoctorAvailableTerms([]);
    };

    const handleNewAppointmentChange = async (e) => {
        const { name, value } = e.target;

        let updatedAppointment = { ...newAppointment, [name]: value };

        if (name === 'doctorId') {
            updatedAppointment.appointmentDate = '';
            updatedAppointment.appointmentTime = '';
            updatedAppointment.terminId = '';
            if (value) {
                await fetchDoctorAllAvailableTerms(value);
            } else {
                setAllDoctorAvailableTerms([]);
            }
        } else if (name === 'terminId') {
            const selectedTerm = allDoctorAvailableTerms.find(term => term.terminID === parseInt(value));
            if (selectedTerm) {
                updatedAppointment.terminId = selectedTerm.terminID;
                updatedAppointment.appointmentDate = selectedTerm.datum;
                updatedAppointment.appointmentTime = selectedTerm.vrijeme;
            } else {
                updatedAppointment.terminId = '';
                updatedAppointment.appointmentTime = '';
                updatedAppointment.appointmentDate = '';
            }
        }
        setNewAppointment(updatedAppointment);
    };


    const handleScheduleAppointment = async () => {
        if (!user || !user.pacijentId) {
            setAlertMessage('ID pacijenta nije dostupan. Nije moguće zakazati pregled.');
            setAlertType('danger');
            setShowAlert(true);
            return;
        }

        if (!newAppointment.doctorId || !newAppointment.terminId || !newAppointment.reason) {
            setAlertMessage('Molimo odaberite doktora, dostupan termin i navedite razlog.');
            setAlertType('warning');
            setShowAlert(true);
            return;
        }

        const appointmentData = {
            pacijentID: user.pacijentId,
            doktorID: parseInt(newAppointment.doctorId),
            terminID: newAppointment.terminId,
            datumPregleda: newAppointment.appointmentDate,
            vrijemePregleda: newAppointment.appointmentTime,
            komentarPacijenta: newAppointment.reason,
            status: editingAppointment ? editingAppointment.status : 'zakazan',
        };

        try {
            await axios.post(`${TERMINI_PREGLEDI_API_PREFIX}/api/pregledi`, appointmentData, authHeaders);

            // KLJUČNA IZMJENA: Promjena poruke o uspjehu
            setAlertMessage('Pregled uspješno zakazan!');
            setAlertType('success'); // Promijenjen tip na 'success'
            setShowAlert(true);

            closeAppointmentModal();
            fetchPatientAppointments(); // Osvježi sve preglede
        } catch (error) {
            console.error('Greška pri zakazivanju/ažuriranju pregleda:', error.response?.data || error.message);
            let errorMessage = 'Nije uspjelo zakazivanje/ažuriranje pregleda. Molimo pokušajte ponovo.';
            if (error.response?.data?.message) {
                errorMessage = error.response.data.message;
            } else if (error.message) {
                errorMessage = error.message;
            }
            setAlertMessage(errorMessage);
            setAlertType('danger');
            setShowAlert(true);
        }
    };

    const handleCancelAppointment = async (appointmentId) => {
        if (window.confirm('Da li ste sigurni da želite otkazati ovaj pregled?')) {
            if (!user || !user.pacijentId) {
                setAlertMessage('ID pacijenta nije dostupan za otkazivanje. Molimo pokušajte ponovo.');
                setAlertType('danger');
                setShowAlert(true);
                return;
            }
            try {
                const existingApp = allAppointments.find(app => app.id === appointmentId);
                if (!existingApp) {
                    setAlertMessage("Pregled nije pronađen za otkazivanje.");
                    setAlertType('danger');
                    setShowAlert(true);
                    return;
                }

                const cancelPayload = {
                    pregledID: existingApp.id,
                    pacijentID: user.pacijentId,
                    doktorID: existingApp.doktorID,
                    terminID: existingApp.terminID,
                    datumPregleda: existingApp.datumPregleda,
                    vrijemePregleda: existingApp.vrijemePregleda,
                    komentarPacijenta: existingApp.komentarPacijenta,
                    status: 'otkazan'
                };

                await axios.put(`${TERMINI_PREGLEDI_API_PREFIX}/api/pregledi/${appointmentId}`, cancelPayload, authHeaders);
                setAlertMessage('Pregled uspješno otkazan!');
                setAlertType('success');
                setShowAlert(true);
                fetchPatientAppointments(); // Osvježi sve preglede
            } catch (error) {
                console.error('Greška pri otkazivanju pregleda:', error.response?.data || error.message);
                setAlertMessage('Nije uspjelo otkazivanje pregleda. Molimo pokušajte ponovo. ' + (error.response?.data?.message || error.message));
                setAlertType('danger');
                setShowAlert(true);
            }
        }
    };

    // --- Ažuriranje profila ---
    const handleProfileChange = (e) => {
        const { name, value } = e.target;
        setPatientProfile(prev => ({ ...prev, [name]: value }));
    };

    const handleSaveProfile = async () => {
        if (!user || !user.pacijentId || !user.userId) {
            setAlertMessage('ID pacijenta ili ID korisnika nije dostupan. Nije moguće sačuvati profil.');
            setAlertType('danger');
            setShowAlert(true);
            return;
        }

        try {
            const patientDetailsPayload = {
                pacijentID: user.pacijentId,
                ime: patientProfile.firstName,
                prezime: patientProfile.lastName,
                email: patientProfile.email,
                telefon: patientProfile.phone,
                userId: user.userId
            };
            await axios.put(`${KORISNICI_DOKTORI_API_PREFIX}/api/pacijenti/${user.pacijentId}`, patientDetailsPayload, authHeaders);

            const authProfilePayload = {
                email: patientProfile.email,
                fullName: `${patientProfile.firstName} ${patientProfile.lastName}`,
                telefon: patientProfile.phone,
            };
            await axios.put(`${AUTH_API_PREFIX}/users/${user.userId}`, authProfilePayload, authHeaders);

            setAlertMessage('Profil uspješno ažuriran!');
            setAlertType('success');
            setShowAlert(true);
            fetchPatientProfile();
        } catch (error) {
            console.error('Greška pri ažuriranju profila:', error.response?.data || error.message);
            setAlertMessage('Nije uspjelo ažuriranje profila. Molimo pokušajte ponovo. ' + (error.response?.data?.message || error.message));
            setAlertType('danger');
            setShowAlert(true);
        }
    };

    // --- Sigurno slanje poruka ---
    const handleNewMessageChange = (e) => {
        const { name, value } = e.target;
        setNewMessage(prev => ({ ...prev, [name]: value }));
    };

    const handleReplyMessageChange = (e) => {
        setReplyMessage(prev => ({ ...prev, content: e.target.value }));
    };

    const handleSendMessage = async () => {
        if (!user || !user.userId) {
            setAlertMessage('Vaš ID korisnika nije dostupan. Nije moguće poslati poruku.');
            setAlertType('danger');
            setShowAlert(true);
            return;
        }
        if (!newMessage.recipientId || !newMessage.subject || !newMessage.content) {
            setAlertMessage('Molimo popunite sva polja poruke.');
            setAlertType('warning');
            setShowAlert(true);
            return;
        }

        try {
            const messageData = {
                senderId: user.userId,
                senderType: 'PACIJENT',
                receiverId: Number(newMessage.recipientId),
                receiverType: 'DOKTOR',
                subject: newMessage.subject,
                content: newMessage.content
            };
            await axios.post(`${OBAVIJESTI_DOKUMENTI_API_PREFIX}/api/poruke/posalji`, messageData, authHeaders);
            setAlertMessage('Poruka uspješno poslana!');
            setAlertType('success');
            setShowAlert(true);
            setNewMessage({ recipientId: '', subject: '', content: '' });
            fetchMessages();
        } catch (error) {
            console.error('Greška pri slanju poruke:', error.response?.data || error.message);
            setAlertMessage('Nije uspjelo slanje poruke. ' + (error.response?.data?.message || error.message));
            setAlertType('danger');
            setShowAlert(true);
        }
    };

    const handleReply = async (messageId) => {
        if (!user || !user.userId) {
            setAlertMessage('Vaš ID korisnika nije dostupan. Nije moguće odgovoriti.');
            setAlertType('danger');
            setShowAlert(true);
            return;
        }
        if (!replyMessage.content) {
            setAlertMessage('Sadržaj odgovora ne može biti prazan.');
            setAlertType('warning');
            setShowAlert(true);
            return;
        }
        try {
            await axios.post(`${OBAVIJESTI_DOKUMENTI_API_PREFIX}/api/poruke/${messageId}/odgovori`, {
                senderId: user.userId,
                senderIme: user.fullName,
                senderType: 'PACIJENT',
                content: replyMessage.content,
                timestamp: new Date().toISOString()
            }, authHeaders);
            setAlertMessage('Odgovor uspješno poslan!', 'success');
            setAlertType('success');
            setShowAlert(true);
            setReplyMessage({ messageId: '', content: '' });
            fetchMessages();
            setSelectedMessage(prev => {
                if (!prev) return null;
                return {
                    ...prev,
                    replies: [...(prev.replies || []), {
                        senderId: user.userId,
                        senderIme: user.fullName,
                        senderType: 'PACIJENT',
                        content: replyMessage.content,
                        timestamp: new Date().toISOString()
                    }]
                };
            });
        } catch (error) {
            console.error('Greška pri slanju odgovora:', error.response?.data || error.message);
            setAlertMessage('Nije uspjelo slanje odgovora. ' + (error.response?.data?.message || error.message));
            setAlertType('danger');
            setShowAlert(true);
        }
    };

    const handleViewMessage = (message) => {
        setSelectedMessage(message);
    };

    const closeMessageModal = () => {
        setSelectedMessage(null);
        setReplyMessage({ messageId: '', content: '' });
    };

    // --- Funkcije ocjenjivanja ---
    // Ažurirano: Sada prima i appointmentId
    const openRatingModal = (doktorID, doktorIme, appointmentId) => {
        setSelectedDoctorForRating({ id: doktorID, name: doktorIme });
        setSelectedAppointmentForRating(appointmentId); // Čuvamo ID pregleda
        // AŽURIRANO: Postavljamo samo ocjenu, komentar je uklonjen
        setRatingData({ ocjena: 5 }); // Podrazumijevana ocjena 5
        setShowRatingModal(true);
    };

    const closeRatingModal = () => {
        setShowRatingModal(false);
        setSelectedDoctorForRating(null);
        setSelectedAppointmentForRating(null); // Resetujemo ID pregleda
        // AŽURIRANO: Postavljamo samo ocjenu, komentar je uklonjen
        setRatingData({ ocjena: 5 });
    };

    const handleRatingChange = (e) => {
        const { name, value } = e.target;
        setRatingData(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmitRating = async () => {
        if (!user || !user.pacijentId) {
            setAlertMessage('ID pacijenta nije dostupan. Nije moguće poslati ocjenu.');
            setAlertType('danger');
            setShowAlert(true);
            return;
        }
        if (!selectedDoctorForRating || ratingData.ocjena === null) {
            setAlertMessage('Molimo odaberite ocjenu.');
            setAlertType('warning');
            setShowAlert(true);
            return;
        }
        if (!selectedAppointmentForRating) { // Provjera da li je pregled ID prisutan
            setAlertMessage('Nije moguće poslati ocjenu bez ID-a pregleda.');
            setAlertType('danger');
            setShowAlert(true);
            return;
        }

        try {
            const ratingPayload = {
                pacijentID: user.pacijentId,
                doktorID: selectedDoctorForRating.id,
                ocjena: parseFloat(ratingData.ocjena),
                pregledID: selectedAppointmentForRating // <-- DODANO: Slanje pregledID-a
                // UKLONJENO: komentar: ratingData.komentar
            };

            await axios.post(`${KORISNICI_DOKTORI_API_PREFIX}/api/ocjene`, ratingPayload, authHeaders);

            setAlertMessage(`Ocjena uspješno poslana za doktora ${selectedDoctorForRating.name}! Prosječna ocjena će biti ažurirana.`);
            setAlertType('success');
            setShowAlert(true);
            closeRatingModal();

            // Važno: Osvježi SVE preglede da bi se povukla nova vrijednost ocjeneDoktora iz backend DTO-a
            // i ispravno ažurirao 'rated' status na frontendu.
            fetchPatientAppointments();
            fetchDoctors(); // Osvježi listu doktora (da bi se ažurirala prosječna ocjena)

        } catch (error) {
            console.error('Greška pri slanju ocjene:', error.response?.data || error.message);
            let errorMessage = 'Nije uspjelo slanje ocjene. Molimo pokušajte ponovo.';
            // AŽURIRANO: Provjera za 409 Conflict status - "Ovaj pregled je već ocijenjen"
            if (error.response?.status === 409) {
                errorMessage = "Ovaj pregled je već ocijenjen od strane vas.";
            } else if (error.response?.data?.message) {
                errorMessage = error.response.data.message;
            } else if (error.message) {
                errorMessage = error.message;
            }
            setAlertMessage(errorMessage);
            setAlertType('danger');
            setShowAlert(true);
        }
    };

    // NOVO: Funkcionalnost "Plati sada"
    const handlePayNow = async (billId) => {
        if (window.confirm('Da li ste sigurni da želite označiti ovaj račun kao plaćen?')) {
            try {
                // Poziv backend endpointa za ažuriranje statusa računa
                await axios.put(`${OBAVIJESTI_DOKUMENTI_API_PREFIX}/api/racuni/${billId}/oznaci-kao-placeno`, {}, authHeaders);
                setAlertMessage('Račun uspješno označen kao plaćen!');
                setAlertType('success');
                setShowAlert(true);
                fetchBills(); // Osvježi listu računa
            } catch (error) {
                console.error('Greška pri označavanju računa kao plaćenog:', error.response?.data || error.message);
                setAlertMessage('Nije uspjelo označavanje računa kao plaćenog. ' + (error.response?.data?.message || error.message));
                setAlertType('danger');
                setShowAlert(true);
            }
        }
    };

    // NOVO: Funkcionalnost "Pregledaj detalje"
    const handleViewBillDetails = (bill) => {
        setSelectedBillDetails(bill);
        setShowBillDetailsModal(true);
    };

    const closeBillDetailsModal = () => {
        setShowBillDetailsModal(false);
        setSelectedBillDetails(null);
    };


    if (loading) {
        return (
            <div className="d-flex justify-content-center align-items-center" style={{ minHeight: '100vh' }}>
                <Spinner animation="border" role="status">
                    <span className="visually-hidden">Učitavanje...</span>
                </Spinner>
                <p className="ms-2">Učitavanje podataka nadzorne ploče...</p>
            </div>
        );
    }

    const currentPatientUserId = user.userId;

    // Filtriranje pregleda za prikaz u različitim tabovima
    const upcomingAppointments = allAppointments.filter(app =>
        app.status === 'zakazan' || app.status === 'potvrđen'
    ).sort((a, b) => new Date(a.datumPregleda + 'T' + a.vrijemePregleda) - new Date(b.datumPregleda + 'T' + b.vrijemePregleda));

    const pastAppointments = allAppointments.filter(app =>
        app.status === 'obavljen' || app.status === 'otkazan'
    ).sort((a, b) => new Date(b.datumPregleda + 'T' + b.vrijemePregleda) - new Date(a.datumPregleda + 'T' + a.vrijemePregleda)); // Najnoviji prvi


    return (
        <div className="d-flex" style={{ minHeight: '100vh' }}>
            {/* Bočna traka */}
            <div className="bg-dark text-white p-3" style={{ width: '250px' }}>
                <h4 className="mb-4">Dashboard</h4>
                <ul className="nav flex-column">
                    <li className="nav-item mb-2">
                        <button
                            className={`btn btn-link text-white text-decoration-none w-100 text-start ${activeTab === 'overview' ? 'active bg-secondary rounded' : ''}`}
                            onClick={() => setActiveTab('overview')}
                        >
                            Pregled
                        </button>
                    </li>
                    <li className="nav-item mb-2">
                        <button
                            className={`btn btn-link text-white text-decoration-none w-100 text-start ${activeTab === 'myAppointments' ? 'active bg-secondary rounded' : ''}`}
                            onClick={() => setActiveTab('myAppointments')}
                        >
                            Moji nadolazeći pregledi
                        </button>
                    </li>
                    {/* NOVI TAB: Prošli pregledi */}
                    <li className="nav-item mb-2">
                        <button
                            className={`btn btn-link text-white text-decoration-none w-100 text-start ${activeTab === 'pastAppointments' ? 'active bg-secondary rounded' : ''}`}
                            onClick={() => setActiveTab('pastAppointments')}
                        >
                            Prošli pregledi
                        </button>
                    </li>
                    <li className="nav-item mb-2">
                        <button
                            className={`btn btn-link text-white text-decoration-none w-100 text-start ${activeTab === 'medicalHistory' ? 'active bg-secondary rounded' : ''}`}
                            onClick={() => setActiveTab('medicalHistory')}
                        >
                            Medicinska historija
                        </button>
                    </li>
                    <li className="nav-item mb-2">
                        <button
                            className={`btn btn-link text-white text-decoration-none w-100 text-start ${activeTab === 'billing' ? 'active bg-secondary rounded' : ''}`}
                            onClick={() => setActiveTab('billing')}
                        >
                            Naplata i plaćanja
                        </button>
                    </li>
                    <li className="nav-item mb-2">
                        <button
                            className={`btn btn-link text-white text-decoration-none w-100 text-start ${activeTab === 'messages' ? 'active bg-secondary rounded' : ''}`}
                            onClick={() => setActiveTab('messages')}
                        >
                            Poruke
                        </button>
                    </li>
                    <li className="nav-item mb-2">
                        <button
                            className={`btn btn-link text-white text-decoration-none w-100 text-start ${activeTab === 'doctorSearch' ? 'active bg-secondary rounded' : ''}`}
                            onClick={() => setActiveTab('doctorSearch')}
                        >
                            Pronađi doktora
                        </button>
                    </li>
                    <li className="nav-item mb-2">
                        <button
                            className={`btn btn-link text-white text-decoration-none w-100 text-start ${activeTab === 'profileSettings' ? 'active bg-secondary rounded' : ''}`}
                            onClick={() => setActiveTab('profileSettings')}
                        >
                            Postavke profila
                        </button>
                    </li>
                </ul>
            </div>

            {/* Glavni sadržaj */}
            <div className="flex-grow-1 p-4 bg-light">
                {showAlert && (
                    <Alert variant={alertType} onClose={() => setShowAlert(false)} dismissible className="position-fixed top-0 start-50 translate-middle-x mt-3" style={{ zIndex: 1050 }}>
                        {alertMessage}
                    </Alert>
                )}

                <div className="d-flex justify-content-between align-items-center mb-4">
                    <h2 className="mb-0">Dashboard</h2>
                </div>

                {/* Sadržaj na osnovu aktivnog taba */}
                {activeTab === 'overview' && (
                    <div>
                        <h3>Dobrodošli, {patientProfile.firstName || (user ? user.fullName : 'Pacijent')}!</h3>
                        <p>Ovo je vaš pregled pacijenta. Ovdje možete brzo vidjeti svoje nadolazeće preglede i nedavne aktivnosti.</p>
                        <div className="row mt-4">
                            <div className="col-md-6">
                                <div className="card">
                                    <div className="card-header">
                                        Nadolazeći pregledi
                                    </div>
                                    <ul className="list-group list-group-flush">
                                        {upcomingAppointments.length > 0 ? (
                                            upcomingAppointments.map(app => (
                                                <li key={app.id} className="list-group-item d-flex justify-content-between align-items-center">
                                                    <div>
                                                        <strong>{app.datumPregleda} u {app.vrijemePregleda}</strong>
                                                        <br />
                                                        <small>Razlog: {app.komentarPacijenta}</small>
                                                        <br />
                                                        <small>Doktor: {app.doktorIme}</small>
                                                    </div>
                                                    <span className={`badge bg-${app.status === 'potvrđen' ? 'success' : 'warning'}`}>{app.status}</span>
                                                </li>
                                            ))
                                        ) : (
                                            <li className="list-group-item">Nema nadolazećih pregleda.</li>
                                        )}
                                    </ul>
                                </div>
                            </div>
                        </div>
                    </div>
                )}

                {activeTab === 'myAppointments' && (
                    <div>
                        <h3>Moji nadolazeći pregledi</h3>
                        <Button variant="primary" className="mb-3" onClick={() => openAppointmentModal()}>
                            Zakaži novi pregled
                        </Button>
                        {upcomingAppointments.length > 0 ? (
                            <table className="table table-striped">
                                <thead>
                                    <tr>
                                        <th>Doktor</th>
                                        <th>Datum</th>
                                        <th>Vrijeme</th>
                                        <th>Razlog</th>
                                        <th>Status</th>
                                        <th>Akcije</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {upcomingAppointments.map(app => (
                                        <tr key={app.id}>
                                            <td>{app.doktorIme}</td>
                                            <td>{app.datumPregleda}</td>
                                            <td>{app.vrijemePregleda}</td>
                                            <td>{app.komentarPacijenta}</td>
                                            <td><span className={`badge bg-${app.status === 'zakazan' ? 'warning' : 'info'}`}>{app.status}</span></td>
                                            <td>
                                                <Button variant="danger" size="sm" onClick={() => handleCancelAppointment(app.id)}>Otkaži</Button>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        ) : (
                            <p>Nema pronađenih nadolazećih pregleda.</p>
                        )}
                    </div>
                )}

                {activeTab === 'pastAppointments' && (
                    <div>
                        <h3>Prošli pregledi</h3>
                        {pastAppointments.length > 0 ? (
                            <table className="table table-striped">
                                <thead>
                                    <tr>
                                        <th>Doktor</th>
                                        <th>Datum</th>
                                        <th>Vrijeme</th>
                                        <th>Razlog</th>
                                        <th>Status</th>
                                        <th>Akcije</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {pastAppointments.map(app => (
                                        <tr key={app.id}>
                                            <td>{app.doktorIme}</td>
                                            <td>{app.datumPregleda}</td>
                                            <td>{app.vrijemePregleda}</td>
                                            <td>{app.komentarPacijenta}</td>
                                            <td><span className={`badge bg-${app.status === 'obavljen' ? 'success' : 'danger'}`}>{app.status}</span></td>
                                            <td>
                                                {/* Ažurirana logika prikaza dugmeta "Ocjeni" i statusa "Ocijenjeno" */}
                                                {app.status === 'obavljen' && !app.rated ? (
                                                    <Button variant="success" size="sm" onClick={() => openRatingModal(app.doktorID, app.doktorIme, app.id)}>Ocjeni</Button>
                                                ) : app.status === 'obavljen' && app.rated ? (
                                                    // Ispravka: Prikazati toFixed(1) samo ako je ocjena broj
                                                    <small className="text-muted">Ocijenjeno ({typeof app.ocjenaDoktora === 'number' ? app.ocjenaDoktora.toFixed(1) : 'N/A'})</small>
                                                ) : app.status === 'otkazan' ? (
                                                    <small className="text-muted">Otkazan</small>
                                                ) : (
                                                    <small>Nema dostupnih akcija</small>
                                                )}
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        ) : (
                            <p>Nema pronađenih prošlih pregleda.</p>
                        )}
                    </div>
                )}

                {activeTab === 'medicalHistory' && (
                    <div>
                        <h3>Moja medicinska historija</h3>
                        {medicalHistory.length > 0 ? (
                            <ul className="list-group">
                                {medicalHistory.map((record, index) => (
                                    <li key={record.id} className="list-group-item">
                                        <strong>Datum:</strong> {record.date} <br />
                                        <strong>Dijagnoza:</strong> {record.diagnosis} <br />
                                        <strong>Tretman:</strong> {record.treatment} <br />
                                        <strong>Doktor:</strong> {record.doctorName} <br />
                                        <strong>Bilješke:</strong> {record.notes || 'N/A'}
                                    </li>
                                ))}
                            </ul>
                        ) : (
                            <p>Nema pronađenih zapisa medicinske historije.</p>
                        )}
                    </div>
                )}

                {activeTab === 'billing' && (
                    <div>
                        <h3>Naplata i plaćanja</h3>
                        {bills.length > 0 ? (
                            <table className="table table-striped">
                                <thead>
                                    <tr>
                                        <th>ID računa</th>
                                        <th>Datum izdavanja</th>
                                        <th>Iznos</th>
                                        <th>Status</th>
                                        <th>Opis</th>
                                        <th>Izdao</th>
                                        <th>Akcije</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {bills.map(bill => (
                                        <tr key={bill.id}>
                                            <td>{bill.id}</td>
                                            <td>{new Date(bill.billDate).toLocaleDateString()}</td>
                                            <td>{bill.amount ? bill.amount.toFixed(2) : 'N/A'} KM</td>
                                            <td><span className={`badge bg-${bill.status === 'PLACEN' ? 'success' : 'warning'}`}>{bill.status}</span></td>
                                            <td>{bill.opis}</td>
                                            <td>{bill.doktorIme}</td>
                                            <td>
                                                {bill.status === 'NEPLACEN' && (
                                                    <Button variant="success" size="sm" className="me-2" onClick={() => handlePayNow(bill.id)}>Plati sada</Button>
                                                )}
                                                <Button variant="info" size="sm" onClick={() => handleViewBillDetails(bill)}>Pregledaj detalje</Button>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        ) : (
                            <p>Nema pronađenih zapisa o naplati.</p>
                        )}
                    </div>
                )}

                {activeTab === 'messages' && (
                    <div>
                        <h3>Moje poruke</h3>
                        <Button variant="primary" className="mb-3" onClick={() => setNewMessage({ recipientId: '', subject: '', content: '' })}>Napiši novu poruku</Button>

                        {messages.length > 0 ? (
                            <ul className="list-group">
                                {messages.map(msg => (
                                    <li key={msg.id} className="list-group-item d-flex justify-content-between align-items-center">
                                        <div>
                                            {/* Prikazuje ime pošiljatelja poruke (Auth userId), provjerava je li trenutni korisnik pacijent ili doktor */}
                                            <strong>Od: {msg.senderId === currentPatientUserId ? 'Ja' : (doctors.find(d => d.userId === msg.senderId)?.firstName + ' ' + doctors.find(d => d.userId === msg.senderId)?.lastName || `Doktor (${msg.senderId})`)}</strong> <br />
                                            <strong>Naslov:</strong> {msg.subject} <br />
                                            <small>{new Date(msg.timestamp).toLocaleString()}</small>
                                        </div>
                                        <Button variant="info" size="sm" onClick={() => handleViewMessage(msg)}>Pregledaj poruku</Button>
                                    </li>
                                ))}
                            </ul>
                        ) : (
                            <p>Nema pronađenih poruka.</p>
                        )}

                        <hr className="my-4" />

                        <h4>Napiši novu poruku</h4>
                        <Form>
                            <Form.Group className="mb-3" controlId="formRecipient">
                                <Form.Label>Primalac (Doktor)</Form.Label>
                                <Form.Control
                                    as="select"
                                    name="recipientId"
                                    value={newMessage.recipientId}
                                    onChange={handleNewMessageChange}
                                    required
                                >
                                    <option value="">-- Odaberite doktora --</option>
                                    {doctors.map(doctor => (
                                        <option key={doctor.userId} value={doctor.userId}>{doctor.firstName} {doctor.lastName}</option>
                                    ))}
                                </Form.Control>
                            </Form.Group>
                            <Form.Group className="mb-3" controlId="formSubject">
                                <Form.Label>Naslov</Form.Label>
                                <Form.Control
                                    type="text"
                                    name="subject"
                                    value={newMessage.subject}
                                    onChange={handleNewMessageChange}
                                    required
                                />
                            </Form.Group>
                            <Form.Group className="mb-3" controlId="formContent">
                                <Form.Label>Sadržaj</Form.Label>
                                <Form.Control
                                    as="textarea"
                                    rows={3}
                                    name="content"
                                    value={newMessage.content}
                                    onChange={handleNewMessageChange}
                                    required
                                />
                            </Form.Group>
                            <Button variant="primary" onClick={handleSendMessage}>Pošalji poruku</Button>
                        </Form>
                    </div>
                )}

                {activeTab === 'doctorSearch' && (
                    <div>
                        <h3>Pronađi doktora</h3>
                        <p>Pretražite doktore po imenu ili specijalnosti kako biste pronašli pravu njegu za vas.</p>
                        <Form.Group className="mb-3">
                            <Form.Control type="text" placeholder="Pretražite po imenu ili specijalnosti..." />
                        </Form.Group>
                        {doctors.length > 0 ? (
                            <table className="table table-striped">
                                <thead>
                                    <tr>
                                        <th>Ime</th>
                                        <th>Specijalnost</th>
                                        <th>Kontakt</th>
                                        <th>Ocjena</th>
                                        <th>Akcije</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {doctors.map(doctor => (
                                        <tr key={doctor.id}>
                                            <td>{doctor.firstName} {doctor.lastName}</td>
                                            <td>{doctor.specialty || 'Opšti praktičar'}</td>
                                            <td>{doctor.email} | {doctor.phone}</td>
                                            <td>{doctor.ocjena ? doctor.ocjena.toFixed(1) : 'N/A'}</td>
                                            <td>
                                                <Button variant="primary" size="sm" onClick={() => {
                                                    openAppointmentModal(null, doctor.id);
                                                }}>Zakaži pregled</Button>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        ) : (
                            <p>Nije pronađen nijedan doktor.</p>
                        )}
                    </div>
                )}

                {activeTab === 'profileSettings' && (
                    <div>
                        <h3>Postavke profila</h3>
                        <Form>
                            <Form.Group className="mb-3" controlId="formFirstName">
                                <Form.Label>Ime</Form.Label>
                                <Form.Control
                                    type="text"
                                    name="firstName"
                                    value={patientProfile.firstName || ''}
                                    onChange={handleProfileChange}
                                />
                            </Form.Group>
                            <Form.Group className="mb-3" controlId="formLastName">
                                <Form.Label>Prezime</Form.Label>
                                <Form.Control
                                    type="text"
                                    name="lastName"
                                    value={patientProfile.lastName || ''}
                                    onChange={handleProfileChange}
                                />
                            </Form.Group>
                            <Form.Group className="mb-3" controlId="formEmail">
                                <Form.Label>Email</Form.Label>
                                <Form.Control
                                    type="email"
                                    name="email"
                                    value={patientProfile.email || ''}
                                    onChange={handleProfileChange}
                                />
                            </Form.Group>
                            <Form.Group className="mb-3" controlId="formPhone">
                                <Form.Label>Telefon</Form.Label>
                                <Form.Control
                                    type="text"
                                    name="phone"
                                    value={patientProfile.phone || ''}
                                    onChange={handleProfileChange}
                                />
                            </Form.Group>
                            <Button variant="primary" onClick={handleSaveProfile}>
                                Sačuvaj profil
                            </Button>
                        </Form>
                    </div>
                )}
            </div>

            {/* Modal za zakazivanje pregleda */}
            <Modal show={showAppointmentModal} onHide={closeAppointmentModal}>
                <Modal.Header closeButton>
                    <Modal.Title>{editingAppointment ? 'Uredi pregled' : 'Zakaži novi pregled'}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form>
                        <Form.Group className="mb-3" controlId="formDoctor">
                            <Form.Label>Odaberite doktora</Form.Label>
                            <Form.Control
                                as="select"
                                name="doctorId"
                                value={newAppointment.doctorId}
                                onChange={handleNewAppointmentChange}
                                required
                            >
                                <option value="">-- Odaberite doktora --</option>
                                {doctors.map(doctor => (
                                    <option key={doctor.id} value={doctor.id}>{doctor.firstName} {doctor.lastName}</option>
                                ))}
                            </Form.Control>
                        </Form.Group>

                        {newAppointment.doctorId && (
                            <Form.Group className="mb-3" controlId="formAvailableTerm">
                                <Form.Label>Odaberite dostupan termin</Form.Label>
                                {loading ? (
                                    <Spinner animation="border" size="sm" />
                                ) : allDoctorAvailableTerms.length > 0 ? (
                                    <Form.Control
                                        as="select"
                                        name="terminId"
                                        value={newAppointment.terminId}
                                        onChange={handleNewAppointmentChange}
                                        required
                                    >
                                        <option value="">-- Odaberite datum i vrijeme --</option>
                                        {allDoctorAvailableTerms.map(term => (
                                            <option key={term.terminID} value={term.terminID}>{term.display}</option>
                                        ))}
                                    </Form.Control>
                                ) : (
                                    <Alert variant="info">Nema dostupnih termina za ovog doktora.</Alert>
                                )}
                            </Form.Group>
                        )}

                        {newAppointment.terminId && (
                            <>
                                <Form.Group className="mb-3" controlId="formAppointmentDateDisplay">
                                    <Form.Label>Odabrani datum</Form.Label>
                                    <Form.Control
                                        type="text"
                                        value={newAppointment.appointmentDate}
                                        readOnly
                                        disabled
                                    />
                                </Form.Group>
                                <Form.Group className="mb-3" controlId="formAppointmentTimeDisplay">
                                    <Form.Label>Odabrano vrijeme</Form.Label>
                                    <Form.Control
                                        type="text"
                                        value={newAppointment.appointmentTime ? newAppointment.appointmentTime.substring(0, 5) : ''}
                                        readOnly
                                        disabled
                                    />
                                </Form.Group>
                            </>
                        )}

                        <Form.Group className="mb-3" controlId="formReason">
                            <Form.Label>Razlog za pregled</Form.Label>
                            <Form.Control
                                as="textarea"
                                rows={3}
                                name="reason"
                                value={newAppointment.reason}
                                onChange={handleNewAppointmentChange}
                                placeholder="npr. Opšti pregled, specifični simptomi..."
                                required
                            />
                        </Form.Group>
                    </Form>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={closeAppointmentModal}>
                        Zatvori
                    </Button>
                    <Button
                        variant="primary"
                        onClick={handleScheduleAppointment}
                        disabled={!newAppointment.doctorId || !newAppointment.terminId || !newAppointment.reason || loading}
                    >
                        {editingAppointment ? 'Ažuriraj pregled' : 'Zakaži pregled'}
                    </Button>
                </Modal.Footer>
            </Modal>

            {/* Modal detalja poruke */}
            <Modal show={selectedMessage !== null} onHide={closeMessageModal}>
                <Modal.Header closeButton>
                    <Modal.Title>Poruka: {selectedMessage?.subject}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    {selectedMessage && (
                        <>
                            <p><strong>Od:</strong> {selectedMessage.senderId === currentPatientUserId ? 'Ja' : (doctors.find(d => d.userId === selectedMessage.senderId)?.firstName + ' ' + doctors.find(d => d.userId === selectedMessage.senderId)?.lastName || `Doktor (${selectedMessage.senderId})`)}</p>
                            <p><strong>Datum:</strong> {new Date(selectedMessage.timestamp).toLocaleString()}</p>
                            <hr />
                            <p>{selectedMessage.content}</p>

                            {selectedMessage.replies && selectedMessage.replies.length > 0 && (
                                <div className="mt-4">
                                    <h5>Odgovori:</h5>
                                    {selectedMessage.replies.map((reply, index) => (
                                        <div key={index} className="border p-2 mb-2 rounded bg-light">
                                            <strong>{reply.senderId === currentPatientUserId ? 'Ja' : (doctors.find(d => d.userId === reply.senderId)?.firstName + ' ' + doctors.find(d => d.userId === reply.senderId)?.lastName || `Doktor (${reply.senderId})`)}:</strong> {reply.content}
                                            <br />
                                            <small>{new Date(reply.timestamp).toLocaleString()}</small>
                                        </div>
                                    ))}
                                </div>
                            )}

                            <Form.Group className="mt-4">
                                <Form.Label>Odgovor</Form.Label>
                                <Form.Control
                                    as="textarea"
                                    rows={2}
                                    value={replyMessage.content}
                                    onChange={handleReplyMessageChange}
                                    placeholder="Upišite svoj odgovor ovdje..."
                                />
                            </Form.Group>
                            <Button variant="primary" className="mt-2" onClick={() => handleReply(selectedMessage.id)}>Pošalji odgovor</Button>
                        </>
                    )}
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={closeMessageModal}>
                        Zatvori
                    </Button>
                </Modal.Footer>
            </Modal>

            {/* Modal za ocjenjivanje */}
            <Modal show={showRatingModal} onHide={closeRatingModal}>
                <Modal.Header closeButton>
                    <Modal.Title>Ocjeni doktora: {selectedDoctorForRating?.name}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form>
                        <Form.Group className="mb-3" controlId="formRating">
                            <Form.Label>Ocjena (1-5)</Form.Label>
                            <Form.Control
                                as="select"
                                name="ocjena"
                                value={ratingData.ocjena}
                                onChange={handleRatingChange}
                                required
                            >
                                <option value="1">1 - Vrlo loše</option>
                                <option value="2">2 - Loše</option>
                                <option value="3">3 - Prosječno</option>
                                <option value="4">4 - Dobro</option>
                                <option value="5">5 - Odlično</option>
                            </Form.Control>
                        </Form.Group>
                        {/* UKLONJENO: Polje za komentar */}
                    </Form>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={closeRatingModal}>
                        Zatvori
                    </Button>
                    <Button variant="primary" onClick={handleSubmitRating}>
                        Pošalji ocjenu
                    </Button>
                </Modal.Footer>
            </Modal>

            {/* NOVO: Modal detalja računa */}
            <Modal show={showBillDetailsModal} onHide={closeBillDetailsModal}>
                <Modal.Header closeButton>
                    <Modal.Title>Detalji računa</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    {selectedBillDetails && (
                        <div>
                            <p><strong>ID računa:</strong> {selectedBillDetails.id}</p>
                            <p><strong>Datum izdavanja:</strong> {new Date(selectedBillDetails.billDate).toLocaleDateString()}</p>
                            <p><strong>Iznos:</strong> {selectedBillDetails.amount ? selectedBillDetails.amount.toFixed(2) : 'N/A'} KM</p>
                            <p><strong>Status:</strong> <span className={`badge bg-${selectedBillDetails.status === 'PLACEN' ? 'success' : 'warning'}`}>{selectedBillDetails.status}</span></p>
                            <p><strong>Opis:</strong> {selectedBillDetails.opis}</p>
                            <p><strong>Izdao:</strong> {selectedBillDetails.doktorIme}</p>
                            {selectedBillDetails.dueDate && (
                                <p><strong>Datum dospijeća:</strong> {new Date(selectedBillDetails.dueDate).toLocaleDateString()}</p>
                            )}
                        </div>
                    )}
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={closeBillDetailsModal}>
                        Zatvori
                    </Button>
                </Modal.Footer>
            </Modal>

        </div>
    );
}

export default PatientDashboard;
