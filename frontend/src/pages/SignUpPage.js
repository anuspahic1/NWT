import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import 'bootstrap/dist/css/bootstrap.min.css';

function SignUpPage() {
  const [fullName, setFullName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [registrationMessage, setRegistrationMessage] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setRegistrationMessage('');

    if (password !== confirmPassword) {
      setRegistrationMessage('Passwords do not match.');
      return;
    }

    try {
      const response = await axios.post('http://localhost:8081/auth/register', {
        fullName: fullName,
        email: email,
        password: password
      });

      if (response.status === 200 || response.status === 201) {
        setRegistrationMessage('Registration successful! Please log in.');
        console.log('Registration successful:', response.data);
        setTimeout(() => {
          navigate('/login');
        }, 2000);
      } else {
        setRegistrationMessage('Registration processed successfully, but with an unexpected status. Please check logs.');
        console.warn('Registration successful with unexpected status:', response.status, response.data);
        setTimeout(() => {
          navigate('/login');
        }, 2000);
      }
    } catch (error) {
      let errorMessage = 'An error occurred during registration. Please try again.';

      if (error.response && error.response.data) {
        if (typeof error.response.data === 'string' && error.response.data.includes('Email already exists')) {
          errorMessage = 'Email already exists. Please use a different email.';
        } else if (error.response.data.message && error.response.data.message.includes('Email already exists')) {
          errorMessage = 'Email already exists. Please use a different email.';
        } else if (error.response.data.errors && Array.isArray(error.response.data.errors)) {
          const passwordError = error.response.data.errors.find(err => err.field === 'password');
          if (passwordError) {
              errorMessage = `Password: ${passwordError.defaultMessage || 'Invalid password format.'}`;
          } else {
              errorMessage = error.response.data.errors.map(err => err.defaultMessage || err.field).join('; ');
          }
        } else if (error.response.data.message) {
          errorMessage = error.response.data.message;
        } else if (error.response.data.detail) {
          errorMessage = error.response.data.detail;
        }
      } else if (error.message) {
        errorMessage = error.message;
      }
      setRegistrationMessage(errorMessage);
      console.error('Registration error:', error);
    }
  };

  return (
    <div className="container d-flex justify-content-center align-items-center" style={{ minHeight: '80vh' }}>
      <div className="card shadow-lg p-4" style={{ maxWidth: '450px', width: '100%' }}>
        <div className="card-body">
          <h2 className="card-title text-center mb-4 text-danger">Sign Up for WinxCare</h2>
          {registrationMessage && (
            <div className={`alert ${registrationMessage.includes('successful') ? 'alert-success' : 'alert-danger'} text-center`} role="alert">
              {registrationMessage}
            </div>
          )}
          <form onSubmit={handleSubmit}>
            <div className="mb-3">
              <label htmlFor="nameInput" className="form-label">Full Name</label>
              <input
                type="text"
                className="form-control"
                id="nameInput"
                placeholder="Your Full Name"
                value={fullName}
                onChange={(e) => setFullName(e.target.value)}
                required
              />
            </div>
            <div className="mb-3">
              <label htmlFor="emailInput" className="form-label">Email address</label>
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
            <div className="mb-3">
              <label htmlFor="confirmPasswordInput" className="form-label">Confirm Password</label>
              <input
                type="password"
                className="form-control"
                id="confirmPasswordInput"
                placeholder="Confirm Password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                required
              />
            </div>
            <div className="d-grid gap-2 mb-3">
              <button type="submit" className="btn btn-danger btn-lg">Sign Up</button>
            </div>
          </form>
          <div className="text-center mt-3">
            <p className="text-muted">Already have an account? <Link to="/login" className="text-danger text-decoration-none">Login</Link></p>
          </div>
        </div>
        </div>
    </div>
  );
}

export default SignUpPage;
