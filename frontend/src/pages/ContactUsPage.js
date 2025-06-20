import React, { useState } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import axios from 'axios';
import hospitalImage from '../images/hospital.jpg';

const GATEWAY_BASE_URL = 'http://localhost:8081';
const AUTH_API_PREFIX = `${GATEWAY_BASE_URL}/auth`;

function ContactUsPage() {
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [subject, setSubject] = useState('');
  const [message, setMessage] = useState('');
  const [submitMessage, setSubmitMessage] = useState('');
  const [submitMessageType, setSubmitMessageType] = useState('success');

  const contactInfo = {
    address: 'Zmaja od Bosne, 71000 Sarajevo, Bosnia and Herzegovina',
    phone: '+387 33 123 456',
    email: 'termini.pregledi@gmail.com', //email na koji se salju pitanja sa forme
    workingHours: 'Monday - Friday: 08:00 - 18:00'
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitMessage(''); //obrisi prethodnu poruku
    setSubmitMessageType('success');

    try {
      const payload = {
        name,
        email,
        subject,
        message,
      };

      const response = await axios.post(`${AUTH_API_PREFIX}/contact`, payload);

      setSubmitMessage(response.data);
      setSubmitMessageType('success');
      setName('');
      setEmail('');
      setSubject('');
      setMessage('');
    } catch (error) {
      console.error('Error sending contact form:', error.response?.data || error.message);
      setSubmitMessage(`Error sending message: ${error.response?.data?.message || error.response?.data || error.message}. Please try again.`);
      setSubmitMessageType('danger');
    }
  };

  return (
    <div className="container my-5">
      <div className="card shadow-lg p-4">
        <div className="card-body">
          <h2 className="card-title text-center mb-4 text-danger">Contact Us</h2>
          <p className="text-center text-muted mb-5">
            Have a question or need help? Feel free to contact us using the form below.
          </p>

          <div className="row mb-5 align-items-center">
            <div className="col-md-6">
              <img src={hospitalImage} alt="WinxCare Clinic" className="img-fluid rounded shadow-sm mb-3 mb-md-0" />
            </div>
            <div className="col-md-6">
              <h4 className="text-danger mb-3">Our Information</h4>
              <p className="text-muted mb-1"><strong>Address:</strong> {contactInfo.address}</p>
              <p className="text-muted mb-1"><strong>Phone:</strong> {contactInfo.phone}</p>
              <p className="text-muted mb-1"><strong>Email:</strong> {contactInfo.email}</p>
              <p className="text-muted mb-1"><strong>Working Hours:</strong> {contactInfo.workingHours}</p>
            </div>
          </div>

          <hr className="my-5" />

          <h3 className="text-center mb-4 text-dark">Send Us a Message</h3>
          {submitMessage && (
            <div className={`alert alert-${submitMessageType} text-center mb-4`} role="alert">
              {submitMessage}
            </div>
          )}
          <form onSubmit={handleSubmit}>
            <div className="mb-3">
              <label htmlFor="nameInput" className="form-label">Your Name</label>
              <input
                type="text"
                className="form-control"
                id="nameInput"
                placeholder="Full Name"
                value={name}
                onChange={(e) => setName(e.target.value)}
                required
              />
            </div>
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
              <label htmlFor="subjectInput" className="form-label">Subject</label>
              <input
                type="text"
                className="form-control"
                id="subjectInput"
                placeholder="Subject of your message"
                value={subject}
                onChange={(e) => setSubject(e.target.value)}
                required
              />
            </div>
            <div className="mb-3">
              <label htmlFor="messageTextarea" className="form-label">Your Message</label>
              <textarea
                className="form-control"
                id="messageTextarea"
                rows="5"
                placeholder="Type your message here..."
                value={message}
                onChange={(e) => setMessage(e.target.value)}
                required
              ></textarea>
            </div>
            <div className="d-grid gap-2">
              <button type="submit" className="btn btn-danger btn-lg">Send Message</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}

export default ContactUsPage;
