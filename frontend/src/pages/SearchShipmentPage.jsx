import { useState } from 'react'
import { Link } from 'react-router-dom'
import apiClient from '../api/client'
import ShipmentDetail from '../components/ShipmentDetail'
import './SearchShipmentPage.css'

function SearchShipmentPage() {
  const [idInput, setIdInput] = useState('')
  const [searchState, setSearchState] = useState('idle') // idle | loading | found | not-found | error
  const [shipment, setShipment] = useState(null)

  const [startDate, setStartDate] = useState('')
  const [endDate, setEndDate] = useState('')
  const [dateRangeState, setDateRangeState] = useState('idle') // idle | loading | loaded | error
  const [dateResults, setDateResults] = useState([])
  const [selectedResultId, setSelectedResultId] = useState(null)
  const [statusFilter, setStatusFilter] = useState('ALL')

  const filteredDateResults =
    statusFilter === 'ALL'
      ? dateResults
      : dateResults.filter((result) => result.status === statusFilter)

  const trimmedId = idInput.trim()
  const canSearch = /^\d+$/.test(trimmedId) && searchState !== 'loading'
  const canSearchDateRange =
    startDate !== '' &&
    endDate !== '' &&
    startDate <= endDate &&
    dateRangeState !== 'loading'

  function handleIdChange(event) {
    setIdInput(event.target.value)
    setSearchState('idle')
    setShipment(null)
  }

  async function handleSearch() {
    setSearchState('loading')
    try {
      const response = await apiClient.get(`/shipments/${trimmedId}`)
      setShipment(response.data)
      setSelectedResultId(null)
      setSearchState('found')
    } catch (error) {
      if (error.response?.status === 404) {
        setSearchState('not-found')
      } else {
        setSearchState('error')
      }
    }
  }

  function handleIdKeyDown(event) {
    if (event.key === 'Enter' && canSearch) {
      handleSearch()
    }
  }

  async function handleDateRangeSearch() {
    setDateRangeState('loading')
    setStatusFilter('ALL')
    try {
      const response = await apiClient.get('/shipments/by-date-range', {
        params: { start: startDate, end: endDate },
      })
      setDateResults(response.data)
      setDateRangeState('loaded')
    } catch {
      setDateRangeState('error')
    }
  }

  async function handleSelectResult(id) {
    setSelectedResultId(id)
    try {
      const response = await apiClient.get(`/shipments/${id}`)
      setShipment(response.data)
      setSearchState('found')
    } catch {
      setDateRangeState('error')
    }
  }

  return (
    <main className="page">
      <p className="breadcrumb">
        <Link to="/">Home</Link> / Search Shipment
      </p>
      <h1>Search Shipment</h1>

      <section className="lookup-card">
        <h2>Find shipment by ID</h2>
        <div className="lookup-row">
          <input
            type="text"
            inputMode="numeric"
            placeholder="Shipment ID"
            value={idInput}
            onChange={handleIdChange}
            onKeyDown={handleIdKeyDown}
          />
          <button type="button" onClick={handleSearch} disabled={!canSearch}>
            Search
          </button>
        </div>

        {searchState === 'loading' && <p className="status">Searching&hellip;</p>}
        {searchState === 'error' && (
          <p className="status error">
            Something went wrong looking up that shipment. Try again.
          </p>
        )}
        {searchState === 'not-found' && (
          <p className="status not-found">
            No shipment found with ID {trimmedId}.
          </p>
        )}
      </section>

      <section className="lookup-card">
        <h2>Find shipments by date range</h2>
        <div className="lookup-row">
          <input
            type="date"
            value={startDate}
            onChange={(event) => {
              setStartDate(event.target.value)
              setDateRangeState('idle')
            }}
          />
          <input
            type="date"
            value={endDate}
            onChange={(event) => {
              setEndDate(event.target.value)
              setDateRangeState('idle')
            }}
          />
          <button
            type="button"
            onClick={handleDateRangeSearch}
            disabled={!canSearchDateRange}
          >
            Search
          </button>
        </div>

        {dateRangeState === 'loading' && (
          <p className="status">Searching&hellip;</p>
        )}
        {dateRangeState === 'error' && (
          <p className="status error">
            Something went wrong loading shipments. Try again.
          </p>
        )}

        {dateRangeState === 'loaded' && dateResults.length > 0 && (
          <label className="status-filter">
            Status
            <select
              value={statusFilter}
              onChange={(event) => setStatusFilter(event.target.value)}
            >
              <option value="ALL">All</option>
              <option value="PENDING">Pending</option>
              <option value="DELIVERED">Delivered</option>
            </select>
          </label>
        )}

        {dateRangeState === 'loaded' && dateResults.length === 0 && (
          <p className="status not-found">
            No shipments found in that date range.
          </p>
        )}
        {dateRangeState === 'loaded' &&
          dateResults.length > 0 &&
          filteredDateResults.length === 0 && (
            <p className="status not-found">
              No {statusFilter.toLowerCase()} shipments in that date range.
            </p>
          )}
        {dateRangeState === 'loaded' && filteredDateResults.length > 0 && (
          <ul className="results">
            {filteredDateResults.map((result) => (
              <li key={result.id}>
                <button
                  type="button"
                  className={
                    selectedResultId === result.id
                      ? 'result selected'
                      : 'result'
                  }
                  onClick={() => handleSelectResult(result.id)}
                >
                  <span className="name">
                    LS{result.id} &mdash; {result.recipientName}
                  </span>
                  <span className="detail">
                    {new Date(result.createdDate).toLocaleDateString()} &middot;{' '}
                    {result.status}
                  </span>
                </button>
              </li>
            ))}
          </ul>
        )}
      </section>

      {searchState === 'found' && shipment && (
        <ShipmentDetail
          key={shipment.id}
          shipment={shipment}
          onUpdate={setShipment}
        />
      )}
    </main>
  )
}

export default SearchShipmentPage
