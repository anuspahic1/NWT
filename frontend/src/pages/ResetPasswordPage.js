import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useLocation, useNavigate, Link } from 'react-router-dom';

function ResetPasswordPage() {
  const location = useLocation();
  const navigate = useNavigate();
  const [token, setToken] = useState(null);
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [resetSuccess, setResetSuccess] = useState(false);

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const receivedToken = params.get('token');
    if (receivedToken) {
      setToken(receivedToken);
    } else {
      setError("Password reset token not found in the URL.");
    }
  }, [location.search]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage('');
    setError('');

    if (!token) {
      setError("Password reset token is missing.");
      return;
    }

    if (newPassword !== confirmPassword) {
      setError("Passwords do not match.");
      return;
    }

    try {
       const response = await axios.post(
              'http://localhost:8081/auth/reset-password',
              { token: token, newPassword: newPassword },
              {
                  headers: {
                      'Content-Type': 'application/json'
                  }
              }
            );
      setMessage(response.data);
      setError('');
      setResetSuccess(true);

    } catch (err) {
      if (err.response) {
        setError(err.response.data.message || err.response.data.error || "An error occurred while resetting your password. Please try again.");
      } else {
        setError("An error occurred while resetting your password. Please try again.");
      }
    }
  };

  return (
    <div className="container mt-5">
      <div className="row justify-content-center">
        <div className="col-md-6">
          <div className="card">
            <div className="card-header text-center bg-danger text-white">
              <h2>Password Reset</h2>
            </div>
            <div className="card-body">
              {message && <div className="alert alert-success">{message}</div>}
              {error && <div className="alert alert-danger">{error}</div>}

              {resetSuccess ? (
                  <div className="text-center">
                      <p>Your password has been successfully reset!</p>
                      <Link to="/login" className="btn btn-danger mt-3">Back to Login</Link>
                  </div>
              ) : token ? (
                <form onSubmit={handleSubmit}>
                  <div className="mb-3">
                    <label htmlFor="newPasswordInput" className="form-label">New Password</label>
                    <input
                      type="password"
                      className="form-control"
                      id="newPasswordInput"
                      value={newPassword}
                      onChange={(e) => setNewPassword(e.target.value)}
                      required
                    />
                  </div>
                  <div className="mb-3">
                    <label htmlFor="confirmPasswordInput" className="form-label">Confirm New Password</label>
                    <input
                      type="password"
                      className="form-control"
                      id="confirmPasswordInput"
                      value={confirmPassword}
                      onChange={(e) => setConfirmPassword(e.target.value)}
                      required
                    />
                  </div>
                  <div className="d-grid gap-2">
                    <button type="submit" className="btn btn-danger">Reset Password</button>
                  </div>
                </form>
              ) : (
                <p className="text-center">Please follow the link from your email to reset your password.</p>
              )}
              {!resetSuccess && <Link to="/login" className="text-center text-danger mt-3 d-block">Back to Login</Link>}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default ResetPasswordPage;
