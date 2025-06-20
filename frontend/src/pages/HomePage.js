import React, { useState } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import homeVilaImage from '../images/homevila.jpg';

function HomePage() {
    return (
    <div className="container">
      <section className="hero py-5">
        <div className="row align-items-center">
          <div className="col-md-6">
            <h2 className="display-4">Providing Excellent <strong>Healthcare</strong> For <br className="d-none d-lg-block" /> A <span className="text-success">Healthier</span> And <span className="text-danger">Brighter</span> Future.</h2>
            <p className="lead">At WinxCare, we believe in a future where quality healthcare is accessible and intuitive. Our platform seamlessly connects you with top medical professionals, simplifies appointment booking, and provides a secure way to manage your health records. Experience compassionate care and advanced technology working together for your well-being. Your journey to better health starts here.</p>
          </div>
          <div className="col-md-6 position-relative">
            <div className="bg-danger rounded-circle position-absolute" style={{ width: '150px', height: '150px', opacity: 0.6, top: '-20px', right: '80px', zIndex: 1}}></div>
            <div className="bg-success rounded-circle position-absolute" style={{ width: '120px', height: '120px', opacity: 0.6, bottom: '30px', left: '90px', zIndex: 1 }}></div>
            <div className="d-flex justify-content-end" style={{ position: 'relative', zIndex: 2, left:'-70px'}}>
              <img src={homeVilaImage} alt="Healthcare Illustration" className="rounded" style={{ maxWidth: '500px', height: 'auto'}} />
            </div>
          </div>
        </div>
      </section>
    </div>
  );
}

export default HomePage;
