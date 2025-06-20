import React, { useState, useEffect, useMemo, useRef } from 'react';
import axios from 'axios';
import { useAuth } from '../contexts/AuthContext';
import 'bootstrap/dist/css/bootstrap.min.css';
import { Modal, Button, Form, Alert } from 'react-bootstrap';

const GATEWAY_BASE_URL = 'http://localhost:8081';

const KORISNICI_DOKTORI_API_PREFIX = `${GATEWAY_BASE_URL}/api/korisnici-doktori`;
const TERMINI_PREGLEDI_API_PREFIX = `${GATEWAY_BASE_URL}/api/termini-pregledi`;
const AUTH_API_PREFIX = `${GATEWAY_BASE_URL}/auth`;

function AdminDashboard() {
  const { token, loading: authLoading } = useAuth();
  const [loadingInitial, setLoadingInitial] = useState(true);

  const [activeTab, setActiveTab] = useState(() => {
    const savedTab = localStorage.getItem('adminActiveTab');
    return savedTab || 'overview';
  });

  const [doctors, setDoctors] = useState([]);
  const [patients, setPatients] = useState([]);
  const [examinations, setExaminations] = useState([]);
  const [terms, setTerms] = useState([]);

  const [showDoctorModal, setShowDoctorModal] = useState(false);
  const [editingDoctor, setEditingDoctor] = useState(null);
  const [showPatientModal, setShowPatientModal] = useState(false);
  const [editingPatient, setEditingPatient] = useState(null);
  const [showTermModal, setShowTermModal] = useState(false);
  const [editingTerm, setEditingTerm] = useState(null);
  const [showExaminationModal, setShowExaminationModal] = useState(false);
  const [editingExamination, setEditingExamination] = useState(null);

  const [showAlert, setShowAlert] = useState(false);
  const [alertMessage, setAlertMessage] = useState('');
  const [alertType, setAlertType] = useState('success');

  const [doctorSearchTerm, setDoctorSearchTerm] = useState('');
  const [patientSearchTerm, setPatientSearchTerm] = useState('');
  const [termSearchTerm, setTermSearchTerm] = useState('');
  const [examinationSearchTerm, setExaminationSearchTerm] = useState('');

  const [doctorsCurrentPage, setDoctorsCurrentPage] = useState(1);
  const [patientsCurrentPage, setPatientsCurrentPage] = useState(1);
  const [termsCurrentPage, setTermsCurrentPage] = useState(1);
  const examinationsCurrentPage = useRef(1);
  const itemsPerPage = 5;

  // Function to display notifications
  const showNotification = (message, type = 'success') => {
    setAlertMessage(message);
    setAlertType(type);
    setShowAlert(true);
    setTimeout(() => {
      setShowAlert(false);
      setAlertMessage('');
    }, 3000);
  };

  // Memoized authorization headers for API calls
  const authHeaders = useMemo(() => ({
    headers: {
      'Authorization': `Bearer ${token}`
    }
  }), [token]);

  // Function to fetch data
  const fetchDoctors = async () => {
    try {
      const response = await axios.get(`${KORISNICI_DOKTORI_API_PREFIX}/api/doktori`);
      const transformedDoctors = response.data.map(doc => ({
        id: doc.doktorID,
        userId: doc.userId,
        name: `${doc.ime} ${doc.prezime}`,
        ime: doc.ime,
        prezime: doc.prezime,
        specialty: Array.isArray(doc.specijalizacije) && doc.specijalizacije.length > 0
                   ? doc.specijalizacije.join(', ')
                   : 'N/A',
        specijalizacije: doc.specijalizacije,
        city: doc.grad || 'N/A',
        grad: doc.grad,
        email: doc.email || 'N/A',
        telefon: doc.telefon || 'N/A',
        radnoVrijeme: doc.radnoVrijeme || '',
        iskustvo: doc.iskustvo !== null && doc.iskustvo !== undefined ? doc.iskustvo : '',
        ocjena: doc.ocjena !== null && doc.ocjena !== undefined ? doc.ocjena : 0.0,
      }));
      setDoctors(transformedDoctors);
    } catch (error) {
      console.error("Error fetching doctors:", error.response?.data || error.message);
      showNotification("Error fetching doctors from server.", "danger");
    }
  };

  const fetchPatients = async () => {
    try {
      const response = await axios.get(`${KORISNICI_DOKTORI_API_PREFIX}/api/pacijenti`);
      const transformedPatients = response.data.map(pat => ({
        id: pat.pacijentID,
        userId: pat.userId,
        name: `${pat.ime} ${pat.prezime}`,
        ime: pat.ime,
        prezime: pat.prezime,
        email: pat.email || 'N/A',
        phone: pat.telefon || 'N/A',
        telefon: pat.telefon,
      }));
      setPatients(transformedPatients);
    } catch (error) {
      console.error("Error fetching patients:", error.response?.data || error.message);
      showNotification("Error fetching patients from server.", "danger");
    }
  };

  const fetchTerms = async () => {
    try {
      const response = await axios.get(`${TERMINI_PREGLEDI_API_PREFIX}/api/termini`);
      const transformedTerms = response.data.map(term => ({
        id: term.terminID,
        doktorID: term.doktorID,
        datum: term.datum,
        vrijeme: term.vrijeme,
        statusTermina: term.statusTermina,
      }));
      setTerms(transformedTerms);
    } catch (error) {
      console.error("Error fetching terms:", error.response?.data || error.message);
      showNotification("Error fetching terms from server.", "danger");
    }
  };

  const fetchExaminations = async () => {
    try {
      const response = await axios.get(`${TERMINI_PREGLEDI_API_PREFIX}/api/pregledi`);
      const transformedExaminations = response.data.map(app => ({
        id: app.pregledID,
        pacijentID: app.pacijentID,
        doktorID: app.doktorID,
        patientName: app.pacijentIme || 'N/A',
        doctorName: app.doktorIme || 'N/A',
        datumPregleda: app.datumPregleda,
        vrijemePregleda: app.vrijemePregleda,
        status: app.status,
        komentarPacijenta: app.komentarPacijenta || '',
        ocjenaDoktora: app.ocjenaDoktora || null,
        terminID: app.terminID !== null && app.terminID !== undefined ? app.terminID : 'N/A',
      }));
      setExaminations(transformedExaminations);
    } catch (error) {
      console.error("Error fetching examinations:", error.response?.data || error.message);
      showNotification("Error fetching examinations from server.", "danger");
    }
  };

  // MAIN USEEFFECT - CHECKS AUTHENTICATION DATA AVAILABILITY
  useEffect(() => {
    console.log("AdminDashboard useEffect: token=", token, "authLoading=", authLoading);

    if (!authLoading) {
      if (token) {
        console.log("AdminDashboard useEffect: Authentication complete, token available. Initiating data fetch.");
        const initializeDashboard = async () => {
          try {
            await Promise.all([
              fetchDoctors(),
              fetchPatients(),
              fetchTerms(),
              fetchExaminations()
            ]);
            showNotification("Dashboard data successfully loaded!", "success");
          } catch (error) {
            console.error("Error during initial data fetch:", error);
            showNotification("Error loading initial data. Please try again.", "danger");
          } finally {
            setLoadingInitial(false);
          }
        };
        initializeDashboard();
      } else {
        console.warn("AdminDashboard useEffect: Token not available after authentication loading. User not logged in/authorized.");
        setLoadingInitial(false);
      }
    }
  }, [token, authLoading]);

  // useEffect for tab persistence
  useEffect(() => {
    localStorage.setItem('adminActiveTab', activeTab);
  }, [activeTab]);


  // Functions for adding, updating, deleting entities
  const handleAddDoctor = async (newDoctorData) => {
        try {
          const registerPayload = {
            email: newDoctorData.email,
            password: newDoctorData.password,
            fullName: `${newDoctorData.ime} ${newDoctorData.prezime}`,
            telefon: newDoctorData.telefon,
            roles: ["ROLE_DOKTOR"],
            grad: newDoctorData.grad,
            specijalizacije: newDoctorData.specijalizacije,
            radnoVrijeme: newDoctorData.radnoVrijeme,
            iskustvo: newDoctorData.iskustvo,
            ocjena: newDoctorData.ocjena || 0.0,
          };

          const authResponse = await axios.post(`${AUTH_API_PREFIX}/register`, registerPayload);
          console.log("Auth Service Register Response Data:", authResponse.data);

          return { success: true, message: 'New doctor successfully registered!' };
        } catch (error) {
          console.error("Error adding doctor, full error object:", error);
          let errorMessage = 'Error adding doctor. Check your input.';

          if (error.response) {
              console.log("Error.response:", error.response);
              console.log("Error.response.data:", error.response.data);

              if (typeof error.response.data === 'string') {
                  errorMessage = error.response.data;
              } else if (error.response.data.message) {
                  errorMessage = error.response.data.message;
              } else if (error.response.data.error) {
                  errorMessage = error.response.data.error;
              } else {
                  errorMessage = "Unknown backend error: " + JSON.stringify(error.response.data);
              }
          } else if (error.message) {
              errorMessage = error.message;
          } else {
              errorMessage = "An unexpected client-side error occurred.";
          }

          if (errorMessage.toLowerCase().includes("email already exists") || errorMessage.toLowerCase().includes("email već postoji")) {
              errorMessage = "User with this email already exists.";
          } else if (errorMessage.toLowerCase().includes("password") && errorMessage.toLowerCase().includes("size")) {
              errorMessage = "Password must be at least 6 characters long.";
          } else if (errorMessage.toLowerCase().includes("bad request") && errorMessage.toLowerCase().includes("validation failed")) {
              errorMessage = "Validation error. Check entered data.";
          }

          console.log("Final error message for adding doctor:", errorMessage);
          return { success: false, message: errorMessage };
        }
      };

       const handleUpdateDoctor = async (updatedDoctor) => {
           try {
             if (!updatedDoctor.id) {
                 return { success: false, message: 'Doctor ID is missing for update.' };
             }

             const doctorUpdatePayload = {
               ime: updatedDoctor.ime,
               prezime: updatedDoctor.prezime,
               email: updatedDoctor.email,
               telefon: updatedDoctor.telefon,
               grad: updatedDoctor.grad,
               specijalizacije: updatedDoctor.specijalizacije,
               radnoVrijeme: updatedDoctor.radnoVrijeme || '',
               iskustvo: updatedDoctor.iskustvo !== null && updatedDoctor.iskustvo !== undefined ? updatedDoctor.iskustvo : 0,
               ocjena: updatedDoctor.ocjena || 0.0,
             };
             await axios.put(`${KORISNICI_DOKTORI_API_PREFIX}/api/doktori/${updatedDoctor.id}`, doctorUpdatePayload);

             const userUpdatePayload = {
               email: updatedDoctor.email,
               fullName: `${updatedDoctor.ime} ${updatedDoctor.prezime}`,
               telefon: updatedDoctor.telefon,
             };
             if (updatedDoctor.userId) {
                 await axios.put(`${AUTH_API_PREFIX}/users/${updatedDoctor.userId}`, userUpdatePayload);
             } else {
                 console.warn("No userId found for updating user in Auth service.");
             }

             return { success: true, message: 'Doctor successfully updated!' };
           } catch (error) {
             console.error("Error updating doctor, full error object:", error);
             let errorMessage = 'Error updating doctor. Check your input.';
             if (error.response) {
                 if (typeof error.response.data === 'string') {
                     errorMessage = error.response.data;
                 } else if (error.response.data.message) {
                     errorMessage = error.response.data.message;
                 } else if (error.response.data.error) {
                     errorMessage = error.response.data.error;
                 } else {
                     errorMessage = "Unknown backend error: " + JSON.stringify(error.response.data);
                 }
             } else if (error.message) {
                 errorMessage = error.message;
             } else {
                 errorMessage = "An unexpected client-side error occurred.";
             }
             console.log("Final error message for updating doctor:", errorMessage);
             return { success: false, message: errorMessage };
           }
         };

         const [showConfirmModal, setShowConfirmModal] = useState(false);
         const [confirmAction, setConfirmAction] = useState(() => () => {});
         const [confirmMessage, setConfirmMessage] = useState("");
         const [confirmTitle, setConfirmTitle] = useState("Confirmation");

         const showCustomConfirm = (message, action, title = "Confirmation") => {
           setConfirmMessage(message);
           setConfirmAction(() => action);
           setConfirmTitle(title);
           setShowConfirmModal(true);
         };

         const handleConfirmAction = () => {
           confirmAction();
           setShowConfirmModal(false);
         };

         const handleDeleteDoctor = async (id) => {
           showCustomConfirm(
             'Are you sure you want to delete this doctor? This action will also delete the user account.',
             async () => {
               try {
                 const doctorToDelete = doctors.find(doc => doc.id === id);
                 if (!doctorToDelete || !doctorToDelete.userId) {
                   showNotification('User ID not found for deleting doctor.', 'danger');
                   return;
                 }

                 await axios.delete(`${KORISNICI_DOKTORI_API_PREFIX}/api/doktori/${id}`);
                 await axios.delete(`${AUTH_API_PREFIX}/users/${doctorToDelete.userId}`);

                 fetchDoctors();
                 showNotification('Doctor successfully deleted!', 'danger');
               } catch (error) {
                 console.error("Error deleting doctor:", error.response?.data || error.message);
                 showNotification(error.response?.data?.message || 'Error deleting doctor.', "danger");
               }
             },
             "Confirm Doctor Deletion"
           );
         };

         const openDoctorModal = (doctor = null) => {
           setEditingDoctor(doctor);
           setShowDoctorModal(true);
         };

         const filteredDoctors = doctors.filter(doctor =>
           doctor.name.toLowerCase().includes(doctorSearchTerm.toLowerCase()) ||
           doctor.specialty.toLowerCase().includes(doctorSearchTerm.toLowerCase()) ||
           doctor.city.toLowerCase().includes(doctorSearchTerm.toLowerCase()) ||
           (doctor.email && doctor.email.toLowerCase().includes(doctorSearchTerm.toLowerCase())) ||
           (doctor.telefon && doctor.telefon.toLowerCase().includes(doctorSearchTerm.toLowerCase())) ||
           (doctor.radnoVrijeme && doctor.radnoVrijeme.toLowerCase().includes(doctorSearchTerm.toLowerCase()))
         );
         const indexOfLastDoctor = doctorsCurrentPage * itemsPerPage;
         const indexOfFirstDoctor = indexOfLastDoctor - itemsPerPage;
         const currentDoctors = filteredDoctors.slice(indexOfFirstDoctor, indexOfLastDoctor);
         const totalDoctorPages = Math.ceil(filteredDoctors.length / itemsPerPage);

         const handleAddPatient = async (newPatient) => {
           try {
             const registerPayload = {
               email: newPatient.email,
               password: newPatient.password,
               fullName: `${newPatient.ime} ${newPatient.prezime}`,
               telefon: newPatient.telefon,
               roles: ["ROLE_PACIJENT"],
               grad: null,
               specijalizacije: null,
               ocjena: null,
               iskustvo: null,
               radnoVrijeme: null,
             };
             const authResponse = await axios.post(`${AUTH_API_PREFIX}/register`, registerPayload);

             return { success: true, message: 'New patient successfully registered!' };

           } catch (error) {
             console.error("Error adding patient, full error object:", error);
             let errorMessage = 'Error adding patient. Check your input.';

             if (error.response) {
                if (Array.isArray(error.response.data.errors) && error.response.data.errors.length > 0) {
                    const passwordError = error.response.data.errors.find(err => err.field === 'password');
                    if (passwordError) {
                        errorMessage = `Password: ${passwordError.defaultMessage || 'Invalid password.'}`;
                    } else {
                        errorMessage = error.response.data.errors.map(err => err.defaultMessage || err.field).join('; ');
                    }
                } else if (typeof error.response.data === 'string') {
                    errorMessage = error.response.data;
                } else if (error.response.data.message) {
                    errorMessage = error.response.data.message;
                } else if (error.response.data.error) {
                    errorMessage = error.response.data.error;
                } else {
                    errorMessage = "Unknown backend error: " + JSON.stringify(error.response.data);
                }
             } else if (error.message) {
                errorMessage = error.message;
             } else {
                errorMessage = "An unexpected client-side error occurred.";
             }

             if (errorMessage.toLowerCase().includes("email already exists") || errorMessage.toLowerCase().includes("email već postoji")) {
                 errorMessage = "User with this email already exists.";
             } else if (errorMessage.toLowerCase().includes("password") && errorMessage.toLowerCase().includes("size")) {
                 errorMessage = "Password must be at least 6 characters long.";
             } else if (errorMessage.toLowerCase().includes("bad request") && errorMessage.toLowerCase().includes("validation failed")) {
                 errorMessage = "Validation error. Check entered data.";
             }

             console.log("Final error message for adding patient:", errorMessage);
             return { success: false, message: errorMessage };
           }
         };

  const handleUpdatePatient = async (updatedPatient) => {
    try {
      const patientUpdatePayload = {
        ime: updatedPatient.ime,
        prezime: updatedPatient.prezime,
        email: updatedPatient.email,
        telefon: updatedPatient.telefon,
      };
      await axios.put(`${KORISNICI_DOKTORI_API_PREFIX}/api/pacijenti/${updatedPatient.id}`, patientUpdatePayload);

      const userUpdatePayload = {
        email: updatedPatient.email,
        fullName: `${updatedPatient.ime} ${updatedPatient.prezime}`,
        telefon: updatedPatient.telefon,
      };
      if (updatedPatient.userId) {
          await axios.put(`${AUTH_API_PREFIX}/users/${updatedPatient.userId}`, userUpdatePayload);
      } else {
          console.warn("No userId found for updating user in Auth service.");
      }

      return { success: true, message: 'Patient successfully updated!' };
    } catch (error) {
      console.error("Error updating patient, full error object:", error);
      let errorMessage = 'Error updating patient. Check your input.';
      if (error.response) {
          if (typeof error.response.data === 'string') {
              errorMessage = error.response.data;
          } else if (error.response.data.message) {
              errorMessage = error.response.data.message;
          } else if (error.response.data.error) {
              errorMessage = error.response.data.error;
          } else {
              errorMessage = "Unknown backend error: " + JSON.stringify(error.response.data);
          }
      } else if (error.message) {
          errorMessage = error.message;
      } else {
          errorMessage = "An unexpected client-side error occurred.";
      }
      console.log("Final error message for updating patient:", errorMessage);
      return { success: false, message: errorMessage };
    }
  };

  const handleDeletePatient = async (id) => {
    showCustomConfirm(
      'Are you sure you want to delete this patient? This action will also delete the user account.',
      async () => {
        try {
          const patientToDelete = patients.find(pat => pat.id === id);
          if (!patientToDelete || !patientToDelete.userId) {
            showNotification('User ID not found for deleting patient.', 'danger');
            return;
          }

          await axios.delete(`${KORISNICI_DOKTORI_API_PREFIX}/api/pacijenti/${id}`);
          await axios.delete(`${AUTH_API_PREFIX}/users/${patientToDelete.userId}`);

          fetchPatients();
          showNotification('Patient successfully deleted!', 'success');
        } catch (error) {
          console.error("Error deleting patient:", error.response?.data || error.message);
          showNotification(error.response?.data?.message || 'Error deleting patient.', "danger");
        }
      },
      "Confirm Patient Deletion"
    );
  };

  const openPatientModal = (patient = null) => {
    setEditingPatient(patient);
    setShowPatientModal(true);
  };

  const filteredPatients = patients.filter(patient =>
    patient.name.toLowerCase().includes(patientSearchTerm.toLowerCase()) ||
    patient.email.toLowerCase().includes(patientSearchTerm.toLowerCase()) ||
    (patient.phone && patient.phone.toLowerCase().includes(patientSearchTerm.toLowerCase()))
  );
  const indexOfLastPatient = patientsCurrentPage * itemsPerPage;
  const indexOfFirstPatient = indexOfLastPatient - itemsPerPage;
  const currentPatients = filteredPatients.slice(indexOfFirstPatient, indexOfLastPatient);
  const totalPatientPages = Math.ceil(filteredPatients.length / itemsPerPage);

  // Helper function to format time to HH:MM:SS
  const formatTimeToHHMMSS = (timeString) => {
    if (!timeString) return '';
    const parts = timeString.split(':');
    let hours = parts[0] || '00';
    let minutes = parts[1] || '00';
    let seconds = parts[2] || '00'; // Assume seconds if present, default to 00

    // Pad with leading zeros if necessary
    hours = hours.padStart(2, '0');
    minutes = minutes.padStart(2, '0');
    seconds = seconds.padStart(2, '0');

    return `${hours}:${minutes}:${seconds}`;
  };

  const handleAddTerm = async (newTermData) => {
    // Check if doctorID exists
    if (!newTermData.doktorID) {
      return { success: false, message: 'Doctor ID is required for adding a term.' };
    }

    try {
      const termPayload = {
          doktorID: newTermData.doktorID,
          datum: newTermData.datum,
          vrijeme: formatTimeToHHMMSS(newTermData.vrijeme), // Use the helper function here
          statusTermina: newTermData.statusTermina
      };
      console.log("Adding new term with payload:", termPayload);

      // Add authorization header
      const response = await axios.post(`${TERMINI_PREGLEDI_API_PREFIX}/api/termini`, termPayload, authHeaders);

      showNotification("New term successfully added!", "success");
      fetchTerms(); // Refresh terms
      return { success: true, message: 'New term successfully added!' };

    } catch (error) {
      console.error("Error adding term, full error object:", error);
      let errorMessage = 'Error adding term. Check your input.';

      if (error.response) {
        if (typeof error.response.data === 'string') {
          errorMessage = error.response.data;
        } else if (error.response.data.message) {
          errorMessage = error.response.data.message;
        } else if (error.response.data.error) {
          errorMessage = error.response.data.error;
        } else {
          errorMessage = "Unknown backend error: " + JSON.stringify(error.response.data);
        }
      } else if (error.message) {
        errorMessage = error.message;
      } else {
        errorMessage = "An unexpected client-side error occurred.";
      }

      showNotification(errorMessage, "danger");
      return { success: false, message: errorMessage };
    }
  };


  const handleUpdateTerm = async (updatedTerm) => {
    try {
      if (!updatedTerm.id || !updatedTerm.doktorID) {
        return { success: false, message: 'Term ID and Doctor ID are required for update.' };
      }

      const termPayload = {
        doktorID: updatedTerm.doktorID,
        datum: updatedTerm.datum,
        vrijeme: formatTimeToHHMMSS(updatedTerm.vrijeme), // Use the helper function here
        statusTermina: updatedTerm.statusTermina
      };

      await axios.put(
        `${TERMINI_PREGLEDI_API_PREFIX}/api/termini/${updatedTerm.id}`,
        termPayload,
        authHeaders
      );

      showNotification("Term successfully updated!", "success");
      fetchTerms();
      return { success: true, message: 'Term successfully updated!' };

    } catch (error) {
      console.error("Error updating term, full error object:", error);
      let errorMessage = 'Error updating term. Check your input.';

      if (error.response) {
        if (typeof error.response.data === 'string') {
          errorMessage = error.response.data;
        } else if (error.response.data.message) {
          errorMessage = error.response.data.message;
        } else if (error.response.data.error) {
          errorMessage = error.response.data.error;
        } else {
          errorMessage = "Unknown backend error: " + JSON.stringify(error.response.data);
        }
      } else if (error.message) {
        errorMessage = error.message;
      } else {
        errorMessage = "An unexpected client-side error occurred.";
      }

      showNotification(errorMessage, "danger");
      return { success: false, message: errorMessage };
    }
  };


  const handleDeleteTerm = async (id) => {
    showCustomConfirm(
      'Are you sure you want to delete this term?',
      async () => {
        try {
          await axios.delete(`${TERMINI_PREGLEDI_API_PREFIX}/api/termini/${id}`, authHeaders);
          showNotification('Term successfully deleted!', 'success');
          fetchTerms();
        } catch (error) {
          console.error("Error deleting term:", error.response?.data || error.message);
          showNotification(error.response?.data?.message || 'Error deleting term.', "danger");
        }
      },
      "Confirm Term Deletion"
    );
  };

  const openTermModal = (term = null) => {
    if (term) {
        setEditingTerm({
          ...term,
          datum: term.datum,
          vrijeme: term.vrijeme ? term.vrijeme.substring(0, 5) : '', // Keep HH:mm for input field
          statusTermina: term.statusTermina,
        });
    } else {
        setEditingTerm(null);
    }
    setShowTermModal(true);
  };

  const filteredTerms = terms.filter(term =>
    (term.doktorID != null && term.doktorID.toString().toLowerCase().includes(termSearchTerm.toLowerCase())) ||
    (term.datum && term.datum.toLowerCase().includes(termSearchTerm.toLowerCase())) ||
    (term.vrijeme && term.vrijeme.toLowerCase().includes(termSearchTerm.toLowerCase())) ||
    (term.statusTermina && term.statusTermina.toLowerCase().includes(termSearchTerm.toLowerCase()))
  ).sort((a, b) => { // Sort by date and time
        const dateComparison = a.datum.localeCompare(b.datum);
        if (dateComparison !== 0) {
            return dateComparison;
        }
        return a.vrijeme.localeCompare(b.vrijeme);
    });

  const indexOfLastTerm = termsCurrentPage * itemsPerPage;
  const indexOfFirstTerm = indexOfLastTerm - itemsPerPage;
  const currentTerms = filteredTerms.slice(indexOfFirstTerm, indexOfLastTerm);
  const totalTermPages = Math.ceil(filteredTerms.length / itemsPerPage);

  const handleUpdateExamination = async (updatedExamination) => {
    try {
      if (!updatedExamination.id) {
        return { success: false, message: 'Examination ID is missing for update.' };
      }
      const payload = {
        pacijentID: updatedExamination.pacijentID,
        doktorID: updatedExamination.doktorID,
        terminID: updatedExamination.terminID,
        datumPregleda: updatedExamination.datumPregleda,
        vrijemePregleda: updatedExamination.vrijemePregleda,
        status: updatedExamination.status,
        komentarPacijenta: updatedExamination.komentarPacijenta,
        ocjenaDoktora: updatedExamination.ocjenaDoktora
      };
      await axios.put(`${TERMINI_PREGLEDI_API_PREFIX}/api/pregledi/${updatedExamination.id}`, payload, authHeaders);
      showNotification('Examination successfully updated!', 'success');
      fetchExaminations(); // Refresh examination list after update
      return { success: true, message: 'Examination successfully updated!' };
    } catch (error) {
      console.error("Error updating examination, full error object:", error);
      let errorMessage = 'Error updating examination. Check your input.';
      if (error.response) {
          if (typeof error.response.data === 'string') {
              errorMessage = error.response.data;
          } else if (error.response.data.message) {
              errorMessage = error.response.data.message;
          } else if (error.response.data.error) {
              errorMessage = error.response.data.error;
          } else {
              errorMessage = "Unknown backend error: " + JSON.stringify(error.response.data);
          }
      } else if (error.message) {
          errorMessage = error.message;
      } else {
          errorMessage = "An unexpected client-side error occurred.";
      }
      console.log("Final error message for updating examination:", errorMessage);
      return { success: false, message: errorMessage };
    }
  };

  const handleDeleteExamination = async (id) => {
    showCustomConfirm(
      'Are you sure you want to delete this examination?',
      async () => {
        try {
          await axios.delete(`${TERMINI_PREGLEDI_API_PREFIX}/api/pregledi/${id}`, authHeaders);
          showNotification('Examination successfully deleted!', 'success');
          fetchExaminations();
        } catch (error) {
          console.error("Error deleting examination:", error.response?.data || error.message);
          showNotification(error.response?.data?.message || 'Error deleting examination.', "danger");
        }
      },
      "Confirm Examination Deletion"
    );
  };

  const openExaminationModal = (examination) => {
    setEditingExamination({
        ...examination,
        pacijentID: examination.pacijentID,
        doktorID: examination.doktorID,
        terminID: examination.terminID,
        datumPregleda: examination.datumPregleda,
        vrijemePregleda: examination.vrijemePregleda
    });
    setShowExaminationModal(true);
  };

  const filteredExaminations = examinations.filter(exam =>
    (exam.patientName && exam.patientName.toLowerCase().includes(examinationSearchTerm.toLowerCase())) ||
    (exam.doctorName && exam.doctorName.toLowerCase().includes(examinationSearchTerm.toLowerCase())) ||
    (exam.status && exam.status.toLowerCase().includes(examinationSearchTerm.toLowerCase())) ||
    (exam.datumPregleda && exam.datumPregleda.toLowerCase().includes(examinationSearchTerm.toLowerCase())) ||
    (exam.vrijemePregleda && exam.vrijemePregleda.toLowerCase().includes(examinationSearchTerm.toLowerCase()))
  );
  const indexOfLastExamination = examinationsCurrentPage.current * itemsPerPage;
  const indexOfFirstExamination = indexOfLastExamination - itemsPerPage;
  const currentExaminations = filteredExaminations.slice(indexOfFirstExamination, indexOfLastExamination);
  const totalExaminationPages = Math.ceil(filteredExaminations.length / itemsPerPage);

  const addDoctorFields = [
      { name: 'ime', label: 'Ime', type: 'text', required: true },
      { name: 'prezime', label: 'Prezime', type: 'text', required: true },
      { name: 'email', label: 'Email', type: 'email', required: true },
      { name: 'password', label: 'Lozinka', type: 'password', required: true },
      { name: 'telefon', label: 'Telefon', type: 'text', required: true },
      { name: 'grad', label: 'Grad', type: 'select', required: true, options: ['Sarajevo', 'Mostar', 'Zenica', 'Banja Luka', 'Tuzla', 'Bihać'] },
      { name: 'specijalizacije', label: 'Specijalnost', type: 'select', required: true, options: ['Kardiolog', 'Neurolog', 'Ortoped', 'Pedijatar', 'Hirurg', 'Dermatolog', 'Oftamolog', 'Psihijatar'] },
      { name: 'radnoVrijeme', label: 'Radno vrijeme', type: 'text' },
      { name: 'iskustvo', label: 'Godine iskustva', type: 'number' },
  ];

  const editDoctorFields = [
      { name: 'id', label: 'ID Doktora', type: 'text', readOnly: true },
      { name: 'ime', label: 'Ime', type: 'text', required: true },
      { name: 'prezime', label: 'Prezime', type: 'text', required: true },
      { name: 'email', label: 'Email', type: 'email', required: true },
      { name: 'telefon', label: 'Telefon', type: 'text', required: true },
      { name: 'grad', label: 'Grad', type: 'select', required: true, options: ['Sarajevo', 'Mostar', 'Zenica', 'Banja Luka', 'Tuzla', 'Bihać'] },
      { name: 'specijalizacije', label: 'Specijalnost', type: 'select', required: true, options: ['Kardiolog', 'Neurolog', 'Ortoped', 'Pedijatar', 'Hirurg', 'Dermatolog', 'Oftamolog', 'Psihijatar'] },
      { name: 'radnoVrijeme', label: 'Radno vrijeme', type: 'text' },
      { name: 'iskustvo', label: 'Godine iskustva', type: 'number' },
      { name: 'ocjena', label: 'Ocjena', type: 'number', readOnly: true },
  ];

  const addPatientFields = [
      { name: 'ime', label: 'Ime', type: 'text', required: true },
      { name: 'prezime', label: 'Prezime', type: 'text', required: true },
      { name: 'email', label: 'Email', type: 'email', required: true },
      { name: 'password', label: 'Lozinka', type: 'password', required: true },
      { name: 'telefon', label: 'Telefon', type: 'text', required: true },
  ];

  const editPatientFields = [
      { name: 'id', label: 'ID Pacijenta', type: 'text', readOnly: true },
      { name: 'ime', label: 'Ime', type: 'text', required: true },
      { name: 'prezime', label: 'Prezime', type: 'text', required: true },
      { name: 'email', label: 'Email', type: 'email', required: true },
      { name: 'telefon', label: 'Telefon', type: 'text', required: true },
  ];

  const addTermFields = [
      { name: 'doktorID', label: 'ID Doktora', type: 'number', required: true },
      { name: 'datum', label: 'Datum', type: 'date', required: true },
      { name: 'vrijeme', label: 'Vrijeme (HH:mm)', type: 'time', required: true },
      { name: 'statusTermina', label: 'Status', type: 'select', options: ['DOSTUPAN', 'ZAKAZAN', 'OTKAZAN'], required: true },
  ];

  const editTermFields = [
      { name: 'id', label: 'ID Termina', type: 'text', readOnly: true },
      { name: 'doktorID', label: 'ID Doktora', type: 'number', required: true },
      { name: 'datum', label: 'Datum', type: 'date', required: true },
      { name: 'vrijeme', label: 'Vrijeme (HH:mm)', type: 'time', required: true },
      { name: 'statusTermina', label: 'Status', type: 'select', options: ['DOSTUPAN', 'ZAKAZAN', 'OTKAZAN'], required: true },
  ];

  const examinationStatusMap = {
    'zakazan': 'Zakazan',
    'obavljen': 'Obavljen',
    'otkazan': 'Otkazan',
    '': 'Nepoznat',
    null: 'Nepoznat'
  };

  const getTranslatedExaminationStatus = (status) => {
    return examinationStatusMap[status] || status;
  };

  const editExaminationFields = [
      { name: 'id', label: 'ID Pregleda', type: 'text', readOnly: true },
      { name: 'pacijentID', label: 'ID Pacijenta', type: 'number', required: true, readOnly: true },
      { name: 'terminID', label: 'ID Termina', type: 'number', required: true, readOnly: true },
      { name: 'doktorID', label: 'ID Doktora', type: 'number', required: true, readOnly: true },
      { name: 'datumPregleda', label: 'Datum pregleda', type: 'date', required: true, readOnly: true },
      { name: 'vrijemePregleda', label: 'Vrijeme pregleda', type: 'time', required: true, readOnly: true },
      { name: 'status', label: 'Status', type: 'select', options: ['zakazan', 'obavljen', 'otkazan'], required: true },
      { name: 'komentarPacijenta', label: 'Komentar pacijenta', type: 'textarea' },
      { name: 'ocjenaDoktora', label: 'Ocjena doktora', type: 'number' },
  ];

  // Function to convert date to local ISO format (YYYY-MM-DD)
  const toLocalISOString = (dateString) => {
      if (!dateString) return '';
      if (dateString.match(/^\d{4}-\d{2}-\d{2}$/)) {
          return dateString;
      }
      const d = new Date(dateString);
      d.setMinutes(d.getMinutes() - d.getTimezoneOffset());
      return d.toISOString().slice(0, 10);
  };

  // DataModal component for entering/editing data
  const DataModal = ({ show, onClose, data, onSave, fields, title, idPrefix, onSuccess }) => {
    const prevTerminIDRef = useRef(null);
    const [shouldFetchTermDetails, setShouldFetchTermDetails] = useState(false);
    const [formData, setFormData] = useState({});

    const [localValidationErrors, setLocalValidationErrors] = useState({});
    const [apiErrorMessage, setApiErrorMessage] = useState('');

    useEffect(() => {
        const newFormData = {};
        fields.forEach(field => {
            if (data === null || data[field.name] === undefined || data[field.name] === null) {
                if (field.name === 'specijalizacije') {
                    newFormData[field.name] = '';
                } else if (field.type === 'number') {
                    newFormData[field.name] = null;
                } else if (field.name === 'statusTermina') {
                    newFormData[field.name] = 'DOSTUPAN'; // Default status for new terms
                } else {
                    newFormData[field.name] = '';
                }
            } else {
                let value = data[field.name];
                if (field.type === 'date' && typeof value === 'string') {
                    value = toLocalISOString(value); // Ensure "YYYY-MM-DD" format
                } else if (field.type === 'time' && typeof value === 'string' && value.length >= 5) {
                    value = value.substring(0, 5); // Ensure "HH:mm" format for input
                } else if (field.name === 'specijalizacije' && Array.isArray(value)) {
                    value = value[0] || ''; // Select the first specialty if it's an array
                }
                newFormData[field.name] = value;
            }
        });
        setFormData(newFormData);
        prevTerminIDRef.current = (idPrefix === 'examination' && data && typeof data.terminID === 'number' && data.terminID > 0) ? data.terminID : null;
        setShouldFetchTermDetails(false);
        setLocalValidationErrors({});
        setApiErrorMessage('');
    }, [data, fields, idPrefix]);

    const handleTerminIDBlur = () => {
        if (idPrefix === 'examination') {
            const currentTerminID = formData.terminID;
            const isNumericAndValidTerminID = typeof currentTerminID === 'number' && currentTerminID > 0;

            if (isNumericAndValidTerminID && currentTerminID !== prevTerminIDRef.current) {
                setShouldFetchTermDetails(true);
            } else if (!isNumericAndValidTerminID && prevTerminIDRef.current !== null) {
                setFormData(prev => ({
                    ...prev,
                    doktorID: null,
                    datumPregleda: '',
                    vrijemePregleda: '',
                }));
                prevTerminIDRef.current = null;
                setShouldFetchTermDetails(false);
            } else if (currentTerminID === null || currentTerminID === 0 || currentTerminID === '') {
                if (formData.doktorID !== null || formData.datumPregleda !== '' || formData.vrijemePregleda !== '') {
                    setFormData(prev => ({
                        ...prev,
                        doktorID: null,
                        datumPregleda: '',
                        vrijemePregleda: '',
                    }));
                }
                prevTerminIDRef.current = null;
                setShouldFetchTermDetails(false);
            }
        }
    };

    useEffect(() => {
        if (shouldFetchTermDetails && idPrefix === 'examination') {
            const terminIDToFetch = formData.terminID;
            if (!(typeof terminIDToFetch === 'number' && terminIDToFetch > 0)) {
                setShouldFetchTermDetails(false);
                return;
            }

            const fetchTermDetails = async () => {
                try {
                    const response = await axios.get(`${TERMINI_PREGLEDI_API_PREFIX}/api/termini/${terminIDToFetch}`);
                    const fetchedTerm = response.data;

                    setFormData(prev => {
                        const newState = {
                            ...prev,
                            doktorID: fetchedTerm.doktorID,
                            datumPregleda: toLocalISOString(fetchedTerm.datum),
                            vrijemePregleda: fetchedTerm.vrijeme.substring(0, 5),
                        };
                        return newState;
                    });
                    prevTerminIDRef.current = terminIDToFetch;
                } catch (error) {
                    setApiErrorMessage('Error fetching term details. Check term ID.');
                    setFormData(prev => ({
                        ...prev,
                        doktorID: null,
                        datumPregleda: '',
                        vrijemePregleda: '',
                    }));
                    prevTerminIDRef.current = null;
                } finally {
                    setShouldFetchTermDetails(false);
                }
            };
            fetchTermDetails();
        }
    }, [shouldFetchTermDetails, idPrefix, TERMINI_PREGLEDI_API_PREFIX, formData.terminID]);

    const handleChange = (e) => {
      const { name, value, type, checked } = e.target;

      setLocalValidationErrors(prev => {
          const newState = { ...prev };
          delete newState[name];
          return newState;
      });
      setApiErrorMessage('');

      setFormData(prev => {
        let newValue = value;

        if (name === 'specijalizacije') {
            newValue = value;
        } else if (type === 'number') {
            newValue = value === '' ? null : Number(value);
            if (isNaN(newValue)) {
                newValue = null;
            }
        } else if (type === 'checkbox') {
            newValue = checked;
        } else if (type === 'date') {
            newValue = value;
        }
        const newState = { ...prev, [name]: newValue };
        return newState;
      });
    };

    const handleSubmit = async (e) => {
      e.preventDefault();
      setLocalValidationErrors({});
      setApiErrorMessage('');

      const finalFormData = { ...formData };

      if (idPrefix === 'doctor' && typeof finalFormData.specijalizacije === 'string') {
          finalFormData.specijalizacije = finalFormData.specijalizacije ? [finalFormData.specijalizacije] : [];
      } else if (idPrefix === 'doctor' && finalFormData.specijalizacije === null) {
          finalFormData.specijalizacije = [];
      }

      let currentValidationErrors = {};
      fields.forEach(field => {
        if (field.required) {
          if (field.type === 'text' || field.type === 'textarea' || field.type === 'date' || field.type === 'time' || field.type === 'select') {
            if (!finalFormData[field.name] || finalFormData[field.name].toString().trim() === '') {
              currentValidationErrors[field.name] = `${field.label} is a required field.`;
            }
          } else if (field.type === 'number') {
            if (finalFormData[field.name] === null || finalFormData[field.name] === undefined || typeof finalFormData[field.name] !== 'number') {
                currentValidationErrors[field.name] = `${field.label} is a required field and must be a valid number.`;
            } else if (field.name === 'iskustvo' && finalFormData[field.name] < 0) {
                 currentValidationErrors[field.name] = `Years of experience cannot be negative.`;
            } else if (field.name === 'ocjena' && (finalFormData[field.name] < 0 || finalFormData[field.name] > 5)) {
                 currentValidationErrors[field.name] = `Rating must be between 0 and 5.`;
            }
          } else if (field.type === 'email') {
              if (!finalFormData[field.name] || !/\S+@\S+\.\S+/.test(finalFormData[field.name])) {
                  currentValidationErrors[field.name] = `Email address must be valid.`;
              }
          } else if (field.name === 'password') {
             if (!finalFormData[field.name] || finalFormData[field.name].toString().length < 6) {
                 currentValidationErrors[field.name] = `Password must be at least 6 characters long.`;
             }
          }
        }
        if (field.type === 'email' && finalFormData[field.name] && finalFormData[field.name].trim() !== '' && !/\S+@\S+\.\S+/.test(finalFormData[field.name])) {
            currentValidationErrors[field.name] = `Email address must be valid.`;
        }
      });

      setLocalValidationErrors(currentValidationErrors);

      if (Object.keys(currentValidationErrors).length > 0) {
        setApiErrorMessage('Please correct the errors in the form.');
        return;
      }

      fields.forEach(field => {
          if (field.type === 'number' && (finalFormData[field.name] === null || finalFormData[field.name] === undefined || finalFormData[field.name] === '')) {
              if (field.name === 'iskustvo' || field.name === 'ocjena') {
                  finalFormData[field.name] = 0;
              } else {
                  finalFormData[field.name] = null;
              }
          }
      });

      // No need for specific time formatting here, as it's done in handleAddTerm/handleUpdateTerm
      // if (idPrefix === 'term' && finalFormData.vrijeme) {
      //     if (finalFormData.vrijeme.length === 5) {
      //         finalFormData.vrijeme += ':00';
      //     }
      // } else if (idPrefix === 'examination' && finalFormData.vrijemePregleda) {
      //     if (finalFormData.vrijemePregleda.length === 5) {
      //         finalFormData.vrijemePregleda += ':00';
      //     }
      // }

      const result = await onSave(finalFormData);
      if (!result.success) {
          setApiErrorMessage(result.message);
      } else {
           showNotification(result.message, 'success');
           onClose();
           if (onSuccess) onSuccess();
      }
    };

    return (
      <Modal show={show} onHide={onClose} centered>
        <Modal.Header closeButton>
          <Modal.Title>{title}</Modal.Title>
        </Modal.Header>
        <Form onSubmit={handleSubmit}>
          <Modal.Body>
            {apiErrorMessage && (
              <Alert variant="danger" className="mb-3">
                {apiErrorMessage}
              </Alert>
            )}
            {fields.map(field => (
              <Form.Group className="mb-3" controlId={`${idPrefix}-${field.name}`} key={field.name}>
                <Form.Label>{field.label}</Form.Label>
                {field.type === 'select' ? (
                  <Form.Select
                    name={field.name}
                    value={formData[field.name] === null ? '' : formData[field.name]}
                    onChange={handleChange}
                    required={field.required}
                    isInvalid={!!localValidationErrors[field.name]}
                    readOnly={field.readOnly}
                    disabled={field.readOnly}
                  >
                    <option value="">Select {field.label.toLowerCase()}</option>
                    {field.options.map(option => (
                      <option key={option} value={option}>{option}</option>
                    ))}
                  </Form.Select>
                ) : field.type === 'textarea' ? (
                  <Form.Control
                    as="textarea"
                    rows={3}
                    name={field.name}
                    value={formData[field.name] || ''}
                    onChange={handleChange}
                    required={field.required}
                    isInvalid={!!localValidationErrors[field.name]}
                    readOnly={field.readOnly}
                    disabled={field.readOnly}
                  />
                ) : field.type === 'checkbox' ? (
                    <Form.Check
                        type="checkbox"
                        name={field.name}
                        checked={formData[field.name] || false}
                        onChange={handleChange}
                        label={field.label}
                        readOnly={field.readOnly}
                        disabled={field.readOnly}
                    />
                ) : (
                  <Form.Control
                    type={field.type || 'text'}
                    name={field.name}
                    value={
                        (formData[field.name] === null || formData[field.name] === undefined || formData[field.name] === 'N/A')
                        ? ''
                        : formData[field.name]
                    }
                    onChange={handleChange}
                    required={field.required}
                    readOnly={field.readOnly || (idPrefix === 'examination' && (field.name === 'doktorID' || field.name === 'datumPregleda' || field.name === 'vrijemePregleda'))}
                    disabled={field.readOnly || (idPrefix === 'examination' && (field.name === 'doktorID' || field.name === 'datumPregleda' || field.name === 'vrijemePregleda'))}
                    onBlur={field.name === 'terminID' && idPrefix === 'examination' ? handleTerminIDBlur : undefined}
                    isInvalid={!!localValidationErrors[field.name]}
                  />
                )}
                {localValidationErrors[field.name] && (
                  <Form.Control.Feedback type="invalid">
                    {localValidationErrors[field.name]}
                  </Form.Control.Feedback>
                )}
              </Form.Group>
            ))}
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={onClose}>
              Zatvori
            </Button>
            <Button variant="primary" type="submit">
              Sačuvaj izmjene
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>
    );
  };

  // Pagination component
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

  // Function for rendering tab content
  const renderContent = () => {
    switch (activeTab) {
      case 'overview':
        return (
          <div>
            <h4 className="mb-4">Pregled nadzorne ploče</h4>
            <div className="row g-4">
              <div className="col-md-3">
                <div className="card text-white bg-primary mb-3">
                  <div className="card-header">Ukupno doktora</div>
                  <div className="card-body">
                    <h5 className="card-title display-4">{doctors.length}</h5>
                  </div>
                </div>
              </div>
              <div className="col-md-3">
                <div className="card text-white bg-success mb-3">
                  <div className="card-header">Ukupno pacijenata</div>
                  <div className="card-body">
                    <h5 className="card-title display-4">{patients.length}</h5>
                  </div>
                </div>
              </div>
              <div className="col-md-3">
                <div className="card text-white bg-info mb-3">
                  <div className="card-header">Dostupni termini</div>
                  <div className="card-body">
                    <h5 className="card-title display-4">{terms.filter(t => t.statusTermina === 'DOSTUPAN').length}</h5>
                  </div>
                </div>
              </div>
              <div className="col-md-3">
                <div className="card text-white bg-warning mb-3">
                  <div className="card-header">Pregledi na čekanju</div>
                  <div className="card-body">
                    <h5 className="card-title display-4">{examinations.filter(exam => exam.status === 'zakazan').length}</h5>
                  </div>
                </div>
              </div>
            </div>
            <div className="mt-5">
              <h5>Brze akcije</h5>
              <div className="d-flex gap-2 flex-wrap">
                <button className="btn btn-outline-primary" onClick={() => openDoctorModal()}>Dodaj novog doktora</button>
                <button className="btn btn-outline-success" onClick={() => openPatientModal()}>Dodaj novog pacijenta</button>
                <button className="btn btn-outline-info" onClick={() => openTermModal()}>Dodaj novi termin</button>
              </div>
            </div>
          </div>
        );
      case 'doctors':
        return (
          <div>
            <h4 className="mb-4">Upravljanje doktorima</h4>
            <div className="d-flex justify-content-between mb-3">
              <button className="btn btn-success" onClick={() => openDoctorModal()}>Dodaj novog doktora</button>
              <input
                type="text"
                className="form-control w-25"
                placeholder="Pretraži doktore..."
                value={doctorSearchTerm}
                onChange={(e) => setDoctorSearchTerm(e.target.value)}
              />
            </div>
            <div className="table-responsive">
              <table className="table table-striped table-hover">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Ime</th>
                    <th>Specijalnost</th>
                    <th>Grad</th>
                    <th>Email</th>
                    <th>Telefon</th>
                    <th>Radno vrijeme</th>
                    <th>Iskustvo</th>
                    <th>Ocjena</th>
                    <th>Akcije</th>
                  </tr>
                </thead>
                <tbody>
                  {currentDoctors.length > 0 ? (
                    currentDoctors.map(doctor => (
                      <tr key={doctor.id}>
                        <td>{doctor.id}</td>
                        <td>{doctor.name}</td>
                        <td>{doctor.specialty}</td>
                        <td>{doctor.city}</td>
                        <td>{doctor.email}</td>
                        <td>{doctor.telefon}</td>
                        <td>{doctor.radnoVrijeme}</td>
                        <td>{doctor.iskustvo}</td>
                        <td>{doctor.ocjena}</td>
                        <td>
                          <button className="btn btn-info btn-sm me-2" onClick={() => openDoctorModal(doctor)}>Uredi</button>
                          <button className="btn btn-danger btn-sm" onClick={() => handleDeleteDoctor(doctor.id)}>Obriši</button>
                        </td>
                      </tr>
                    ))
                  ) : (
                    <tr>
                      <td colSpan="10" className="text-center">Nije pronađen nijedan doktor.</td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
            <Pagination
              itemsPerPage={itemsPerPage}
              totalItems={filteredDoctors.length}
              currentPage={doctorsCurrentPage}
              paginate={setDoctorsCurrentPage}
            />
          </div>
        );
      case 'patients':
        return (
          <div>
            <h4 className="mb-4">Upravljanje pacijentima</h4>
            <div className="d-flex justify-content-between mb-3">
              <button className="btn btn-success" onClick={() => openPatientModal()}>Dodaj novog pacijenta</button>
              <input
                type="text"
                className="form-control w-25"
                placeholder="Pretraži pacijente..."
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
                  {currentPatients.length > 0 ? (
                    currentPatients.map(patient => (
                      <tr key={patient.id}>
                        <td>{patient.id}</td>
                        <td>{patient.name}</td>
                        <td>{patient.email}</td>
                        <td>{patient.telefon}</td>
                        <td>
                          <button className="btn btn-info btn-sm me-2" onClick={() => openPatientModal(patient)}>Uredi</button>
                          <button className="btn btn-danger btn-sm" onClick={() => handleDeletePatient(patient.id)}>Obriši</button>
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
              totalItems={filteredPatients.length}
              currentPage={patientsCurrentPage}
              paginate={setPatientsCurrentPage}
            />
          </div>
        );
      case 'terms':
        return (
          <div>
            <h4 className="mb-4">Upravljanje terminima</h4>
            <div className="d-flex justify-content-between mb-3">
              <button className="btn btn-success" onClick={() => openTermModal()}>Dodaj novi termin</button>
              <input
                type="text"
                className="form-control w-25"
                placeholder="Pretraži termine..."
                value={termSearchTerm}
                onChange={(e) => setTermSearchTerm(e.target.value)}
              />
            </div>
            <div className="table-responsive">
              <table className="table table-striped table-hover">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>ID Doktora</th>
                    <th>Datum</th>
                    <th>Vrijeme</th>
                    <th>Status</th>
                    <th>Akcije</th>
                  </tr>
                </thead>
                <tbody>
                  {currentTerms.length > 0 ? (
                    currentTerms.map(term => (
                      <tr key={term.id}>
                        <td>{term.id}</td>
                        <td>{term.doktorID}</td>
                        <td>{term.datum}</td>
                        <td>{term.vrijeme ? term.vrijeme.substring(0, 5) : 'N/A'}</td>
                        <td>
                            <span className={`badge ${
                                term.statusTermina === 'DOSTUPAN' ? 'bg-success' :
                                term.statusTermina === 'ZAKAZAN' ? 'bg-warning text-dark' :
                                'bg-danger'
                            }`}>
                                {getTranslatedExaminationStatus(term.statusTermina)}
                            </span>
                        </td>
                        <td>
                          <button className="btn btn-info btn-sm me-2" onClick={() => openTermModal(term)}>Uredi</button>
                          <button className="btn btn-danger btn-sm" onClick={() => handleDeleteTerm(term.id)}>Obriši</button>
                        </td>
                      </tr>
                    ))
                  ) : (
                    <tr>
                      <td colSpan="6" className="text-center">Nije pronađen nijedan termin.</td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
            <Pagination
              itemsPerPage={itemsPerPage}
              totalItems={filteredTerms.length}
              currentPage={termsCurrentPage}
              paginate={setTermsCurrentPage}
            />
          </div>
        );
      case 'examinations':
        return (
          <div>
            <h4 className="mb-4">Upravljanje pregledima</h4>
            <div className="d-flex justify-content-between mb-3">
              <input
                type="text"
                className="form-control w-25"
                placeholder="Pretraži preglede..."
                value={examinationSearchTerm}
                onChange={(e) => setExaminationSearchTerm(e.target.value)}
              />
            </div>
            <div className="table-responsive">
              <table className="table table-striped table-hover">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Pacijent</th>
                    <th>Doktor</th>
                    <th>ID Termina</th>
                    <th>Datum</th>
                    <th>Vrijeme</th>
                    <th>Status</th>
                    <th>Komentar pacijenta</th>
                    <th>Ocjena doktora</th>
                    <th>Akcije</th>
                  </tr>
                </thead>
                <tbody>
                  {currentExaminations.length > 0 ? (
                    currentExaminations.map(examination => (
                      <tr key={examination.id}>
                        <td>{examination.id}</td>
                        <td>{examination.patientName}</td>
                        <td>{examination.doctorName}</td>
                        <td>{examination.terminID}</td>
                        <td>{examination.datumPregleda}</td>
                        <td>{examination.vrijemePregleda ? examination.vrijemePregleda.substring(0, 5) : 'N/A'}</td>
                        <td>
                            <span className={`badge ${
                                examination.status === 'zakazan' ? 'bg-warning text-dark' :
                                examination.status === 'obavljen' ? 'bg-success' :
                                examination.status === 'otkazan' ? 'bg-danger' :
                                'bg-secondary'
                            }`}>
                                {getTranslatedExaminationStatus(examination.status)}
                            </span>
                        </td>
                        <td>{examination.komentarPacijenta}</td>
                        <td>{examination.ocjenaDoktora}</td>
                        <td>
                          <button className="btn btn-info btn-sm me-2" onClick={() => openExaminationModal(examination)}>Uredi</button>
                          <button className="btn btn-danger btn-sm" onClick={() => handleDeleteExamination(examination.id)}>Obriši</button>
                        </td>
                      </tr>
                    ))
                  ) : (
                    <tr>
                      <td colSpan="10" className="text-center">Nije pronađen nijedan pregled.</td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
            <Pagination
              itemsPerPage={itemsPerPage}
              totalItems={filteredExaminations.length}
              currentPage={examinationsCurrentPage.current}
              paginate={(page) => { examinationsCurrentPage.current = page; }}
            />
          </div>
        );
      default:
        return <p>Odaberite karticu za prikaz sadržaja.</p>;
    }
  };

  // Display loading state before rendering panel
  if (authLoading || loadingInitial) {
    return (
      <div className="d-flex justify-content-center align-items-center" style={{ minHeight: '100vh' }}>
        <div className="spinner-border text-primary" role="status">
          <span className="visually-hidden">Učitavanje administratorskog panela...</span>
        </div>
        <p className="ms-3">Učitavanje administratorskog panela...</p>
      </div>
    );
  }

  // Display access denied message if token is not present after loading
  if (!token) {
    return (
      <div className="d-flex justify-content-center align-items-center" style={{ minHeight: '100vh' }}>
        <div className="text-center">
          <h2 className="text-danger">Pristup odbijen</h2>
          <p className="lead">Niste ovlašteni za pristup ovoj stranici. Molimo prijavite se kao administrator.</p>
        </div>
      </div>
    );
  }

  return (
    <div className="container-fluid">
      {showAlert && (
        <Alert variant={alertType} onClose={() => setShowAlert(false)} dismissible className="position-fixed top-0 start-50 translate-middle-x mt-3" style={{ zIndex: 1050 }}>
          {alertMessage}
        </Alert>
      )}

      <Modal show={showConfirmModal} onHide={() => setShowConfirmModal(false)} centered>
        <Modal.Header closeButton>
          <Modal.Title>{confirmTitle}</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {confirmMessage}
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowConfirmModal(false)}>
            Odustani
          </Button>
          <Button variant="danger" onClick={handleConfirmAction}>
            Potvrdi
          </Button>
        </Modal.Footer>
      </Modal>

      {showDoctorModal && (
        <DataModal
          show={showDoctorModal}
          onClose={() => {setShowDoctorModal(false); setEditingDoctor(null);}}
          data={editingDoctor}
          onSave={editingDoctor ? handleUpdateDoctor : handleAddDoctor}
          title={editingDoctor ? 'Uredi doktora' : 'Dodaj novog doktora'}
          idPrefix="doctor"
          fields={editingDoctor ? editDoctorFields : addDoctorFields}
          onSuccess={() => fetchDoctors()}
        />
      )}

      {showPatientModal && (
        <DataModal
          show={showPatientModal}
          onClose={() => {setShowPatientModal(false); setEditingPatient(null);}}
          data={editingPatient}
          onSave={editingPatient ? handleUpdatePatient : handleAddPatient}
          title={editingPatient ? 'Uredi pacijenta' : 'Dodaj novog pacijenta'}
          idPrefix="patient"
          fields={editingPatient ? editPatientFields : addPatientFields}
          onSuccess={() => fetchPatients()}
        />
      )}

      {showTermModal && (
        <DataModal
          show={showTermModal}
          onClose={() => {setShowTermModal(false); setEditingTerm(null);}}
          data={editingTerm}
          onSave={editingTerm ? handleUpdateTerm : handleAddTerm}
          title={editingTerm ? 'Uredi termin' : 'Dodaj novi termin'}
          idPrefix="term"
          fields={editingTerm ? editTermFields : addTermFields}
          onSuccess={() => fetchTerms()}
        />
      )}

      {showExaminationModal && (
        <DataModal
          show={showExaminationModal}
          onClose={() => {setShowExaminationModal(false); setEditingExamination(null);}}
          data={editingExamination}
          onSave={handleUpdateExamination}
          title={'Uredi pregled'}
          idPrefix="examination"
          fields={editExaminationFields}
          onSuccess={() => fetchExaminations()}
        />
      )}

      <div className="row">
        <nav className="col-md-3 col-lg-2 d-md-block bg-light sidebar collapse" style={{ minHeight: '100vh' }}>
          <div className="position-sticky pt-3">
            <ul className="nav flex-column">
              <li className="nav-item">
                <a className={`nav-link ${activeTab === 'overview' ? 'active' : ''}`} href="#" onClick={() => setActiveTab('overview')}>
                  Pregled
                </a>
              </li>
              <li className="nav-item">
                <a className={`nav-link ${activeTab === 'doctors' ? 'active' : ''}`} href="#" onClick={() => setActiveTab('doctors')}>
                  Doktori
                </a>
              </li>
              <li className="nav-item">
                <a className={`nav-link ${activeTab === 'patients' ? 'active' : ''}`} href="#" onClick={() => setActiveTab('patients')}>
                  Pacijenti
                </a>
              </li>
              <li className="nav-item">
                <a className={`nav-link ${activeTab === 'terms' ? 'active' : ''}`} href="#" onClick={() => setActiveTab('terms')}>
                  Termini
                </a>
              </li>
              <li className="nav-item">
                <a className={`nav-link ${activeTab === 'examinations' ? 'active' : ''}`} href="#" onClick={() => setActiveTab('examinations')}>
                  Pregledi
                </a>
              </li>
            </ul>
          </div>
        </nav>

        <main className="col-md-9 ms-sm-auto col-lg-10 px-md-4 py-4">
          {renderContent()}
        </main>
      </div>
    </div>
  );
}

export default AdminDashboard;
