import { useState } from 'react'
import apiClient from '../api/client'
import { formatPhoneNumber } from '../utils/phone'
import './ShipmentDetail.css'

function ShipmentDetail({ shipment, onUpdate = () => {} }) {
  const [trackingInput, setTrackingInput] = useState(
    shipment.trackingNumber || '',
  )
  const [trackingSubmitting, setTrackingSubmitting] = useState(false)
  const [trackingError, setTrackingError] = useState('')

  const [deliverSubmitting, setDeliverSubmitting] = useState(false)
  const [deliverError, setDeliverError] = useState('')

  const canSaveTracking =
    trackingInput.trim() !== '' &&
    trackingInput.trim() !== (shipment.trackingNumber || '') &&
    !trackingSubmitting

  async function handleSaveTracking() {
    setTrackingSubmitting(true)
    setTrackingError('')
    try {
      const response = await apiClient.patch(
        `/shipments/${shipment.id}/tracking-number`,
        { trackingNumber: trackingInput.trim() },
      )
      onUpdate(response.data)
    } catch {
      setTrackingError('Could not save tracking number. Try again.')
    } finally {
      setTrackingSubmitting(false)
    }
  }

  async function handleMarkDelivered() {
    setDeliverSubmitting(true)
    setDeliverError('')
    try {
      const response = await apiClient.patch(
        `/shipments/${shipment.id}/mark-delivered`,
      )
      onUpdate(response.data)
    } catch {
      setDeliverError('Could not mark as delivered. Try again.')
    } finally {
      setDeliverSubmitting(false)
    }
  }

  return (
    <>
      <div className="selection-grid">
        <div className="info-card">
          <p className="info-label">Sender</p>
          <p className="info-name">{shipment.customer.name}</p>
          <p className="info-detail">
            {formatPhoneNumber(shipment.customer.phone)}
          </p>
          <p className="info-detail">{shipment.customer.address}</p>
        </div>

        <div className="info-card">
          <p className="info-label">Recipient</p>
          <p className="info-name">{shipment.recipient.name}</p>
          <p className="info-detail">
            {formatPhoneNumber(shipment.recipient.phone)}
          </p>
          <p className="info-detail">{shipment.recipient.address}</p>
        </div>
      </div>

      <div className="shipment-detail">
        <div className="detail-header">
          <h2>Shipment LS{shipment.id}</h2>
          <div className="detail-header-actions">
            <span className={`status-pill ${shipment.status.toLowerCase()}`}>
              {shipment.status}
            </span>
            {shipment.status === 'PENDING' && (
              <button
                type="button"
                className="secondary"
                onClick={handleMarkDelivered}
                disabled={deliverSubmitting}
              >
                {deliverSubmitting ? 'Updating…' : 'Mark delivered'}
              </button>
            )}
          </div>
        </div>
        {deliverError && <p className="status error">{deliverError}</p>}

        <p className="info-detail">{shipment.description}</p>

        <div className="detail-meta">
          <div>
            <p className="info-label">Zone</p>
            <p className="info-detail">
              {shipment.zone === 'CITY' ? 'City' : 'Other'}
            </p>
          </div>
          <div className="tracking-field">
            <p className="info-label">Tracking #</p>
            <div className="tracking-row">
              <input
                type="text"
                value={trackingInput}
                onChange={(event) => setTrackingInput(event.target.value)}
                placeholder="Not set"
              />
              <button
                type="button"
                className="secondary"
                onClick={handleSaveTracking}
                disabled={!canSaveTracking}
              >
                {trackingSubmitting ? 'Saving…' : 'Save'}
              </button>
            </div>
            {trackingError && (
              <p className="field-error">{trackingError}</p>
            )}
          </div>
          <div>
            <p className="info-label">Declared value</p>
            <p className="info-detail">
              {shipment.declaredValue != null
                ? `$${shipment.declaredValue.toFixed(2)}`
                : '—'}
            </p>
          </div>
          <div>
            <p className="info-label">Total cost</p>
            <p className="info-detail">${shipment.totalCost.toFixed(2)}</p>
          </div>
          <div>
            <p className="info-label">Created</p>
            <p className="info-detail">
              {new Date(shipment.createdDate).toLocaleString()}
            </p>
          </div>
        </div>

        <div className="form-section">
          <p className="form-section-title">Boxes</p>
          <ul className="detail-list">
            {shipment.boxes.map((box) => (
              <li key={box.id}>
                {box.label ? `${box.label} — ` : ''}
                {box.weight} lb
              </li>
            ))}
          </ul>
        </div>

        {shipment.packageItems.length > 0 && (
          <div className="form-section">
            <p className="form-section-title">Package items</p>
            <ul className="detail-list">
              {shipment.packageItems.map((item) => (
                <li key={item.id}>
                  {item.itemName} &mdash; {item.quantity}{' '}
                  {item.pricingType === 'PER_POUND' ? 'lb' : 'ea'} @ $
                  {item.rate} = ${item.itemFee.toFixed(2)}
                </li>
              ))}
            </ul>
          </div>
        )}
      </div>
    </>
  )
}

export default ShipmentDetail
