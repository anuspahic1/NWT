import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.min.css';
import backgroundImage from '../images/background.jpg';
import dentalImage from '../images/dental.jpg';
import dermaImage from '../images/derma.jpg';
import ophtaImage from '../images/ophta.jpg';

function ServicePage() {
  const [doctors, setDoctors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [searchTerm, setSearchTerm] = useState('');
  const [selectedSpecialty, setSelectedSpecialty] = useState('');
  const [selectedLocation, setSelectedLocation] = useState('');

  const API_URL = 'http://localhost:8081/api/korisnici-doktori/api/doktori';

  const popularServices = [
      {
        id: 1,
        title: 'Dental Treatments',
        description: 'At WinxCare, our expert dentists offer a range of services from routine check-ups to advanced cosmetic procedures, ensuring your brightest smile and optimal oral health.',
        image: dentalImage
      },
      {
        id: 2,
        title: 'Dermatology',
        description: 'Our dermatologists specialize in skin health, offering solutions for acne, eczema, anti-aging, and skin cancer screenings, helping you achieve clear and healthy skin.',
        image: dermaImage
      },
      {
        id: 3,
        title: 'Ophthalmology',
        description: 'Comprehensive eye care services, including vision tests, contact lens fittings, and treatment for eye conditions. Our ophthalmologists are dedicated to preserving your sight.',
        image: ophtaImage
      }
    ];

    useEffect(() => {
      const fetchDoctors = async () => {
        try {
          const response = await fetch(API_URL);
          if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
          }
          const data = await response.json();

          const transformedDoctors = data.map(doktor => {
            const initials = doktor.ime.charAt(0) + doktor.prezime.charAt(0);
            const defaultImage = `https://placehold.co/100x100/CCCCCC/000000?text=${initials}`;

            return {
              id: doktor.doktorID,
              name: `${doktor.ime} ${doktor.prezime}`,
              specialty: Array.isArray(doktor.specijalizacije) && doktor.specijalizacije.length > 0
                         ? doktor.specijalizacije.join(', ')
                         : 'N/A',
              location: doktor.grad,
              rating: doktor.ocjena,
              image: doktor.profileImageBase64 ? doktor.profileImageBase64 : defaultImage
            };
          });

          setDoctors(transformedDoctors);
        } catch (err) {
          setError(err);
        } finally {
          setLoading(false);
        }
      };

      fetchDoctors();
    }, []);

    const uniqueSpecialties = [...new Set(doctors.map(doctor => doctor.specialty))].filter(s => s !== 'N/A');
    const uniqueLocations = [...new Set(doctors.map(doctor => doctor.location))];

    const filteredDoctors = doctors.filter(doctor => {
      const matchesSearch = doctor.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
                            doctor.specialty.toLowerCase().includes(searchTerm.toLowerCase());
      const matchesSpecialty = selectedSpecialty === '' || doctor.specialty === selectedSpecialty;
      const matchesLocation = selectedLocation === '' || doctor.location === selectedLocation;
      return matchesSearch && matchesSpecialty && matchesLocation;
    });

    if (loading) {
      return (
        <div className="container-fluid text-center py-5">
          <p className="lead">Loading doctors...</p>
          <div className="spinner-border text-danger" role="status">
            <span className="visually-hidden">Loading...</span>
          </div>
        </div>
      );
    }

    if (error) {
      return (
        <div className="container-fluid text-center py-5">
          <p className="lead text-danger">Error loading doctors: {error.message}</p>
          <p>Please try again later.</p>
        </div>
      );
    }

    return (
      <div className="container-fluid p-0">
        <section
          className="py-5 text-white position-relative"
          style={{
            backgroundImage: `url(${backgroundImage})`,
            backgroundSize: 'cover',
            backgroundPosition: 'center',
            backgroundRepeat: 'no-repeat',
            minHeight: '400px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
          }}
        >
          <div
            className="position-absolute top-0 start-0 w-100 h-100"
            style={{ backgroundColor: 'rgba(0, 0, 0, 0.4)', zIndex: 1 }}
          ></div>
          <div className="container position-relative" style={{ zIndex: 2 }}>
            <h2 className="text-center mb-5 display-4">Find Your Doctor</h2>

            <div className="card p-4 mb-5 shadow-sm bg-white bg-opacity-75">
              <div className="row g-3 align-items-center">
                <div className="col-md-5">
                  <input
                    type="text"
                    className="form-control"
                    placeholder="Search by name or specialty..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                  />
                </div>
                <div className="col-md-3">
                  <select
                    className="form-select"
                    value={selectedSpecialty}
                    onChange={(e) => setSelectedSpecialty(e.target.value)}
                  >
                    <option value="">All Specialties</option>
                    {uniqueSpecialties.map(specialty => (
                      <option key={specialty} value={specialty}>{specialty}</option>
                    ))}
                  </select>
                </div>
                <div className="col-md-3">
                  <select
                    className="form-select"
                    value={selectedLocation}
                    onChange={(e) => setSelectedLocation(e.target.value)}
                  >
                    <option value="">All Locations</option>
                    {uniqueLocations.map(location => (
                      <option key={location} value={location}>{location}</option>
                    ))}
                  </select>
                </div>
                <div className="col-md-1 d-flex justify-content-end">
                  <button className="btn btn-danger w-100">Search</button>
                </div>
              </div>
            </div>
          </div>
        </section>

        <section className="py-5 bg-white">
          <div className="container">
            <h3 className="text-center mb-5 display-5 text-dark">Popular Services</h3>
            <div className="row row-cols-1 row-cols-md-3 g-4">
              {popularServices.map(service => (
                <div className="col" key={service.id}>
                  <div className="card h-100 shadow-sm border-0 rounded-lg">
                    <img src={service.image} className="card-img-top rounded-top" alt={service.title} />
                    <div className="card-body text-center">
                      <h5 className="card-title text-danger mb-2">{service.title}</h5>
                      <p className="card-text text-muted">{service.description}</p>
                      <a href="#" className="btn btn-link text-danger text-decoration-none">Learn More →</a>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </section>

        <main className="py-5 bg-light">
          <div className="container">
            <div className="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
              {filteredDoctors.length > 0 ? (
                filteredDoctors.map(doctor => (
                  <div className="col" key={doctor.id}>
                    <div className="card h-100 shadow-sm border-0">
                      <div className="card-body d-flex flex-column align-items-center text-center">
                        <img
                          src={doctor.image}
                          alt={doctor.name}
                          className="rounded-circle mb-3"
                          style={{ width: '100px', height: '100px', objectFit: 'cover' }}
                        />
                        <h5 className="card-title mb-1">{doctor.name}</h5>
                        <p className="card-subtitle mb-2 text-muted">{doctor.specialty}</p>
                        <div className="d-flex align-items-center mb-3">
                          <span className="text-warning me-1">★</span>
                          <span className="fw-bold">{doctor.rating}</span>
                          <span className="text-muted ms-2">({doctor.location})</span>
                        </div>
                        <Link to={`/doctor/${doctor.id}`} className="btn btn-outline-danger mt-auto">View Profile</Link>
                      </div>
                    </div>
                  </div>
                ))
              ) : (
                <div className="col-12 text-center py-5">
                  <p className="lead">No doctors found matching your criteria.</p>
                </div>
              )}
            </div>
          </div>
        </main>
      </div>
    );
  }

export default ServicePage;
