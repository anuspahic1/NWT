import React from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import { Link } from 'react-router-dom';

function HelpPage() {
  const faqs = [
    {
      id: 1,
      question: 'How can I book an appointment with a doctor?',
      answer: 'You can book an appointment by navigating to the "Service" page, finding your desired doctor, and clicking "View Profile". On the doctor\'s profile, you will find a section to schedule an appointment where you can select a date and an available time slot.'
    },
    {
      id: 2,
      question: 'Can I change or cancel a booked appointment?',
      answer: 'Currently, changing or canceling appointments directly through the website is not available. Please contact us via phone or email listed on the "Contact Us" page for assistance.'
    },
    {
      id: 3,
      question: 'How can I find a doctor with a specific specialty?',
      answer: 'On the "Service" page, use the search filters. You can search by specialty, doctor\'s name, or city to narrow down your results.'
    },
    {
      id: 4,
      question: 'What should I do if I cannot find my doctor on the list?',
      answer: 'If you cannot find your doctor, please ensure you have entered the name or specialty correctly. If you still have trouble, please contact us directly for assistance.'
    },
    {
      id: 5,
      question: 'Is my personal data secure?',
      answer: 'Your privacy is our priority. All personal data is stored securely and used solely for the purpose of providing medical services. You can find more information in our privacy policy.'
    }
  ];

  return (
    <div className="container my-5">
      <div className="card shadow-lg p-4">
        <div className="card-body">
          <h2 className="card-title text-center mb-4 text-danger">Help Center</h2>
          <p className="text-center text-muted mb-5">
            Here you can find answers to frequently asked questions. If you don't find what you're looking for, feel free to contact us.
          </p>

          <hr className="my-5" />

          <h3 className="text-center mb-4 text-dark">Frequently Asked Questions</h3>
          <div className="accordion" id="faqAccordion">
            {faqs.map(faq => (
              <div className="accordion-item" key={faq.id}>
                <h2 className="accordion-header" id={`heading${faq.id}`}>
                  <button
                    className="accordion-button collapsed"
                    type="button"
                    data-bs-toggle="collapse"
                    data-bs-target={`#collapse${faq.id}`}
                    aria-expanded="false"
                    aria-controls={`collapse${faq.id}`}
                  >
                    {faq.question}
                  </button>
                </h2>
                <div
                  id={`collapse${faq.id}`}
                  className="accordion-collapse collapse"
                  aria-labelledby={`heading${faq.id}`}
                  data-bs-parent="#faqAccordion"
                >
                  <div className="accordion-body text-muted">
                    {faq.answer}
                  </div>
                </div>
              </div>
            ))}
          </div>

          <hr className="my-5" />

          <h3 className="text-center mb-4 text-dark">Still Need Help?</h3>
          <div className="row justify-content-center text-center">
            <div className="col-md-6 mb-3">
              <div className="card h-100 shadow-sm border-0">
                <div className="card-body">
                  <h5 className="card-title text-danger mb-3">Contact Us Directly</h5>
                  <p className="card-text text-muted">
                    If you have a specific question or need personalized assistance, send us a message.
                  </p>
                  <Link to="/contact-us" className="btn btn-outline-danger mt-3">Go to Contact Page</Link>
                </div>
              </div>
            </div>
            <div className="col-md-6 mb-3">
              <div className="card h-100 shadow-sm border-0">
                <div className="card-body">
                  <h5 className="card-title text-danger mb-3">Call Us</h5>
                  <p className="card-text text-muted">
                    For urgent cases or quick answers, call us during working hours.
                  </p>
                  <p className="card-text text-muted">
                    <strong>Phone:</strong> +387 33 123 456
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default HelpPage;
