import React from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import { Link } from 'react-router-dom';

function BlogsPage() {
  //Mock podaci za blog postove
  const blogPosts = [
    {
      id: 1,
      title: 'The Importance of Regular Check-ups',
      author: 'Dr. Emily White',
      date: 'May 15, 2025',
      summary: 'Regular medical check-ups are crucial for maintaining good health and detecting potential issues early. Learn why they are so important.',
      image: 'https://placehold.co/400x250/F06292/FFFFFF?text=Checkups'
    },
    {
      id: 2,
      title: 'Understanding Childhood Vaccinations',
      author: 'Dr. John Doe',
      date: 'April 28, 2025',
      summary: 'Vaccinations play a vital role in protecting children from various diseases. This article explains the basics of childhood immunization.',
      image: 'https://placehold.co/400x250/8BC34A/FFFFFF?text=Vaccines'
    },
    {
      id: 3,
      title: 'Tips for a Healthy Heart',
      author: 'Dr. Jane Smith',
      date: 'April 10, 2025',
      summary: 'Maintaining cardiovascular health is key to a long and active life. Discover practical tips for keeping your heart strong.',
      image: 'https://placehold.co/400x250/00BCD4/FFFFFF?text=Heart'
    },
    {
      id: 4,
      title: 'Managing Stress in Daily Life',
      author: 'Dr. Sarah Green',
      date: 'March 22, 2025',
      summary: 'Stress is a common part of modern life, but effective management techniques can significantly improve your well-being. Read more here.',
      image: 'https://placehold.co/400x250/FFC107/FFFFFF?text=Stress'
    }
  ];

  return (
    <div className="container my-5">
      <div className="card shadow-lg p-4">
        <div className="card-body">
          <h2 className="card-title text-center mb-4 text-danger">Our Blog</h2>
          <p className="text-center text-muted mb-5">
            Stay informed with our latest articles on health, wellness, and medical advancements.
          </p>

          <hr className="my-5" />

          <div className="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
            {blogPosts.map(post => (
              <div className="col" key={post.id}>
                <div className="card h-100 shadow-sm border-0 rounded-lg">
                  <img src={post.image} className="card-img-top rounded-top" alt={post.title} />
                  <div className="card-body d-flex flex-column">
                    <h5 className="card-title text-dark mb-2">{post.title}</h5>
                    <p className="card-subtitle mb-2 text-muted small">
                      By {post.author} on {post.date}
                    </p>
                    <p className="card-text text-muted flex-grow-1">{post.summary}</p>
                    <Link to={`/blog/${post.id}`} className="btn btn-link text-danger text-decoration-none mt-auto">Read More →</Link>
                  </div>
                </div>
              </div>
            ))}
          </div>

          <div className="d-flex justify-content-center mt-5">
            <nav aria-label="Page navigation example">
              <ul className="pagination">
                <li className="page-item disabled"><a className="page-link" href="#">Previous</a></li>
                <li className="page-item active"><a className="page-link" href="#">1</a></li>
                <li className="page-item"><a className="page-link" href="#">2</a></li>
                <li className="page-item"><a className="page-link" href="#">3</a></li>
                <li className="page-item"><a className="page-link" href="#">Next</a></li>
              </ul>
            </nav>
          </div>
        </div>
      </div>
    </div>
  );
}

export default BlogsPage;
