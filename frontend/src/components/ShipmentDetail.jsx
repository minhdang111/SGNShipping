import { formatPhoneNumber } from '../utils/phone'
import './ShipmentDetail.css'

function ShipmentDetail({ shipment }) {
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
          <span className={`status-pill ${shipment.status.toLowerCase()}`}>
            {shipment.status}
          </span>
        </div>

        <p className="info-detail">{shipment.description}</p>

        <div className="detail-meta">
          <div>
            <p className="info-label">Zone</p>
            <p className="info-detail">
              {shipment.zone === 'CITY' ? 'City' : 'Other'}
            </p>
          </div>
          <div>
            <p className="info-label">Tracking #</p>
            <p className="info-detail">{shipment.trackingNumber || '—'}</p>
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
