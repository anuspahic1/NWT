import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link, Navigate } from 'react-router-dom';

import HomePage from './pages/HomePage';
import ServicePage from './pages/ServicePage';
import LoginPage from './pages/LoginPage';
import SignUpPage from './pages/SignUpPage';
import AdminDashboard from './pages/AdminDashboard';
import DoctorDashboard from './pages/DoctorDashboard';
import PatientDashboard from './pages/PatientDashboard';
import DoctorProfilePage from './pages/DoctorProfilePage';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import ForgotPasswordRequestPage from './pages/ForgotPasswordRequestPage';
import ResetPasswordPage from './pages/ResetPasswordPage';
import ContactUsPage from './pages/ContactUsPage';
import HelpPage from './pages/HelpPage';
import BlogsPage from './pages/BlogsPage';

const ProtectedRoute = ({ children, requiredRoles }) => {
  // AŽURIRANO: Dohvati 'token' iz useAuth()
  const { user, loading, token } = useAuth(); // DODAN 'token'

  console.log("ProtectedRoute check: user:", user, "loading:", loading, "token exists:", !!token); // Dodan log za token
  console.log("ProtectedRoute check: Required Roles for this route:", requiredRoles);

  if (loading) {
    console.log("ProtectedRoute: Authentication still loading...");
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh', fontSize: '24px' }}>
        Učitavanje autentifikacije...
      </div>
    );
  }

  // AŽURIRANO: Provjeri da li 'user' objekt postoji I da li 'token' postoji
  // Ako user postoji i token postoji, onda je korisnik autentifikovan.
  if (!user || !token) { // KLJUČNA PROMJENA: promijenjeno iz !user.token u !token
    console.log("ProtectedRoute: User not authenticated (missing user object or token). Redirecting to /login.");
    return <Navigate to="/login" replace />;
  }

  // Provjeri uloge samo ako su specificirane
  if (requiredRoles && requiredRoles.length > 0) {
    const userRoles = user.roles || [];
    const hasRequiredRole = requiredRoles.some(role => userRoles.includes(role));

    if (!hasRequiredRole) {
      console.warn("ProtectedRoute: User does NOT have required role. Redirecting to /.");
      return <Navigate to="/" replace />;
    }
  }

  console.log("ProtectedRoute: User IS authorized. Rendering children.");
  return children;
};

// ... (ostatak koda AppHeader i App komponente ostaju isti)
function AppHeader() {
  const { user, logout, loading } = useAuth();

  if (loading) {
    console.log("AppHeader: Still loading authentication, not rendering header.");
    return null;
  }
  console.log("AppHeader: User object for header display:", user);


  return (
    <header className="d-flex justify-content-between align-items-center py-3 px-4 bg-white shadow-sm sticky-header">
      <h1 className="h3 mb-0 text-danger">WinxCare</h1>
      <nav>
        <ul className="nav">
          <li className="nav-item"><Link className="nav-link" to="/">Home</Link></li>
          <li className="nav-item"><Link className="nav-link" to="/service">Service</Link></li>
          <li className="nav-item"><Link className="nav-link" to="/contact-us">Contact Us</Link></li>
          <li className="nav-item"><Link className="nav-link" to="/help">Help</Link></li>
          <li className="nav-item"><Link className="nav-link" to="/blogs">Blogs</Link></li>
          {user && user.roles && user.roles.includes("ROLE_ADMIN") && (
            <li className="nav-item"><Link className="nav-link" to="/admin-dashboard">Admin Panel</Link></li>
          )}
          {user && user.roles && user.roles.includes("ROLE_DOKTOR") && (
            <li className="nav-item"><Link className="nav-link" to="/doctor-dashboard">Doctor Panel</Link></li>
          )}
          {user && user.roles && user.roles.includes("ROLE_PACIJENT") && (
            <li className="nav-item"><Link className="nav-link" to="/patient-dashboard">Patient Panel</Link></li>
          )}
        </ul>
      </nav>
      <div className="d-flex">
        {!user ? (
          <>
            <Link to="/sign-up" className="btn btn-outline-danger me-2">Sign Up</Link>
            <Link to="/login" className="btn btn-danger">Log In</Link>
          </>
        ) : (
          <>
            <span className="align-self-center me-3">Welcome, {user.fullName || user.email}!</span>
            <button onClick={logout} className="btn btn-danger">Log Out</button>
          </>
        )}
      </div>
    </header>
  );
}

function App() {
  return (
    <Router>
      <style>
        {`
        .nav-link { color: black !important; }
        .nav-link.active { color: #e91e63 !important; font-weight: bold; }
        .nav-link:hover { color: #e91e63 !important; }
        .sticky-header {
          position: sticky;
          top: 0;
          z-index: 1020;
          width: 100%;
          background-color: white;
        }
        `}
      </style>
      <AuthProvider>
        <div className="App">
          <AppHeader />

          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/service" element={<ServicePage />} />
            <Route path="/contact-us" element={<ContactUsPage />} />
            <Route path="/help" element={<HelpPage />} />
            <Route path="/blogs" element={<BlogsPage />} />

            <Route path="/login" element={<LoginPage />} />
            <Route path="/sign-up" element={<SignUpPage />} />
            <Route path="/forgot-password" element={<ForgotPasswordRequestPage />} />
            <Route path="/reset-password" element={<ResetPasswordPage />} />

            <Route path="/doctor/:id" element={<DoctorProfilePage />} />

            <Route
              path="/admin-dashboard"
              element={
                <ProtectedRoute requiredRoles={['ROLE_ADMIN']}>
                  <AdminDashboard />
                </ProtectedRoute>
              }
            />
            <Route
              path="/doctor-dashboard"
              element={
                <ProtectedRoute requiredRoles={['ROLE_DOKTOR']}>
                  <DoctorDashboard />
                </ProtectedRoute>
              }
            />
            <Route
              path="/patient-dashboard"
              element={
                <ProtectedRoute requiredRoles={['ROLE_PACIJENT']}>
                  <PatientDashboard />
                </ProtectedRoute>
              }
            />

          </Routes>
        </div>
      </AuthProvider>
    </Router>
  );
}

export default App;
