import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import apiClient from '../api/client'
import './HomePage.css'

function HomePage() {
  const [summary, setSummary] = useState(null)
  const [summaryState, setSummaryState] = useState('loading') // loading | loaded | error

  useEffect(() => {
    apiClient
      .get('/shipments/summary/today')
      .then((response) => {
        setSummary(response.data)
        setSummaryState('loaded')
      })
      .catch(() => setSummaryState('error'))
  }, [])

  return (
    <main className="home-page">
      <h1>SGN Shipping</h1>

      {summaryState === 'loaded' && summary && (
        <div className="today-summary">
          <div className="summary-stat">
            <p className="summary-value">{summary.shipmentCount}</p>
            <p className="summary-label">Shipments today</p>
          </div>
          <div className="summary-stat">
            <p className="summary-value">
              {summary.totalWeight.toFixed(1)} lb
            </p>
            <p className="summary-label">Total weight today</p>
          </div>
        </div>
      )}
      {summaryState === 'error' && (
        <p className="status error">Could not load today&rsquo;s summary.</p>
      )}

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
