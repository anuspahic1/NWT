import React, { useState } from 'react';
import axios from 'axios'; // I dalje potreban za globalne Axios konfiguracije, ali ne za direktan POST ovdje
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';


function LoginPage() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const { login } = useAuth(); // Dohvati login funkciju iz konteksta
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    try {
      // AŽURIRANO: Pozovi login funkciju iz AuthContext-a
      // Ona će se pobrinuti za slanje POST zahtjeva i spremanje tokena/korisnika
      const loginResponse = await login(email, password); // login funkcija sada vraća response.data

      console.log("LoginPage: Login successful. Response data:", loginResponse);

      setSuccess(loginResponse.message || 'Login successful!');
      console.log('LoginPage: Login successful. Redirecting...');

      const userRoles = loginResponse.roles; // Koristi podatke dobivene od login funkcije
      if (userRoles && userRoles.includes('ROLE_ADMIN')) {
        navigate('/admin-dashboard');
      } else if (userRoles && userRoles.includes('ROLE_DOKTOR')) {
        navigate('/doctor-dashboard');
      } else if (userRoles && userRoles.includes('ROLE_PACIJENT')) {
        navigate('/patient-dashboard');
      } else {
        navigate('/');
      }

    } catch (err) {
      console.error('LoginPage: Login error:', err.response?.data || err.message);
      // Prikazuj poruku greške koju je vratio AuthContext ili generičku poruku
      setError(err.response?.data?.message || 'An error occurred during login. Please check your credentials.');
    }
  };

  return (
    <div className="container d-flex justify-content-center align-items-center" style={{ minHeight: '80vh' }}>
      <div className="card shadow-lg p-4" style={{ maxWidth: '400px', width: '100%' }}>
        <div className="card-body">
          <h2 className="card-title text-center mb-4 text-danger">Login to WinxCare</h2>
          {error && (
            <div className="alert alert-danger text-center" role="alert">
              {error}
            </div>
          )}
          {success && (
            <div className="alert alert-success text-center" role="alert">
              {success}
            </div>
          )}
          <form onSubmit={handleSubmit}>
            <div className="mb-3">
              <label htmlFor="emailInput" className="form-label">Email Address</label>
              <input
                type="email"
                className="form-control"
                id="emailInput"
                placeholder="name@example.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </div>
            <div className="mb-3">
              <label htmlFor="passwordInput" className="form-label">Password</label>
              <input
                type="password"
                className="form-control"
                id="passwordInput"
                placeholder="Password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>
            <div className="d-grid gap-2 mb-3">
              <button type="submit" className="btn btn-danger btn-lg">Login</button>
            </div>
          </form>
          <div className="text-center mt-3">
            <Link to="/forgot-password" className="text-muted text-decoration-none">Forgot your password?</Link>
          </div>
          <div className="text-center mt-2">
            <p className="text-muted">Do not have an account? <Link to="/sign-up" className="text-danger text-decoration-none">Register</Link></p>
          </div>
        </div>
      </div>
    </div>
  );
}

export default LoginPage;
