import { Link } from 'react-router-dom'
import './HomePage.css'

function HomePage() {
  return (
    <main className="home-page">
      <h1>SGN Shipping</h1>
      <nav className="navbar">
        <Link to="/new-shipment" className="nav-link">
          New Shipment
        </Link>
        <Link to="/search-shipment" className="nav-link">
          Search Shipment
        </Link>
        <Link to="/search-customer" className="nav-link">
          Search Customer
        </Link>
      </nav>
    </main>
  )
}

export default HomePage
