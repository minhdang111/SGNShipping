import { useState } from 'react'
import { Link } from 'react-router-dom'
import apiClient from '../api/client'
import ShipmentDetail from '../components/ShipmentDetail'
import { formatPhoneNumber, stripPhoneFormatting } from '../utils/phone'
import './SearchCustomerPage.css'

function SearchCustomerPage() {
  const [phoneInput, setPhoneInput] = useState('')
  const [searchState, setSearchState] = useState('idle') // idle | loading | found | not-found | error
  const [matches, setMatches] = useState([])
  const [selectedCustomer, setSelectedCustomer] = useState(null)

  const [shipmentsState, setShipmentsState] = useState('idle') // idle | loading | loaded | error
  const [shipments, setShipments] = useState([])

  const [selectedShipmentId, setSelectedShipmentId] = useState(null)
  const [shipmentDetailState, setShipmentDetailState] = useState('idle') // idle | loading | loaded | error
  const [shipmentDetail, setShipmentDetail] = useState(null)

  const digits = stripPhoneFormatting(phoneInput)
  const canSearch = digits.length === 10 && searchState !== 'loading'

  function handlePhoneChange(event) {
    setPhoneInput(formatPhoneNumber(event.target.value))
    setSearchState('idle')
    setSelectedCustomer(null)
    setShipments([])
    setShipmentsState('idle')
    setSelectedShipmentId(null)
    setShipmentDetail(null)
  }

  async function handleSearch() {
    setSearchState('loading')
    try {
      const response = await apiClient.get('/customers/by-phone', {
        params: { phone: digits },
      })
      setMatches(response.data)
      setSearchState(response.data.length > 0 ? 'found' : 'not-found')
    } catch {
      setSearchState('error')
    }
  }

  function handlePhoneKeyDown(event) {
    if (event.key === 'Enter' && canSearch) {
      handleSearch()
    }
  }

  async function handleSelectCustomer(customer) {
    setSelectedCustomer(customer)
    setSelectedShipmentId(null)
    setShipmentDetail(null)
    setShipmentsState('loading')
    try {
      const response = await apiClient.get('/shipments/by-customer-phone', {
        params: { phone: customer.phone },
      })
      setShipments(response.data)
      setShipmentsState('loaded')
    } catch {
      setShipmentsState('error')
    }
  }

  async function handleSelectShipment(shipmentId) {
    setSelectedShipmentId(shipmentId)
    setShipmentDetailState('loading')
    try {
      const response = await apiClient.get(`/shipments/${shipmentId}`)
      setShipmentDetail(response.data)
      setShipmentDetailState('loaded')
    } catch {
      setShipmentDetailState('error')
    }
  }

  return (
    <main className="page">
      <p className="breadcrumb">
        <Link to="/">Home</Link> / Search Customer
      </p>
      <h1>Search Customer</h1>

      <section className="lookup-card">
        <h2>Find customer by phone</h2>
        <div className="lookup-row">
          <input
            type="tel"
            inputMode="numeric"
            placeholder="Phone number"
            value={phoneInput}
            onChange={handlePhoneChange}
            onKeyDown={handlePhoneKeyDown}
            maxLength={14}
          />
          <button type="button" onClick={handleSearch} disabled={!canSearch}>
            Search
          </button>
        </div>

        {searchState === 'loading' && <p className="status">Searching&hellip;</p>}
        {searchState === 'error' && (
          <p className="status error">
            Something went wrong looking up that number. Try again.
          </p>
        )}
        {searchState === 'not-found' && (
          <p className="status not-found">No customer found with that number.</p>
        )}

        {searchState === 'found' && (
          <ul className="results">
            {matches.map((customer) => (
              <li key={customer.id}>
                <button
                  type="button"
                  className={
                    selectedCustomer?.id === customer.id
                      ? 'result selected'
                      : 'result'
                  }
                  onClick={() => handleSelectCustomer(customer)}
                >
                  <span className="name">{customer.name}</span>
                  <span className="detail">
                    {formatPhoneNumber(customer.phone)}
                  </span>
                  <span className="detail">{customer.address}</span>
                </button>
              </li>
            ))}
          </ul>
        )}
      </section>

      {selectedCustomer && (
        <div className="info-card customer-card">
          <p className="info-label">Customer</p>
          <p className="info-name">{selectedCustomer.name}</p>
          <p className="info-detail">
            {formatPhoneNumber(selectedCustomer.phone)}
          </p>
          <p className="info-detail">{selectedCustomer.address}</p>
        </div>
      )}

      {selectedCustomer && (
        <section className="lookup-card">
          <h2>Past shipments</h2>

          {shipmentsState === 'loading' && (
            <p className="status">Loading&hellip;</p>
          )}
          {shipmentsState === 'error' && (
            <p className="status error">
              Something went wrong loading shipments. Try again.
            </p>
          )}
          {shipmentsState === 'loaded' && shipments.length === 0 && (
            <p className="status not-found">
              No shipments yet for this customer.
            </p>
          )}
          {shipmentsState === 'loaded' && shipments.length > 0 && (
            <ul className="results">
              {shipments.map((shipmentSummary) => (
                <li key={shipmentSummary.id}>
                  <button
                    type="button"
                    className={
                      selectedShipmentId === shipmentSummary.id
                        ? 'result selected'
                        : 'result'
                    }
                    onClick={() => handleSelectShipment(shipmentSummary.id)}
                  >
                    <span className="name">
                      LS{shipmentSummary.id} &mdash; {shipmentSummary.recipientName}
                    </span>
                    <span className="detail">
                      {new Date(shipmentSummary.createdDate).toLocaleDateString()}
                    </span>
                  </button>
                </li>
              ))}
            </ul>
          )}
        </section>
      )}

      {shipmentDetailState === 'loading' && (
        <p className="status">Loading shipment&hellip;</p>
      )}
      {shipmentDetailState === 'loaded' && shipmentDetail && (
        <ShipmentDetail shipment={shipmentDetail} />
      )}
    </main>
  )
}

export default SearchCustomerPage
