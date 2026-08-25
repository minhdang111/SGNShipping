import { useState } from 'react'
import { Link } from 'react-router-dom'
import apiClient from '../api/client'
import Modal from '../components/Modal'
import ShipmentForm from '../components/ShipmentForm'
import { formatPhoneNumber, stripPhoneFormatting } from '../utils/phone'
import './NewShipmentPage.css'

const emptyRecipientForm = { name: '', address: '', phone: '' }

function NewShipmentPage() {
  const [phoneInput, setPhoneInput] = useState('')
  const [searchState, setSearchState] = useState('idle') // idle | loading | found | not-found | error
  const [matches, setMatches] = useState([])
  const [selectedCustomer, setSelectedCustomer] = useState(null)

  const [recipientsOpen, setRecipientsOpen] = useState(false)
  const [recipientsState, setRecipientsState] = useState('idle') // idle | loading | loaded | error
  const [recipients, setRecipients] = useState([])
  const [selectedRecipient, setSelectedRecipient] = useState(null)

  const [showRecipientForm, setShowRecipientForm] = useState(false)
  const [recipientForm, setRecipientForm] = useState(emptyRecipientForm)
  const [recipientFormErrors, setRecipientFormErrors] = useState({})
  const [recipientFormSubmitting, setRecipientFormSubmitting] = useState(false)

  const [createdShipment, setCreatedShipment] = useState(null)

  const digits = stripPhoneFormatting(phoneInput)
  const canSearch = digits.length === 10 && searchState !== 'loading'

  const recipientPhoneDigits = stripPhoneFormatting(recipientForm.phone)
  const canSubmitRecipient =
    recipientForm.name.trim() !== '' &&
    recipientForm.address.trim() !== '' &&
    recipientPhoneDigits.length === 10 &&
    !recipientFormSubmitting

  function handlePhoneChange(event) {
    setPhoneInput(formatPhoneNumber(event.target.value))
    setSearchState('idle')
    setSelectedCustomer(null)
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

  async function loadRecipients(customerId) {
    setRecipientsState('loading')
    try {
      const response = await apiClient.get(
        `/recipients/by-customer/${customerId}`,
      )
      setRecipients(response.data)
      setRecipientsState('loaded')
    } catch {
      setRecipientsState('error')
    }
  }

  function handleSelectCustomer(customer) {
    setSelectedCustomer(customer)
    setSelectedRecipient(null)
    setShowRecipientForm(false)
    setRecipientsOpen(true)
    loadRecipients(customer.id)
  }

  function handleSelectRecipient(recipient) {
    setSelectedRecipient(recipient)
    setRecipientsOpen(false)
  }

  function openRecipientForm() {
    setRecipientForm(emptyRecipientForm)
    setRecipientFormErrors({})
    setShowRecipientForm(true)
  }

  function handleRecipientFormChange(field, value) {
    setRecipientForm((previous) => ({
      ...previous,
      [field]: field === 'phone' ? formatPhoneNumber(value) : value,
    }))
  }

  async function handleRecipientFormSubmit(event) {
    event.preventDefault()
    setRecipientFormSubmitting(true)
    setRecipientFormErrors({})
    try {
      const response = await apiClient.post('/recipients', {
        name: recipientForm.name.trim(),
        address: recipientForm.address.trim(),
        phone: recipientPhoneDigits,
        customerId: selectedCustomer.id,
      })
      setShowRecipientForm(false)
      handleSelectRecipient(response.data)
    } catch (error) {
      if (error.response?.status === 400 && error.response.data) {
        setRecipientFormErrors(error.response.data)
      } else {
        setRecipientFormErrors({ _general: 'Could not add recipient. Try again.' })
      }
    } finally {
      setRecipientFormSubmitting(false)
    }
  }

  function handleStartOver() {
    setPhoneInput('')
    setSearchState('idle')
    setMatches([])
    setSelectedCustomer(null)
    setSelectedRecipient(null)
    setRecipients([])
    setRecipientsState('idle')
    setCreatedShipment(null)
  }

  return (
    <main className="page">
      <p className="breadcrumb">
        <Link to="/">Home</Link> / New Shipment
      </p>
      <h1>New Shipment</h1>

      <section className="lookup-card">
        <h2>Find sender by phone</h2>
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

        {searchState === 'not-found' && (
          <p className="status not-found">No customer found with that number.</p>
        )}
      </section>

      {createdShipment && (
        <section className="lookup-card shipment-success">
          <h2>Shipment created</h2>
          <p>
            Shipment <strong>#{createdShipment.id}</strong> for{' '}
            <strong>{createdShipment.recipient.name}</strong> &mdash; total{' '}
            <strong>${createdShipment.totalCost.toFixed(2)}</strong>
          </p>
          <button type="button" className="secondary" onClick={handleStartOver}>
            Create another shipment
          </button>
        </section>
      )}

      {selectedCustomer && selectedRecipient && !createdShipment && (
        <div className="selection-grid">
          <div className="info-card">
            <p className="info-label">Sender</p>
            <p className="info-name">{selectedCustomer.name}</p>
            <p className="info-detail">
              {formatPhoneNumber(selectedCustomer.phone)}
            </p>
            <p className="info-detail">{selectedCustomer.address}</p>
          </div>

          <div className="info-card">
            <p className="info-label">Recipient</p>
            <p className="info-name">{selectedRecipient.name}</p>
            <p className="info-detail">
              {formatPhoneNumber(selectedRecipient.phone)}
            </p>
            <p className="info-detail">{selectedRecipient.address}</p>
          </div>

          <ShipmentForm
            customerId={selectedCustomer.id}
            recipientId={selectedRecipient.id}
            onCreated={setCreatedShipment}
          />
        </div>
      )}

      {recipientsOpen && (
        <Modal
          title={`Recipients for ${selectedCustomer?.name}`}
          onClose={() => setRecipientsOpen(false)}
        >
          {recipientsState === 'loading' && <p className="status">Loading&hellip;</p>}
          {recipientsState === 'error' && (
            <p className="status error">
              Something went wrong loading recipients. Try again.
            </p>
          )}

          {recipientsState === 'loaded' && !showRecipientForm && (
            <>
              {recipients.length === 0 ? (
                <p className="status not-found">
                  No recipients yet for this customer.
                </p>
              ) : (
                <ul className="results">
                  {recipients.map((recipient) => (
                    <li key={recipient.id}>
                      <button
                        type="button"
                        className={
                          selectedRecipient?.id === recipient.id
                            ? 'result selected'
                            : 'result'
                        }
                        onClick={() => handleSelectRecipient(recipient)}
                      >
                        <span className="name">{recipient.name}</span>
                        <span className="detail">
                          {formatPhoneNumber(recipient.phone)}
                        </span>
                        <span className="detail">{recipient.address}</span>
                      </button>
                    </li>
                  ))}
                </ul>
              )}

              <button
                type="button"
                className="secondary add-recipient"
                onClick={openRecipientForm}
              >
                + Add new recipient
              </button>
            </>
          )}

          {showRecipientForm && (
            <form className="recipient-form" onSubmit={handleRecipientFormSubmit}>
              {recipientFormErrors._general && (
                <p className="status error">{recipientFormErrors._general}</p>
              )}

              <label>
                Name
                <input
                  type="text"
                  value={recipientForm.name}
                  onChange={(event) =>
                    handleRecipientFormChange('name', event.target.value)
                  }
                />
                {recipientFormErrors.name && (
                  <span className="field-error">{recipientFormErrors.name}</span>
                )}
              </label>

              <label>
                Address
                <input
                  type="text"
                  value={recipientForm.address}
                  onChange={(event) =>
                    handleRecipientFormChange('address', event.target.value)
                  }
                />
                {recipientFormErrors.address && (
                  <span className="field-error">
                    {recipientFormErrors.address}
                  </span>
                )}
              </label>

              <label>
                Phone
                <input
                  type="tel"
                  inputMode="numeric"
                  maxLength={14}
                  value={recipientForm.phone}
                  onChange={(event) =>
                    handleRecipientFormChange('phone', event.target.value)
                  }
                />
                {recipientFormErrors.phone && (
                  <span className="field-error">{recipientFormErrors.phone}</span>
                )}
              </label>

              <div className="form-actions">
                <button
                  type="button"
                  className="secondary"
                  onClick={() => setShowRecipientForm(false)}
                >
                  Cancel
                </button>
                <button type="submit" disabled={!canSubmitRecipient}>
                  {recipientFormSubmitting ? 'Adding…' : 'Add recipient'}
                </button>
              </div>
            </form>
          )}
        </Modal>
      )}
    </main>
  )
}

export default NewShipmentPage
