import { useState } from 'react'
import { Link } from 'react-router-dom'
import apiClient from '../api/client'
import ShipmentDetail from '../components/ShipmentDetail'

function SearchShipmentPage() {
  const [idInput, setIdInput] = useState('')
  const [searchState, setSearchState] = useState('idle') // idle | loading | found | not-found | error
  const [shipment, setShipment] = useState(null)

  const trimmedId = idInput.trim()
  const canSearch = /^\d+$/.test(trimmedId) && searchState !== 'loading'

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

      {searchState === 'found' && shipment && (
        <ShipmentDetail shipment={shipment} />
      )}
    </main>
  )
}

export default SearchShipmentPage
