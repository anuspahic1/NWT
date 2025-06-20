import axios from 'axios';
import { useAuth } from '../contexts/AuthContext';
import 'bootstrap/dist/css/bootstrap.min.css';
import React, { useState, useEffect, useMemo } from 'react';
import { Modal, Button, Form, Alert, Spinner } from 'react-bootstrap';

const GATEWAY_BASE_URL = 'http://localhost:8081';

const KORISNICI_DOKTORI_API_PREFIX = `${GATEWAY_BASE_URL}/api/korisnici-doktori`;
const TERMINI_PREGLEDI_API_PREFIX = `${GATEWAY_BASE_URL}/api/termini-pregledi`;
const OBAVIJESTI_DOKUMENTI_API_PREFIX = `${GATEWAY_BASE_URL}/api/obavijesti-dokumentacija`;
// API prefiks za notifikacije unutar ObavijestiDokumenti servisa
const OBAVIJESTI_API_PREFIX = `${GATEWAY_BASE_URL}/api/obavijesti-dokumentacija/api/obavijesti`;

function DoctorDashboard() {
    const [activeTab, setActiveTab] = useState('overview');

    const [doctors, setDoctors] = useState([]);
    const [patients, setPatients] = useState([]);
    const [examinations, setExaminations] = useState([]);
    const [terms, setTerms] = useState([]);
    const [activityLog, setActivityLog] = useState([]);

    const [currentDoctorProfile, setCurrentDoctorProfile] = useState(null);
    const [isEditingProfile, setIsEditingProfile] = useState(false);

    const [editIme, setEditIme] = useState('');
    const [editPrezime, setEditPrezime] = useState('');
    const [editEmail, setEditEmail] = useState('');
    const [editTelefon, setEditTelefon] = useState('');
    const [editSpecijalizacije, setEditSpecijalizacije] = useState('');
    const [editGrad, setEditGrad] = useState('');
    const [editRadnoVrijeme, setEditRadnoVrijeme] = useState('');
    const [editIskustvo, setEditIskustvo] = useState('');
    const [editOcjena, setEditOcjena] = useState('');
    const [profileImageFile, setProfileImageFile] = useState(null);
    const [profileImagePreview, setProfileImagePreview] = useState(null);

    const [documents, setDocuments] = useState([]);
    const [messages, setMessages] = useState([]);
    const [newMessage, setNewMessage] = useState({ recipientId: '', subject: '', content: '' });
    const [replyMessage, setReplyMessage] = useState({ messageId: '', content: '' });
    const [selectedMessage, setSelectedMessage] = useState(null);

    // Novo stanje za notifikacije
    const [notifications, setNotifications] = useState([]);

    const [showAlert, setShowAlert] = useState(false);
    const [alertMessage, setAlertMessage] = useState('');
    const [alertType, setAlertType] = useState('success');

    const [patientSearchTerm, setPatientSearchTerm] = useState('');
    const [examinationSearchTerm, setExaminationSearchTerm] = useState('');
    const [examinationFilterStatus, setExaminationFilterStatus] = useState('All');

    const [myPatientsCurrentPage, setMyPatientsCurrentPage] = useState(1);
    const [myExaminationsCurrentPage, setMyExaminationsCurrentPage] = useState(1);
    const [notificationsCurrentPage, setNotificationsCurrentPage] = useState(1); // Dodano za paginaciju obavijesti
    const itemsPerPage = 5;

    const [showPatientDetailModal, setShowPatientDetailModal] = useState(false);
    const [selectedPatient, setSelectedPatient] = useState(null);
    // Stanja medicinske historije
    const [medicalHistoryEntries, setMedicalHistoryEntries] = useState([]);
    const [newMedicalHistoryEntry, setNewMedicalHistoryEntry] = useState({
        diagnosis: '',
        treatment: '',
        notes: ''
    });

    const [newTermDate, setNewTermDate] = useState('');
    const [newTermTime, setNewTermTime] = useState('');
    const [selectedTermStatus, setSelectedTermStatus] = useState('DOSTUPAN');

    const { token, user, loading: authLoading } = useAuth();

    const authHeaders = useMemo(() => ({
        headers: {
            'Authorization': `Bearer ${token}`
        }
    }), [token]);

    // Funkcija za prikazivanje obavijesti
    const showNotification = (message, type = 'success') => {
        setAlertMessage(message);
        setAlertType(type);
        setShowAlert(true);
        setTimeout(() => {
            setShowAlert(false);
            setAlertMessage('');
        }, 3000);
    };

    // Mapa za prevođenje statusa pregleda i termina na bosanski jezik
    const statusMap = {
        'zakazan': 'Zakazan',
        'obavljen': 'Obavljen',
        'otkazan': 'Otkazan',
        'DOSTUPAN': 'Dostupan',
        'ZAKAZAN': 'Zakazan', // Za termine
        'OTKAZAN': 'Otkazan', // Za termine
        'NEDOSTUPAN': 'Nedostupan', // Za termine
        'POSLANO': 'Poslano', // Za notifikacije
        'PROCITANO': 'Pročitano', // Za notifikacije
        '': 'Nepoznat', // Default ili prazno
        null: 'Nepoznat' // Za null vrijednosti
    };

    // Funkcija za dobivanje prevedenog statusa
    const getTranslatedStatus = (status) => {
        return statusMap[status] || status; // Vraća originalni status ako prevod ne postoji
    };

    const fetchDoctors = async () => {
        if (!token) return;
        try {
            const response = await axios.get(`${KORISNICI_DOKTORI_API_PREFIX}/api/doktori`, authHeaders);
            setDoctors(response.data.map(doc => ({
                id: doc.doktorID,
                userId: Number(doc.userId),
                name: `${doc.ime} ${doc.prezime}`,
                ime: doc.ime,
                prezime: doc.prezime,
                specialty: Array.isArray(doc.specijalizacije) && doc.specijalizacije.length > 0
                           ? doc.specijalizacije.join(', ')
                           : 'N/A',
                grad: doc.grad,
                email: doc.email,
                telefon: doc.telefon,
                radnoVrijeme: doc.radnoVrijeme,
                iskustvo: doc.iskustvo,
                ocjena: doc.ocjena,
                profileImageBase64: doc.profileImageBase64,
            })));
        } catch (error) {
            console.error("Greška pri dohvaćanju svih doktora:", error.response?.data || error.message);
            showNotification("Greška pri dohvaćanju doktora.", "danger");
        }
    };

    const fetchPatients = async () => {
        if (!token) return;
        try {
            const response = await axios.get(`${KORISNICI_DOKTORI_API_PREFIX}/api/pacijenti`, authHeaders);
            setPatients(response.data.map(pat => ({
                id: pat.pacijentID,
                userId: Number(pat.userId),
                name: `${pat.ime} ${pat.prezime}`,
                ime: pat.ime,
                prezime: pat.prezime,
                email: pat.email,
                phone: pat.telefon,
            })));
        } catch (error) {
            console.error("Greška pri dohvaćanju svih pacijenata:", error.response?.data || error.message);
            showNotification("Greška pri dohvaćanju pacijenata.", "danger");
        }
    };

    const fetchTerms = async () => {
        if (!token || !user || user.doktorId === null || user.doktorId === undefined) {
            console.warn("Nije moguće dohvatiti termine: doktorId nedostaje ili autentifikacija nije spremna.");
            return;
        }
        try {
            const response = await axios.get(`${TERMINI_PREGLEDI_API_PREFIX}/api/termini/doktor/${user.doktorId}`, authHeaders);
            console.log("Raw podaci o terminima primljeni sa backend-a (FETCH):", response.data);
            setTerms(response.data.map(term => {
                console.log(`Mapiranje ID termina: ${term.terminID}, status: ${term.statusTermina}, datum: ${term.datum}, vrijeme: ${term.vrijeme}`);
                return {
                    id: term.terminID,
                    doktorId: term.doktorID,
                    datum: term.datum,
                    vrijeme: term.vrijeme,
                    statusTermina: term.statusTermina,
                };
            }));
            showNotification("Doktorovi termini uspješno dohvaćeni!", "success");
        } catch (error) {
            console.error("Greška pri dohvaćanju doktorovih termina:", error.response?.data || error.message);
            showNotification("Greška pri dohvaćanju doktorovih termina.", "danger");
        }
    };

    const fetchExaminations = async () => {
        if (!token) return;
        try {
            const response = await axios.get(`${TERMINI_PREGLEDI_API_PREFIX}/api/pregledi`, authHeaders);
            setExaminations(response.data.map(exam => ({
                id: exam.pregledID,
                pacijentId: exam.pacijentID,
                doktorId: exam.doktorID,
                patientName: exam.pacijentIme || 'N/A',
                doctorName: exam.doktorIme || 'N/A',
                date: exam.datumPregleda,
                time: exam.vrijemePregleda,
                status: exam.status,
                komentarPacijenta: exam.komentarPacijenta || '',
                ocjenaDoktora: exam.ocjenaDoktora || null,
                terminID: exam.terminID,
            })));
        } catch (error) {
            console.error("Greška pri dohvaćanju svih pregleda:", error.response?.data || error.message);
            showNotification("Greška pri dohvaćanju pregleda.", "danger");
        }
    };

    const fetchActivityLog = async () => {
        if (!token) return;
        try {
            console.warn("Endpoint Dnevnika aktivnosti nije definiran, preskačem dohvaćanje dnevnika aktivnosti.");
            setActivityLog([]);
        } catch (error) {
            console.error("Greška pri dohvaćanju dnevnika aktivnosti:", error.response?.data || error.message);
            showNotification("Greška pri dohvaćanju dnevnika aktivnosti.", "danger");
        }
    };

    const fetchPatientMedicalHistory = async (patientId) => {
        if (!token || !patientId) return;
        try {
            const response = await axios.get(`${KORISNICI_DOKTORI_API_PREFIX}/api/medicinska-historija/pacijent/${patientId}`, authHeaders);
            setMedicalHistoryEntries(response.data.map(record => ({
                id: record.zapisID,
                patientId: record.pacijentID,
                doctorId: record.doktorID,
                doctorName: record.doktorIme,
                date: record.datumZapisivanja,
                diagnosis: record.dijagnoza,
                treatment: record.lijecenje,
                notes: record.napomene
            })));
        } catch (error) {
            console.error("Greška pri dohvaćanju medicinske historije pacijenta:", error.response?.data || error.message);
            setMedicalHistoryEntries([]);
            showNotification("Greška pri dohvaćanju medicinske historije pacijenta.", "danger");
        }
    };

    const handleAddMedicalHistoryEntry = async () => {
        if (!token || !selectedPatient || !user || !user.doktorId) {
            showNotification("Nedostaju podaci za dodavanje unosa medicinske historije.", "danger");
            return;
        }
        if (!newMedicalHistoryEntry.diagnosis || !newMedicalHistoryEntry.treatment) {
            showNotification("Dijagnoza i liječenje su obavezni za medicinsku historiju.", "warning");
            return;
        }

        try {
            const payload = {
                pacijentID: selectedPatient.id,
                doktorID: user.doktorId,
                datumZapisivanja: new Date().toISOString().split('T')[0],
                dijagnoza: newMedicalHistoryEntry.diagnosis,
                lijecenje: newMedicalHistoryEntry.treatment,
                napomene: newMedicalHistoryEntry.notes
            };
            await axios.post(`${KORISNICI_DOKTORI_API_PREFIX}/api/medicinska-historija`, payload, authHeaders);
            showNotification("Unos medicinske historije uspješno dodan!", "success");
            setNewMedicalHistoryEntry({ diagnosis: '', treatment: '', notes: '' });
            fetchPatientMedicalHistory(selectedPatient.id);
        } catch (error) {
            console.error("Greška pri dodavanju unosa medicinske historije:", error.response?.data || error.message);
            showNotification(error.response?.data?.message || 'Greška pri dodavanju unosa medicinske historije.', 'danger');
        }
    };

    const openPatientDetailModal = async (patient) => {
        setSelectedPatient(patient);
        await fetchPatientMedicalHistory(patient.id);
        setShowPatientDetailModal(true);
    };

    const closePatientDetailModal = () => {
        setShowPatientDetailModal(false);
        setSelectedPatient(null);
        setMedicalHistoryEntries([]);
        setNewMedicalHistoryEntry({ diagnosis: '', treatment: '', notes: '' });
    };

    const handleImageChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            setProfileImageFile(file);
            const reader = new FileReader();
            reader.onloadend = () => {
                setProfileImagePreview(reader.result);
            };
            reader.readAsDataURL(file);
        } else {
            setProfileImageFile(null);
            setProfileImagePreview(null);
        }
    };

    const fetchCurrentDoctorProfile = async () => {
        if (!token || !user || user.doktorId === null || user.doktorId === undefined) {
            console.warn("Nije moguće dohvatiti trenutni profil doktora: doktorId nedostaje ili autentifikacija nije spremna.");
            return;
        }
        try {
            const response = await axios.get(`${KORISNICI_DOKTORI_API_PREFIX}/api/doktori/${user.doktorId}`, authHeaders);
            const profileData = response.data;
            setCurrentDoctorProfile(profileData);
            setEditIme(profileData.ime || '');
            setEditPrezime(profileData.prezime || '');
            setEditEmail(profileData.email || '');
            setEditTelefon(profileData.telefon || '');
            setEditSpecijalizacije(Array.isArray(profileData.specijalizacije) ? profileData.specijalizacije.join(', ') : '');
            setEditGrad(profileData.grad || '');
            setEditRadnoVrijeme(profileData.radnoVrijeme || '');
            setEditIskustvo(profileData.iskustvo || '');
            setEditOcjena(profileData.ocjena || '');
            setProfileImagePreview(profileData.profileImageBase64 || null);
        } catch (error) {
            console.error("Greška pri dohvaćanju profila doktora:", error.response?.data || error.message);
            showNotification("Greška pri dohvaćanju profila doktora.", "danger");
        }
    };

    const handleUpdateDoctorProfile = async (e) => {
        e.preventDefault();
        if (!token || !user || user.doktorId === null || user.doktorId === undefined) {
            showNotification("Niste autorizirani ili ID doktora nedostaje.", "danger");
            return;
        }

        const updatedProfile = {
            doktorID: user.doktorId,
            userId: currentDoctorProfile.userId,
            ime: editIme,
            prezime: editPrezime,
            email: editEmail,
            telefon: editTelefon,
            specijalizacije: editSpecijalizacije.split(',').map(s => s.trim()).filter(s => s !== ''),
            grad: editGrad,
            radnoVrijeme: editRadnoVrijeme,
            iskustvo: parseInt(editIskustvo, 10),
            ocjena: parseFloat(editOcjena) || 0.0,
            profileImageBase64: profileImagePreview,
        };

        try {
            const response = await axios.put(`${KORISNICI_DOKTORI_API_PREFIX}/api/doktori/${user.doktorId}`, updatedProfile, authHeaders);
            setCurrentDoctorProfile(response.data);
            setIsEditingProfile(false);
            showNotification("Profil uspješno ažuriran!");
            fetchDoctors();
        } catch (error) {
            console.error("Greška pri ažuriranju profila:", error.response?.data || error.message);
            showNotification(error.response?.data?.message || 'Greška pri ažuriranju profila.', 'danger');
        }
    };

    const fetchDocuments = async () => {
        if (!token || !user || user.doktorId === null || user.doktorId === undefined) {
            console.warn("Nije moguće dohvatiti dokumente: doktorId nedostaje ili autentifikacija nije spremna.");
            return;
        }
        try {
            const response = await axios.get(`${OBAVIJESTI_DOKUMENTI_API_PREFIX}/api/dokumentacija/doktor/${user.doktorId}`, authHeaders);
            console.log("Raw podaci o dokumentima iz odgovora:", response.data);

            const transformedDocuments = response.data
                .filter(doc => doc.dokumentacijaID !== null && doc.dokumentacijaID !== undefined)
                .map(doc => {
                    const id = Number(doc.dokumentacijaID);
                    if (isNaN(id)) {
                        console.warn(`Dokument sa numeričkim dokumentacijaID pronađen: ${doc.dokumentacijaID}. Ovaj dokument možda neće biti moguće preuzeti, ali će biti prikazan.`);
                    }
                    return {
                        id: id,
                        fileName: doc.nazivDokumenta,
                        documentType: doc.tipDokumenta,
                        uploadDate: doc.datumKreiranja,
                        pristup: doc.pristup,
                        pacijentIme: doc.pacijentIme
                    };
                });

            console.log("Transformirani dokumenti za stanje (nakon laganog filtriranja):", transformedDocuments);
            setDocuments(transformedDocuments);
            showNotification("Dokumenti uspješno dohvaćeni!", "success");
        } catch (error) {
            console.error('Greška pri dohvaćanju dokumenata za doktora:', error.response?.data || error.message);
            showNotification('Nije uspjelo dohvaćanje dokumenata.', "danger");
            setDocuments([]);
        }
    };

    const handleDocumentUpload = async (e) => {
        const file = e.target.files[0];
        if (!file) return;

        if (!user || user.doktorId === null || user.doktorId === undefined) {
            showNotification("ID doktora nije dostupan za upload dokumenta.", "danger");
            return;
        }

        console.log("Pokušavam uploadati dokument sa ID-om doktora:", user.doktorId);

        const formData = new FormData();
        formData.append('file', file);
        formData.append('doktorID', user.doktorId);
        formData.append('tipDokumenta', 'Doctor Upload');
        formData.append('pristup', 'PRIVATNA');

        try {
            await axios.post(`${OBAVIJESTI_DOKUMENTI_API_PREFIX}/api/dokumentacija/upload`, formData, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'multipart/form-data'
                }
            });
            showNotification('Dokument uspješno uploadan!', 'success');
            setTimeout(() => {
                fetchDocuments();
            }, 500);
        } catch (error) {
            console.error('Greška pri uploadu dokumenta:', error.response?.data || error.message);
            showNotification('Upload dokumenta nije uspio. ' + (error.response?.data?.message || error.message), "danger");
        }
    };

    const handleDownloadDocument = async (docId, fileName) => {
        const numericDocId = Number(docId);

        if (docId === null || docId === undefined || isNaN(numericDocId)) {
            console.error('Pokušaj preuzimanja dokumenta sa nevažećim ID-om:', docId);
            showNotification('Greška: ID dokumenta je nevažeći. Preuzimanje nije moguće.', 'danger');
            return;
        }

        const downloadUrl = `${OBAVIJESTI_DOKUMENTI_API_PREFIX}/api/dokumentacija/download/${numericDocId}`;
        console.log("Pokušavam preuzeti dokument sa URL-a:", downloadUrl);

        try {
            const response = await axios.get(downloadUrl, {
                ...authHeaders,
                responseType: 'blob',
            });

            const blob = new Blob([response.data], { type: response.headers['content-type'] });
            const url = window.URL.createObjectURL(blob);
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', fileName);
            document.body.appendChild(link);
            link.click();
            link.remove();
            window.URL.revokeObjectURL(url);

            showNotification('Dokument uspješno preuzet!', 'success');
        } catch (error) {
            console.error('Greška pri preuzimanju dokumenta:', error.response?.data || error.message);
            showNotification('Nije uspjelo preuzimanje dokumenta. ' + (error.response?.data?.message || error.message), 'danger');
        }
    };


    const fetchMessages = async () => {
        if (!token || !user || user.userId === null || user.userId === undefined) {
            console.warn("Nije moguće dohvatiti poruke: user.userId nedostaje ili autentifikacija nije spremna.");
            return;
        }
        try {
            const response = await axios.get(`${OBAVIJESTI_DOKUMENTI_API_PREFIX}/api/poruke/konverzacija/${user.userId}?userType=DOKTOR`, authHeaders);
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
            showNotification("Poruke uspješno dohvaćene!", "success");
        } catch (error) {
            console.error('Greška pri dohvaćanju poruka za doktora:', error.response?.data || error.message);
            showNotification('Nije uspjelo dohvaćanje poruka.', "danger");
        }
    };

    const handleNewMessageChange = (e) => {
        const { name, value } = e.target;
        setNewMessage(prev => ({ ...prev, [name]: value }));
    };

    const handleSendMessage = async () => {
        if (!user || user.userId === null || user.userId === undefined) {
            showNotification('Vaš ID korisnika nije dostupan. Nije moguće poslati poruku.', 'danger');
            return;
        }
        if (!newMessage.recipientId || !newMessage.subject || !newMessage.content) {
            showNotification('Molimo popunite sva polja poruke.', 'warning');
            return;
        }

        try {
            const messageData = {
                senderId: user.userId,
                senderType: 'DOKTOR',
                receiverId: Number(newMessage.recipientId),
                receiverType: 'PACIJENT',
                subject: newMessage.subject,
                content: newMessage.content
            };
            await axios.post(`${OBAVIJESTI_DOKUMENTI_API_PREFIX}/api/poruke/posalji`, messageData, authHeaders);
            showNotification('Poruka uspješno poslana!', 'success');
            setNewMessage({ recipientId: '', subject: '', content: '' });
            fetchMessages();
        } catch (error) {
            console.error('Greška pri slanju poruke:', error.response?.data || error.message);
            showNotification('Nije uspjelo slanje poruke. ' + (error.response?.data?.message || error.message), "danger");
        }
    };

    const handleReplyMessageChange = (e) => {
        setReplyMessage(prev => ({ ...prev, content: e.target.value }));
    };

    const handleReply = async (messageId) => {
        if (!user || user.userId === null || user.userId === undefined) {
            showNotification('Vaš ID korisnika nije dostupan. Nije moguće odgovoriti.', 'danger');
            return;
        }
        if (!replyMessage.content) {
            showNotification('Sadržaj odgovora ne može biti prazan.', 'warning');
            return;
        }
        try {
            await axios.post(`${OBAVIJESTI_DOKUMENTI_API_PREFIX}/api/poruke/${messageId}/odgovori`, {
                senderId: user.userId,
                senderIme: `${user.ime || ''} ${user.prezime || ''}`.trim(),
                senderType: 'DOKTOR',
                content: replyMessage.content,
                timestamp: new Date().toISOString()
            }, authHeaders);
            showNotification('Odgovor uspješno poslan!', 'success');
            setReplyMessage({ messageId: '', content: '' });
            fetchMessages();
            setSelectedMessage(prev => {
                if (!prev) return null;
                return {
                    ...prev,
                    replies: [...(prev.replies || []), {
                        senderId: user.userId,
                        senderIme: `${user.ime || ''} ${user.prezime || ''}`.trim(),
                        senderType: 'DOKTOR',
                        content: replyMessage.content,
                        timestamp: new Date().toISOString()
                    }]
                };
            });
        } catch (error) {
            console.error('Greška pri slanju odgovora:', error.response?.data || error.message);
            showNotification('Nije uspjelo slanje odgovora. ' + (error.response?.data?.message || error.message), "danger");
        }
    };

    const handleViewMessage = (message) => {
        setSelectedMessage(message);
    };

    const closeMessageModal = () => {
        setSelectedMessage(null);
        setReplyMessage({ messageId: '', content: '' });
    };

    const handleCreateTerm = async (e) => {
        e.preventDefault();
        if (!user || user.doktorId === null || user.doktorId === undefined) {
            showNotification("ID doktora nije dostupan. Nije moguće kreirati termin.", "danger");
            return;
        }
        if (!newTermDate || !newTermTime) {
            showNotification("Molimo odaberite i datum i vrijeme za novi termin.", "warning");
            return;
        }

        try {
            const termData = {
                doktorID: user.doktorId,
                datum: newTermDate,
                vrijeme: newTermTime + ':00',
                statusTermina: selectedTermStatus
            };
            console.log("Slanje podataka za novi termin (KREIRANJE):", termData);

            await axios.post(`${TERMINI_PREGLEDI_API_PREFIX}/api/termini`, termData, authHeaders);
            showNotification("Termin uspješno kreiran!", "success");
            setNewTermDate('');
            setNewTermTime('');
            setSelectedTermStatus('DOSTUPAN');
            fetchTerms();
        } catch (error) {
            console.error("Greška pri kreiranju termina:", error.response?.data || error.message);
            showNotification(`Nije uspjelo kreiranje termina: ${error.response?.data?.message || error.message}`, "danger");
        }
    };

    const handleDeleteTerm = async (termId) => {
        if (!token) return;
        try {
            await axios.delete(`${TERMINI_PREGLEDI_API_PREFIX}/api/termini/${termId}`, authHeaders);
            showNotification("Termin uspješno obrisan!", "success");
            fetchTerms();
        } catch (error) {
            console.error("Greška pri brisanju termina:", error.response?.data || error.message);
            showNotification(`Nije uspjelo brisanje termina: ${error.response?.data?.message || error.message}`, "danger");
        }
    };

    const handleUpdateTermStatus = async (termId, targetStatus) => {
        if (!token) return;

        const termToUpdate = terms.find(term => term.id === termId);
        if (!termToUpdate) {
            showNotification('Termin nije pronađen za ažuriranje.', 'danger');
            return;
        }
        console.log(`DEBUG (Frontend): Originalni termin iz stanja za ID ${termId}:`, termToUpdate);
        console.log(`DEBUG (Frontend): Originalni datum termina iz stanja: ${termToUpdate.datum}`);
        console.log(`DEBUG (Frontend): Originalno vrijeme termina iz stanja: ${termToUpdate.vrijeme}`);
        console.log(`DEBUG (Frontend): Ciljani status za ažuriranje: ${targetStatus}`);


        try {
            const payload = {
                terminID: termToUpdate.id,
                doktorID: termToUpdate.doktorId,
                datum: typeof termToUpdate.datum === 'string' ? termToUpdate.datum : termToUpdate.datum.toISOString().split('T')[0],
                vrijeme: termToUpdate.vrijeme, // Koristi vrijeme string direktno iz stanja
                statusTermina: targetStatus
            };
            console.log("Slanje payloada za ažuriranje termina (AŽURIRANJE - sa frontenda):", payload);

            const response = await axios.put(`${TERMINI_PREGLEDI_API_PREFIX}/api/termini/${termId}`, payload, authHeaders);
            console.log("Odgovor backend-a za ažuriranje:", response.data);
            showNotification(`Status termina ažuriran na ${getTranslatedStatus(targetStatus).toLowerCase()}!`, 'success');
            fetchTerms();
        } catch (error) {
            console.error("Greška pri ažuriranju statusa termina:", error.response?.data || error.message);
            showNotification(`Nije uspjelo ažuriranje statusa termina: ${error.response?.data?.message || error.message}`, "danger");
        }
    };

    // Nova funkcija za dohvaćanje obavijesti
    const fetchNotifications = async () => {
        if (!token || !user || user.doktorId === null || user.doktorId === undefined) {
            console.warn("Nije moguće dohvatiti obavijesti: doktorId nedostaje ili autentifikacija nije spremna.");
            setNotifications([]); // Osiguraj praznu listu ako podaci nisu dostupni
            return;
        }
        try {
            // Pretpostavljeni endpoint za dohvaćanje obavijesti za doktora
            const response = await axios.get(`${OBAVIJESTI_API_PREFIX}/notifikacije/doktor/${user.doktorId}`, authHeaders);
            console.log("Raw podaci o notifikacijama primljeni sa backend-a:", response.data);

            setNotifications(response.data.map(notif => ({
                id: notif.notifikacijaID || notif.id, // Koristite notifikacijaID sa backend-a
                korisnikID: notif.korisnikID,
                tip: notif.uloga, // Uloga 'DOKTOR' ili 'PACIJENT' može se koristiti kao tip obavijesti
                sadrzaj: notif.sadrzaj,
                datum: notif.datumSlanja || notif.datum, // Koristite datumSlanja sa backend-a
                procitana: notif.status === 'PROCITANO' // Pretvara STATUS u boolean procitana
            })).sort((a, b) => new Date(b.datum) - new Date(a.datum))); // Sortiraj od najnovije

            showNotification("Obavijesti uspješno dohvaćene!", "success");
        } catch (error) {
            console.error("Greška pri dohvaćanju obavijesti:", error.response?.data || error.message);
            showNotification("Greška pri dohvaćanju obavijesti. Prikazujem demo podatke.", "danger");
            // Demo podaci ako je došlo do greške (za testiranje frontenda)
            setNotifications([
                { id: 1, korisnikID: user.doktorId, tip: 'NOVI_PREGLED', sadrzaj: 'Novi pregled zakazan za Pacijenta A (2025-06-25 u 10:00).', datum: '2025-06-20T10:30:00', procitana: false },
                { id: 2, korisnikID: user.doktorId, tip: 'OPSTA_OBAVIJEST', sadrzaj: 'Sistem će biti nedostupan zbog održavanja od 02:00 do 03:00 ujutro.', datum: '2025-06-19T18:00:00', procitana: false },
                { id: 3, korisnikID: user.doktorId, tip: 'OTKAZANI_PREGLED', sadrzaj: 'Pregled za Pacijenta B (2025-06-22 u 14:00) je otkazan.', datum: '2025-06-18T09:15:00', procitana: true },
                { id: 4, korisnikID: user.doktorId, tip: 'NOVI_PREGLED', sadrzaj: 'Novi pregled zakazan za Pacijenta C (2025-06-28 u 09:30).', datum: '2025-06-20T11:00:00', procitana: false },
            ].filter(n => n.korisnikID === user.doktorId).sort((a,b) => new Date(b.datum) - new Date(a.datum)));
        }
    };

    // Funkcija za označavanje obavijesti kao pročitane
    const handleMarkNotificationAsRead = async (notificationId) => {
        if (!token) return;
        try {
            // Pretpostavljeni endpoint za ažuriranje statusa obavijesti
            await axios.put(`${OBAVIJESTI_API_PREFIX}/notifikacije/${notificationId}/procitana`, null, authHeaders);
            setNotifications(prevNotifications =>
                prevNotifications.map(notif =>
                    notif.id === notificationId ? { ...notif, procitana: true } : notif
                )
            );
            showNotification("Obavijest označena kao pročitana!", "success");
        } catch (error) {
            console.error("Greška pri označavanju obavijesti kao pročitane:", error.response?.data || error.message);
            showNotification("Nije uspjelo označavanje obavijesti kao pročitane.", "danger");
            // Lokalno ažuriranje za demo svrhe ako backend ne radi
            setNotifications(prevNotifications =>
                prevNotifications.map(notif =>
                    notif.id === notificationId ? { ...notif, procitana: true } : notif
                )
            );
        }
    };

    useEffect(() => {
        console.log("DoctorDashboard useEffect: token=", token, "user=", user, "authLoading=", authLoading);
        if (!authLoading && token && user && user.doktorId) {
            console.log("DoctorDashboard useEffect: Pokrećem dohvaćanje podataka za doktorId:", user.doktorId);
            fetchDoctors();
            fetchPatients();
            fetchTerms();
            fetchExaminations();
            fetchActivityLog();
            fetchCurrentDoctorProfile();
            fetchDocuments();
            fetchMessages();
            fetchNotifications(); // Dohvati obavijesti
        } else if (!authLoading) {
            console.log("DoctorDashboard useEffect: Dohvaćanje podataka preskočeno jer uvjet nije ispunjen.", { token, user, doktorId: user?.doktorId, authLoading });
        }
    }, [token, user, authLoading]);

    console.log("DoctorDashboard provjera renderiranja: token=", token, "user=", user, "user.doktorId=", user?.doktorId, "authLoading=", authLoading);
    if (authLoading) {
        console.log("DoctorDashboard: Još uvijek se učitava autentifikacija...");
        return (
            <div className="d-flex justify-content-center align-items-center" style={{ minHeight: '100vh' }}>
                <div className="spinner-border text-primary" role="status">
                    <span className="visually-hidden">Učitavanje...</span>
                </div>
                <p className="ms-3">Učitavanje autentifikacije...</p>
            </div>
        );
    }
    if (!token || !user || user.doktorId === null || user.doktorId === undefined) {
        console.log("DoctorDashboard: Nije autoriziran kao doktor ili nepotpuni podaci. Uvjeti:", { token: !!token, user: !!user, doktorIdDefined: user?.doktorId !== null && user?.doktorId !== undefined });
        return (
            <div className="d-flex justify-content-center align-items-center" style={{ minHeight: '100vh' }}>
                <div className="spinner-border text-primary" role="status">
                    <span className="visually-hidden">Učitavanje...</span>
                </div>
                <p className="ms-3">Niste autorizirani kao doktor ili nepotpuni podaci.</p>
            </div>
        );
    }

    const currentDoctorId = user.doktorId;
    const currentDoctorUserId = user.userId;
    console.log("DoctorDashboard: Trenutni ID doktora za filtriranje (nakon početnih provjera):", currentDoctorId, "Trenutni ID korisnika:", currentDoctorUserId);


    const doctorsExaminations = examinations.filter(
        (examination) => examination.doktorId === currentDoctorId
    );

    const patientIdsWithExaminations = new Set(
        doctorsExaminations.map((exam) => exam.pacijentId)
    );
    const myPatients = patients.filter((patient) =>
        patientIdsWithExaminations.has(patient.id)
    );

    const handleUpdateExaminationStatus = async (id, newStatus) => {
        try {
            const existingExamination = examinations.find(exam => exam.id === id);
            if (!existingExamination) {
                showNotification('Pregled nije pronađen za ažuriranje statusa.', 'danger');
                return;
            }

            console.log("Postojeći objekt pregleda prije ažuriranja:", existingExamination);
            const terminIdToSend = existingExamination.terminID;
            console.log("ID termina za slanje:", terminIdToSend);

            if (terminIdToSend === null || terminIdToSend === undefined) {
                showNotification('Greška: ID termina nedostaje za ovaj pregled. Molimo osigurajte da svi pregledi imaju važeći termin.', 'danger');
                return;
            }

            const ratingToSend = newStatus === 'obavljen' ? (existingExamination.ocjenaDoktora || 5.0) : existingExamination.ocjenaDoktora;


            const examinationPayload = {
                pregledID: existingExamination.id,
                pacijentID: existingExamination.pacijentId,
                doktorID: existingExamination.doktorId,
                datumPregleda: existingExamination.date.split('T')[0],
                vrijemePregleda: existingExamination.time.padEnd(8, ':00'),
                status: newStatus,
                komentarPacijenta: existingExamination.komentarPacijenta,
                ocjenaDoktora: ratingToSend,
                terminID: terminIdToSend,
            };

            const response = await axios.put(`${TERMINI_PREGLEDI_API_PREFIX}/api/pregledi/${id}`, examinationPayload, authHeaders);
            const updatedExam = {
                id: response.data.pregledID,
                pacijentId: response.data.pacijentID,
                doktorId: response.data.doktorID,
                patientName: response.data.pacijentIme || 'N/A',
                doctorName: response.data.doktorIme || 'N/A',
                date: response.data.datumPregleda,
                time: response.data.vrijemePregleda,
                status: response.data.status,
                komentarPacijenta: response.data.komentarPacijenta || '',
                ocjenaDoktora: response.data.ocjenaDoktora || null,
                terminID: response.data.terminID,
            };
            setExaminations(examinations.map(exam => (exam.id === id ? updatedExam : exam)));
            fetchActivityLog();
            showNotification(`Status pregleda ažuriran na ${getTranslatedStatus(newStatus).toLowerCase()}!`, 'info');
        } catch (error) {
            console.error("Greška pri ažuriranju statusa pregleda:", error.response?.data || error.message);
            showNotification(error.response?.data?.message || 'Greška pri ažuriranju statusa pregleda.', "danger");
        }
    };

    const filteredMyPatients = myPatients.filter(patient =>
        (patient.name && patient.name.toLowerCase().includes(patientSearchTerm.toLowerCase())) ||
        (patient.email && patient.email.toLowerCase().includes(patientSearchTerm.toLowerCase())) ||
        (patient.phone && patient.phone.toLowerCase().includes(patientSearchTerm.toLowerCase()))
    );

    const indexOfLastMyPatient = myPatientsCurrentPage * itemsPerPage;
    const indexOfFirstMyPatient = indexOfLastMyPatient - itemsPerPage;
    const currentMyPatients = filteredMyPatients.slice(indexOfFirstMyPatient, indexOfLastMyPatient);
    const totalMyPatientPages = Math.ceil(filteredMyPatients.length / itemsPerPage);

    const filteredMyExaminations = doctorsExaminations.filter(examination => {
        const matchesSearch = (examination.patientName && examination.patientName.toLowerCase().includes(examinationSearchTerm.toLowerCase())) ||
            (examination.status && examination.status.toLowerCase().includes(examinationSearchTerm.toLowerCase())) ||
            (examination.date && new Date(examination.date).toLocaleDateString().includes(examinationSearchTerm));

        const matchesFilterStatus = examinationFilterStatus === 'All' || examination.status === examinationFilterStatus;

        return matchesSearch && matchesFilterStatus;
    });

    const indexOfLastMyExaminations = myExaminationsCurrentPage * itemsPerPage;
    const indexOfFirstMyExaminations = indexOfLastMyExaminations - itemsPerPage;
    const currentMyExaminations = filteredMyExaminations.slice(indexOfFirstMyExaminations, indexOfLastMyExaminations);
    const totalMyExaminationsPages = Math.ceil(filteredMyExaminations.length / itemsPerPage);


    const Pagination = ({ itemsPerPage, totalItems, currentPage, paginate }) => {
        const pageNumbers = [];
        for (let i = 1; i <= Math.ceil(totalItems / itemsPerPage); i++) {
            pageNumbers.push(i);
        }

        return (
            <nav>
                <ul className="pagination justify-content-center mt-4">
                    {pageNumbers.map(number => (
                        <li key={number} className={`page-item ${currentPage === number ? 'active' : ''}`}>
                            <button onClick={() => paginate(number)} className="page-link">
                                {number}
                            </button>
                        </li>
                    ))}
                </ul>
            </nav>
        );
    };

    // Funkcija za renderiranje sadržaja taba
    const renderContent = () => {
        switch (activeTab) {
            case 'overview':
                return (
                    <div>
                        <h4 className="mb-4">Pregled Doktora</h4>
                        <div className="row g-4">
                            <div className="col-md-6">
                                <div className="card text-white bg-info mb-3">
                                    <div className="card-header">Ukupno pacijenata pod skrbi</div>
                                    <div className="card-body">
                                        <h5 className="card-title display-4">{myPatients.length}</h5>
                                    </div>
                                </div>
                            </div>
                            <div className="col-md-6">
                                <div className="card text-white bg-warning mb-3">
                                    <div className="card-header">Pregledi na čekanju</div>
                                    <div className="card-body">
                                        <h5 className="card-title display-4">{doctorsExaminations.filter(exam => exam.status === 'zakazan').length}</h5>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div className="mt-5">
                            <h5>Brze akcije</h5>
                            <div className="d-flex gap-2 flex-wrap">
                                <button className="btn btn-outline-info" onClick={() => setActiveTab('my-examinations')}>Moji pregledi</button>
                                <button className="btn btn-outline-success" onClick={() => setActiveTab('my-patients')}>Moji pacijenti</button>
                                <button className="btn btn-outline-primary" onClick={() => setActiveTab('documents')}>Dokumenti</button>
                                <button className="btn btn-outline-secondary" onClick={() => setActiveTab('messages')}>Poruke</button>
                                <button className="btn btn-outline-danger" onClick={() => setActiveTab('manage-terms')}>Upravljanje terminima</button>
                                <button className="btn btn-outline-dark" onClick={() => setActiveTab('notifications')}>Obavijesti</button> {/* Novi tab */}
                            </div>
                        </div>
                        <div className="mt-5">
                            <h5>Nedavne aktivnosti</h5>
                            <ul className="list-group">
                                {activityLog.length > 0 ? (
                                    activityLog
                                        .filter(log => log.doktorId === currentDoctorId)
                                        .slice(-5).reverse().map(log => (
                                            <li key={log.id || Math.random()} className="list-group-item d-flex justify-content-between align-items-center">
                                                {log.action}
                                                <span className="badge bg-light text-dark">{new Date(log.timestamp).toLocaleString()}</span>
                                            </li>
                                        ))
                                ) : (
                                    <li className="list-group-item text-center text-muted">Nema nedavnih aktivnosti.</li>
                                )}
                            </ul>
                        </div>
                    </div>
                );
            case 'my-examinations':
                return (
                    <div>
                        <h4 className="mb-4">Upravljanje mojim pregledima</h4>
                        <div className="d-flex justify-content-between mb-3">
                            <input
                                type="text"
                                className="form-control w-25 me-2"
                                placeholder="Pretražite preglede (ime pacijenta, status, datum)..."
                                value={examinationSearchTerm}
                                onChange={(e) => setExaminationSearchTerm(e.target.value)}
                            />
                            <Form.Select
                                className="w-auto"
                                value={examinationFilterStatus}
                                onChange={(e) => setExaminationFilterStatus(e.target.value)}
                            >
                                <option value="All">Filtriraj po statusu (Svi)</option>
                                <option value="zakazan">Zakazan</option>
                                <option value="obavljen">Obavljen</option>
                                <option value="otkazan">Otkazan</option>
                            </Form.Select>
                        </div>
                        <div className="table-responsive">
                            <table className="table table-striped table-hover">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Pacijent</th>
                                        <th>Datum</th>
                                        <th>Vrijeme</th>
                                        <th>Status</th>
                                        <th>Akcije</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {currentMyExaminations.length > 0 ? (
                                        currentMyExaminations.map(examination => {
                                            const patient = patients.find(p => p.id === examination.pacijentId);
                                            return (
                                                <tr key={examination.id}>
                                                    <td>{examination.id}</td>
                                                    <td>{patient ? patient.name : 'N/A'}</td>
                                                    <td>{new Date(examination.date).toLocaleDateString()}</td>
                                                    <td>{examination.time}</td>
                                                    <td>
                                                        <span className={`badge ${
                                                            examination.status === 'zakazan' ? 'bg-warning text-dark' :
                                                            examination.status === 'obavljen' ? 'bg-success' :
                                                            examination.status === 'otkazan' ? 'bg-danger' :
                                                            'bg-secondary'
                                                        }`}>
                                                            {getTranslatedStatus(examination.status)}
                                                        </span>
                                                    </td>
                                                    <td>
                                                        {examination.status === 'zakazan' && (
                                                            <>
                                                                <button
                                                                    className="btn btn-success btn-sm me-2"
                                                                    onClick={() => handleUpdateExaminationStatus(examination.id, 'obavljen')}
                                                                >
                                                                    Označi kao Obavljen
                                                                </button>
                                                                <button
                                                                    className="btn btn-danger btn-sm"
                                                                    onClick={() => handleUpdateExaminationStatus(examination.id, 'otkazan')}
                                                                >
                                                                    Otkaži
                                                                </button>
                                                            </>
                                                        )}
                                                        {examination.status === 'obavljen' && (
                                                            <span className="text-muted">Obavljen</span>
                                                        )}
                                                        {examination.status === 'otkazan' && (
                                                            <span className="text-muted">Otkazan</span>
                                                        )}
                                                    </td>
                                                </tr>
                                            );
                                        })
                                    ) : (
                                        <tr>
                                            <td colSpan="6" className="text-center">Nije pronađen nijedan pregled.</td>
                                        </tr>
                                    )}
                                </tbody>
                            </table>
                        </div>
                        <Pagination
                            itemsPerPage={itemsPerPage}
                            totalItems={filteredMyExaminations.length}
                            currentPage={myExaminationsCurrentPage}
                            paginate={setMyExaminationsCurrentPage}
                        />
                    </div>
                );
            case 'my-patients':
                return (
                    <div>
                        <h4 className="mb-4">Moji pacijenti</h4>
                        <div className="d-flex justify-content-between mb-3">
                            <input
                                type="text"
                                className="form-control w-25"
                                placeholder="Pretražite pacijente..."
                                value={patientSearchTerm}
                                onChange={(e) => setPatientSearchTerm(e.target.value)}
                            />
                        </div>
                        <div className="table-responsive">
                            <table className="table table-striped table-hover">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Ime</th>
                                        <th>Email</th>
                                        <th>Telefon</th>
                                        <th>Akcije</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {currentMyPatients.length > 0 ? (
                                        currentMyPatients.map(patient => (
                                            <tr key={patient.id}>
                                                <td>{patient.id}</td>
                                                <td>{patient.name}</td>
                                                <td>{patient.email}</td>
                                                <td>{patient.phone}</td>
                                                <td>
                                                    <button
                                                        className="btn btn-primary btn-sm me-2"
                                                        onClick={() => openPatientDetailModal(patient)}
                                                    >
                                                        Detalji / Medicinska historija
                                                    </button>
                                                </td>
                                            </tr>
                                        ))
                                    ) : (
                                        <tr>
                                            <td colSpan="5" className="text-center">Nije pronađen nijedan pacijent.</td>
                                        </tr>
                                    )}
                                </tbody>
                            </table>
                        </div>
                        <Pagination
                            itemsPerPage={itemsPerPage}
                            totalItems={filteredMyPatients.length}
                            currentPage={myPatientsCurrentPage}
                            paginate={setMyPatientsCurrentPage}
                        />
                    </div>
                );
            case 'documents':
                return (
                    <div>
                        <h4 className="mb-4">Moji dokumenti</h4>
                        <Form.Group controlId="formFile" className="mb-3">
                            <Form.Label>Upload novog dokumenta</Form.Label>
                            <Form.Control type="file" onChange={handleDocumentUpload} />
                        </Form.Group>

                        {documents.length > 0 ? (
                            <table className="table table-striped">
                                <thead>
                                    <tr>
                                        <th>Naziv dokumenta</th>
                                        <th>Tip</th>
                                        <th>Datum uploada</th>
                                        <th>Pristup</th>
                                        <th>Akcije</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {documents.map(doc => (
                                        <tr key={doc.id || `doc-${Math.random()}`}>
                                            <td>{doc.fileName}</td>
                                            <td>{doc.documentType}</td>
                                            <td>{new Date(doc.uploadDate).toLocaleDateString()}</td>
                                            <td>
                                                <span className={`badge bg-${doc.pristup === 'JAVNA' ? 'success' : 'secondary'}`}>
                                                    {doc.pristup === 'JAVNA' ? 'Javno' : 'Privatno'}
                                                </span>
                                            </td>
                                            <td>
                                                <Button variant="success" size="sm" onClick={() => handleDownloadDocument(doc.id, doc.fileName)}>Preuzmi</Button>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        ) : (
                            <p>Nije pronađen nijedan dokument.</p>
                        )}
                    </div>
                );
            case 'messages':
                return (
                    <div>
                        <h4 className="mb-4">Moje poruke</h4>
                        <Button variant="primary" className="mb-3" onClick={() => setNewMessage({ recipientId: '', subject: '', content: '' })}>Napiši novu poruku</Button>

                        {messages.length > 0 ? (
                            <ul className="list-group">
                                {messages.map(msg => (
                                    <li key={msg.id} className="list-group-item d-flex justify-content-between align-items-center">
                                        <div>
                                            <strong>Od: {msg.senderId === currentDoctorUserId ? 'Ja' : (patients.find(p => p.userId === msg.senderId)?.name || `Pacijent (${msg.senderId})`)}</strong> <br />
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
                            <Form.Group className="mb-3">
                                <Form.Label>Primalac (Pacijent)</Form.Label>
                                <Form.Control
                                    as="select"
                                    name="recipientId"
                                    value={newMessage.recipientId}
                                    onChange={handleNewMessageChange}
                                    required
                                >
                                    <option value="">-- Odaberite pacijenta --</option>
                                    {myPatients.map(patient => (
                                        <option key={patient.userId} value={patient.userId}>{patient.name}</option>
                                    ))}
                                </Form.Control>
                            </Form.Group>
                            <Form.Group className="mb-3">
                                <Form.Label>Naslov</Form.Label>
                                <Form.Control
                                    type="text"
                                    name="subject"
                                    value={newMessage.subject}
                                    onChange={handleNewMessageChange}
                                    required
                                />
                            </Form.Group>
                            <Form.Group className="mb-3">
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
                );
            case 'manage-terms':
                const doctorsTerms = terms.filter(term => term.doktorId === currentDoctorId);
                const sortedTerms = doctorsTerms.sort((a, b) => {
                    const dateComparison = a.datum.localeCompare(b.datum);
                    if (dateComparison !== 0) {
                        return dateComparison;
                    }
                    return a.vrijeme.localeCompare(b.vrijeme);
                });

                return (
                    <div>
                        <h4 className="mb-4">Upravljanje mojim terminima</h4>

                        <h5>Kreiraj novi termin</h5>
                        <Form onSubmit={handleCreateTerm} className="mb-5 p-3 border rounded shadow-sm bg-white">
                            <div className="row mb-3">
                                <Form.Group controlId="newTermDate" className="col-md-4">
                                    <Form.Label>Datum</Form.Label>
                                    <Form.Control
                                        type="date"
                                        value={newTermDate}
                                        onChange={(e) => setNewTermDate(e.target.value)}
                                        required
                                    />
                                </Form.Group>
                                <Form.Group controlId="newTermTime" className="col-md-4">
                                    <Form.Label>Vrijeme</Form.Label>
                                    <Form.Control
                                        type="time"
                                        value={newTermTime}
                                        onChange={(e) => setNewTermTime(e.target.value)}
                                        required
                                    />
                                </Form.Group>
                                <Form.Group controlId="newTermStatus" className="col-md-4">
                                    <Form.Label>Dostupnost</Form.Label>
                                    <Form.Control
                                        as="select"
                                        value={selectedTermStatus}
                                        onChange={(e) => setSelectedTermStatus(e.target.value)}
                                    >
                                        <option value="DOSTUPAN">Dostupan</option>
                                        <option value="ZAKAZAN">Zakazan</option>
                                        <option value="OTKAZAN">Otkazan</option>
                                    </Form.Control>
                                </Form.Group>
                            </div>
                            <Button variant="success" type="submit">Kreiraj termin</Button>
                        </Form>

                        <h5>Moji postojeći termini</h5>
                        {sortedTerms.length > 0 ? (
                            <div className="table-responsive">
                                <table className="table table-striped table-hover">
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>Datum</th>
                                            <th>Vrijeme</th>
                                            <th>Status</th>
                                            <th>Akcije</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {sortedTerms.map(term => (
                                            <tr key={term.id}>
                                                <td>{term.id}</td>
                                                <td>{term.datum}</td>
                                                <td>{term.vrijeme}</td>
                                                <td>
                                                    <span className={`badge bg-${
                                                        term.statusTermina === 'DOSTUPAN' ? 'success' :
                                                        term.statusTermina === 'ZAKAZAN' ? 'warning text-dark' :
                                                        'danger'
                                                    }`}>
                                                        {getTranslatedStatus(term.statusTermina)}
                                                    </span>
                                                </td>
                                                <td>
                                                    {term.statusTermina === 'DOSTUPAN' && (
                                                        <Button
                                                            variant="warning"
                                                            size="sm"
                                                            onClick={() => handleUpdateTermStatus(term.id, 'ZAKAZAN')}
                                                            className="me-2"
                                                        >
                                                            Označi kao Zakazan
                                                        </Button>
                                                    )}
                                                    {term.statusTermina === 'ZAKAZAN' && (
                                                        <>
                                                            <Button
                                                                variant="danger"
                                                                size="sm"
                                                                onClick={() => handleUpdateTermStatus(term.id, 'OTKAZAN')}
                                                                className="me-2"
                                                            >
                                                                Označi kao Otkazan
                                                            </Button>
                                                            <Button
                                                                variant="success"
                                                                size="sm"
                                                                onClick={() => handleUpdateTermStatus(term.id, 'DOSTUPAN')}
                                                                className="me-2"
                                                            >
                                                                Označi kao Dostupan
                                                            </Button>
                                                        </>
                                                    )}
                                                    {term.statusTermina === 'OTKAZAN' && (
                                                        <Button
                                                            variant="success"
                                                            size="sm"
                                                            onClick={() => handleUpdateTermStatus(term.id, 'DOSTUPAN')}
                                                            className="me-2"
                                                        >
                                                            Označi kao Dostupan
                                                        </Button>
                                                    )}

                                                    <Button variant="danger" size="sm" onClick={() => handleDeleteTerm(term.id)}>Obriši</Button>
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
                        ) : (
                            <p>Nema pronađenih termina za vaš profil. Molimo kreirajte nove.</p>
                        )}
                    </div>
                );
            case 'notifications': // Novi tab za obavijesti
                const sortedNotifications = notifications.sort((a, b) => new Date(b.datum) - new Date(a.datum));
                const currentNotifications = sortedNotifications.slice(
                    (notificationsCurrentPage - 1) * itemsPerPage,
                    notificationsCurrentPage * itemsPerPage
                );
                const totalNotificationPages = Math.ceil(sortedNotifications.length / itemsPerPage);

                return (
                    <div>
                        <h4 className="mb-4">Moje obavijesti</h4>
                        {notifications.length > 0 ? (
                            <ul className="list-group">
                                {currentNotifications.map(notif => (
                                    <li key={notif.id} className={`list-group-item d-flex justify-content-between align-items-center ${notif.procitana ? 'text-muted bg-light' : 'bg-white'}`}>
                                        <div>
                                            {/* Boja na osnovu tipa obavijesti (može se proširiti) */}
                                            <span className={`badge me-2 ${
                                                notif.tip === 'NOVI_PREGLED' ? 'bg-primary' :
                                                notif.tip === 'OTKAZANI_PREGLED' ? 'bg-danger' :
                                                notif.tip === 'OPSTA_OBAVIJEST' ? 'bg-info text-dark' :
                                                'bg-secondary' // Default boja ako tip nije definisan
                                            }`}>
                                                {notif.tip.replace(/_/g, ' ')} {/* Zamijeni underscore sa razmakom */}
                                            </span>
                                            <strong>{notif.sadrzaj}</strong>
                                            <br />
                                            <small>{new Date(notif.datum).toLocaleString('bs-BA')}</small> {/* Formatiranje datuma za BiH */}
                                        </div>
                                        {!notif.procitana && (
                                            <Button
                                                variant="outline-secondary"
                                                size="sm"
                                                onClick={() => handleMarkNotificationAsRead(notif.id)}
                                            >
                                                Označi kao Pročitano
                                            </Button>
                                        )}
                                        {notif.procitana && (
                                            <span className="text-success small">{getTranslatedStatus('PROCITANO')}</span>
                                        )}
                                    </li>
                                ))}
                            </ul>
                        ) : (
                            <p>Nema pronađenih obavijesti.</p>
                        )}
                        <Pagination
                            itemsPerPage={itemsPerPage}
                            totalItems={notifications.length}
                            currentPage={notificationsCurrentPage}
                            paginate={setNotificationsCurrentPage}
                        />
                    </div>
                );
            case 'settings':
                return (
                    <div className="container my-4">
                        <h4 className="mb-4">Postavke profila doktora</h4>
                        {!currentDoctorProfile ? (
                            <p>Učitavanje profila doktora...</p>
                        ) : (
                            isEditingProfile ? (
                                <Form onSubmit={handleUpdateDoctorProfile}>
                                    <div className="row mb-3">
                                        <Form.Group className="col-md-6 mb-3">
                                            <Form.Label>Ime</Form.Label>
                                            <Form.Control type="text" value={editIme} onChange={(e) => setEditIme(e.target.value)} required />
                                        </Form.Group>
                                        <Form.Group className="col-md-6 mb-3">
                                            <Form.Label>Prezime</Form.Label>
                                            <Form.Control type="text" value={editPrezime} onChange={(e) => setEditPrezime(e.target.value)} required />
                                        </Form.Group>
                                    </div>
                                    <div className="row mb-3">
                                        <Form.Group className="col-md-6 mb-3">
                                            <Form.Label>Email</Form.Label>
                                            <Form.Control type="email" value={editEmail} onChange={(e) => setEditEmail(e.target.value)} required />
                                        </Form.Group>
                                        <Form.Group className="col-md-6 mb-3">
                                            <Form.Label>Telefon</Form.Label>
                                            <Form.Control type="text" value={editTelefon} onChange={(e) => setEditTelefon(e.target.value)} required />
                                        </Form.Group>
                                    </div>
                                    <Form.Group className="mb-3">
                                        <Form.Label>Specijalnosti (odvojene zarezom)</Form.Label>
                                        <Form.Control type="text" value={editSpecijalizacije} onChange={(e) => setEditSpecijalizacije(e.target.value)} />
                                    </Form.Group>
                                    <div className="row mb-3">
                                        <Form.Group className="col-md-6 mb-3">
                                            <Form.Label>Grad</Form.Label>
                                            <Form.Control type="text" value={editGrad} onChange={(e) => setEditGrad(e.target.value)} />
                                        </Form.Group>
                                        <Form.Group className="col-md-6 mb-3">
                                            <Form.Label>Radno vrijeme</Form.Label>
                                            <Form.Control type="text" value={editRadnoVrijeme} onChange={(e) => setEditRadnoVrijeme(e.target.value)} />
                                        </Form.Group>
                                    </div>
                                    <Form.Group className="mb-3">
                                        <Form.Label>Iskustvo (godine)</Form.Label>
                                        <Form.Control type="number" value={editIskustvo} onChange={(e) => setEditIskustvo(e.target.value)} />
                                    </Form.Group>
                                    <Form.Group className="mb-3">
                                        <Form.Label>Profilna slika</Form.Label>
                                        <Form.Control
                                            type="file"
                                            accept="image/*"
                                            onChange={handleImageChange}
                                        />
                                        {profileImagePreview && (
                                            <div className="mt-2">
                                                <img
                                                    src={profileImagePreview}
                                                    alt="Pregled profilne slike"
                                                    style={{ maxWidth: '100px', maxHeight: '100px', borderRadius: '50%', objectFit: 'cover' }}
                                                    className="border border-secondary"
                                                />
                                            </div>
                                        )}
                                    </Form.Group>
                                    <div className="d-flex justify-content-end">
                                        <Button variant="secondary" className="me-2" onClick={() => setIsEditingProfile(false)}>Poništi</Button>
                                        <Button variant="primary" type="submit">Sačuvaj izmjene</Button>
                                    </div>
                                </Form>
                            ) : (
                                <div>
                                    <p>
                                        {currentDoctorProfile.profileImageBase64 ? (
                                            <img
                                                src={currentDoctorProfile.profileImageBase64}
                                                alt="Profil doktora"
                                                style={{ width: '100px', height: '100px', borderRadius: '50%', objectFit: 'cover' }}
                                                className="mb-3 border border-secondary"
                                            />
                                        ) : (
                                            <img
                                                src="https://placehold.co/100x100/CCCCCC/000000?text=Nema+slike"
                                                alt="Nema profilne slike"
                                                style={{ width: '100px', height: '100px', borderRadius: '50%', objectFit: 'cover' }}
                                                className="mb-3 border border-secondary"
                                            />
                                        )}
                                    </p>
                                    <p><strong>Ime:</strong> {currentDoctorProfile.ime}</p>
                                    <p><strong>Prezime:</strong> {currentDoctorProfile.prezime}</p>
                                    <p><strong>Email:</strong> {currentDoctorProfile.email}</p>
                                    <p><strong>Telefon:</strong> {currentDoctorProfile.telefon}</p>
                                    <p><strong>Specijalnosti:</strong> {Array.isArray(currentDoctorProfile.specijalizacije) ? currentDoctorProfile.specijalizacije.join(', ') : 'N/A'}</p>
                                    <p><strong>Grad:</strong> {currentDoctorProfile.grad}</p>
                                    <p><strong>Radno vrijeme:</strong> {currentDoctorProfile.radnoVrijeme}</p>
                                    <p><strong>Iskustvo:</strong> {currentDoctorProfile.iskustvo} godina</p>
                                    <p><strong>Ocjena:</strong> {currentDoctorProfile.ocjena}</p>
                                    <Button variant="primary" onClick={() => setIsEditingProfile(true)}>Uredi profil</Button>
                                </div>
                            )
                        )}
                    </div>
                );
            default:
                return <div>Odaberite karticu iz bočnog menija.</div>;
        }
    };

    return (
        <div className="d-flex" style={{ minHeight: '100vh' }}>
            <div className="bg-dark text-white p-4" style={{ width: '250px' }}>
                <h3 className="mb-4">Doktorski panel</h3>
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
                            className={`btn btn-link text-white text-decoration-none w-100 text-start ${activeTab === 'my-examinations' ? 'active bg-secondary rounded' : ''}`}
                            onClick={() => setActiveTab('my-examinations')}
                        >
                            Moji pregledi
                        </button>
                    </li>
                    <li className="nav-item mb-2">
                        <button
                            className={`btn btn-link text-white text-decoration-none w-100 text-start ${activeTab === 'my-patients' ? 'active bg-secondary rounded' : ''}`}
                            onClick={() => setActiveTab('my-patients')}
                        >
                            Moji pacijenti
                        </button>
                    </li>
                    <li className="nav-item mb-2">
                        <button
                            className={`btn btn-link text-white text-decoration-none w-100 text-start ${activeTab === 'manage-terms' ? 'active bg-secondary rounded' : ''}`}
                            onClick={() => setActiveTab('manage-terms')}
                        >
                            Upravljanje terminima
                        </button>
                    </li>
                    <li className="nav-item mb-2">
                        <button
                            className={`btn btn-link text-white text-decoration-none w-100 text-start ${activeTab === 'documents' ? 'active bg-secondary rounded' : ''}`}
                            onClick={() => setActiveTab('documents')}
                        >
                            Dokumenti
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
                            className={`btn btn-link text-white text-decoration-none w-100 text-start ${activeTab === 'notifications' ? 'active bg-secondary rounded' : ''}`}
                            onClick={() => setActiveTab('notifications')}
                        >
                            Obavijesti
                        </button>
                    </li>
                    <li className="nav-item mb-2">
                        <button
                            className={`btn btn-link text-white text-decoration-none w-100 text-start ${activeTab === 'settings' ? 'active bg-secondary rounded' : ''}`}
                            onClick={() => setActiveTab('settings')}
                        >
                            Postavke
                        </button>
                    </li>
                </ul>
            </div>

            <div className="flex-grow-1 p-4 bg-light">
                {showAlert && (
                    <div className={`alert alert-${alertType} alert-dismissible fade show position-fixed top-0 start-50 translate-middle-x mt-3`} role="alert" style={{ zIndex: 1050 }}>
                        {alertMessage}
                        <button type="button" className="btn-close" onClick={() => setShowAlert(false)}></button>
                    </div>
                )}

                <div className="d-flex justify-content-between align-items-center mb-4">
                    <h2 className="mb-0">Doktorska nadzorna ploča</h2>
                </div>

                {renderContent()}
            </div>

            <Modal show={showPatientDetailModal} onHide={closePatientDetailModal} size="lg" key={selectedPatient?.id || 'patient-modal'}>
                <Modal.Header closeButton>
                    <Modal.Title>Detalji pacijenta: {selectedPatient?.name}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    {selectedPatient && (
                        <div>
                            <p><strong>Email:</strong> {selectedPatient.email}</p>
                            <p><strong>Telefon:</strong> {selectedPatient.phone}</p>
                            <hr />
                            <h5>Historija pregleda za {selectedPatient.name}</h5>
                            {doctorsExaminations.filter(exam => exam.pacijentId === selectedPatient.id).length > 0 ? (
                                <ul className="list-group mb-3">
                                    {doctorsExaminations
                                        .filter(exam => exam.pacijentId === selectedPatient.id)
                                        .sort((a, b) => new Date(b.date) - new Date(a.date))
                                        .map(exam => (
                                            <li key={exam.id} className="list-group-item">
                                                <strong>Datum:</strong> {new Date(exam.date).toLocaleDateString()}, {' '}
                                                <strong>Vrijeme:</strong> {exam.time}, {' '}
                                                <strong>Status:</strong> <span className={`badge ${
                                                    exam.status === 'zakazan' ? 'bg-warning text-dark' :
                                                    exam.status === 'obavljen' ? 'bg-success' :
                                                    exam.status === 'otkazan' ? 'bg-danger' :
                                                    'bg-secondary'
                                                }`}>{getTranslatedStatus(exam.status)}</span>
                                            </li>
                                        ))}
                                </ul>
                            ) : (
                                <p>Nema pronađene historije pregleda za ovog pacijenta.</p>
                            )}
                            <hr />
                            <h5>Unosi medicinske historije za {selectedPatient.name}</h5>
                            <Form.Group className="mb-3">
                                <Form.Label>Dijagnoza</Form.Label>
                                <Form.Control
                                    type="text"
                                    name="diagnosis"
                                    value={newMedicalHistoryEntry.diagnosis}
                                    onChange={(e) => setNewMedicalHistoryEntry(prev => ({ ...prev, diagnosis: e.target.value }))}
                                    placeholder="Unesite dijagnozu..."
                                />
                            </Form.Group>
                            <Form.Group className="mb-3">
                                <Form.Label>Liječenje</Form.Label>
                                <Form.Control
                                    as="textarea"
                                    rows={3}
                                    name="treatment"
                                    value={newMedicalHistoryEntry.treatment}
                                    onChange={(e) => setNewMedicalHistoryEntry(prev => ({ ...prev, treatment: e.target.value }))}
                                    placeholder="Unesite detalje liječenja..."
                                />
                            </Form.Group>
                            <Form.Group className="mb-3">
                                <Form.Label>Bilješke</Form.Label>
                                <Form.Control
                                    as="textarea"
                                    rows={3}
                                    name="notes"
                                    value={newMedicalHistoryEntry.notes}
                                    onChange={(e) => setNewMedicalHistoryEntry(prev => ({ ...prev, notes: e.target.value }))}
                                    placeholder="Dodatne bilješke..."
                                />
                            </Form.Group>
                            <Button variant="primary" onClick={handleAddMedicalHistoryEntry}>Dodaj unos medicinske historije</Button>

                            <h6 className="mt-4">Postojeći zapisi medicinske historije:</h6>
                            {medicalHistoryEntries.length > 0 ? (
                                <ul className="list-group">
                                    {medicalHistoryEntries.map(record => (
                                        <li key={record.id} className="list-group-item">
                                            <strong>Datum:</strong> {new Date(record.date).toLocaleDateString()} <br />
                                            <strong>Dijagnoza:</strong> {record.diagnosis} <br />
                                            <strong>Liječenje:</strong> {record.treatment} <br />
                                            <strong>Bilješke:</strong> {record.notes || 'N/A'}
                                        </li>
                                    ))}
                                </ul>
                            ) : (
                                <p>Nema zapisa medicinske historije za ovog pacijenta.</p>
                            )}
                        </div>
                    )}
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={closePatientDetailModal}>
                        Zatvori
                    </Button>
                </Modal.Footer>
            </Modal>
        </div>
    );
}

export default DoctorDashboard;
