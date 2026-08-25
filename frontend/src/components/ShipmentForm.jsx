import { useState } from 'react'
import apiClient from '../api/client'
import './ShipmentForm.css'

const emptyBox = { label: '', weight: '' }
const emptyItem = { itemName: '', pricingType: 'PER_POUND', quantity: '', rate: '' }

function ShipmentForm({ customerId, recipientId, onCreated }) {
  const [description, setDescription] = useState('')
  const [zone, setZone] = useState('CITY')
  const [declaredValue, setDeclaredValue] = useState('')
  const [boxes, setBoxes] = useState([{ ...emptyBox }])
  const [packageItems, setPackageItems] = useState([])
  const [errors, setErrors] = useState({})
  const [submitting, setSubmitting] = useState(false)

  const boxesValid =
    boxes.length > 0 && boxes.every((box) => Number(box.weight) > 0)
  const itemsValid = packageItems.every(
    (item) =>
      item.itemName.trim() !== '' &&
      Number(item.quantity) > 0 &&
      Number(item.rate) > 0,
  )
  const canSubmit =
    description.trim() !== '' && boxesValid && itemsValid && !submitting

  function updateBox(index, field, value) {
    setBoxes((previous) =>
      previous.map((box, i) => (i === index ? { ...box, [field]: value } : box)),
    )
  }

  function addBox() {
    setBoxes((previous) => [...previous, { ...emptyBox }])
  }

  function removeBox(index) {
    setBoxes((previous) => previous.filter((_, i) => i !== index))
  }

  function updateItem(index, field, value) {
    setPackageItems((previous) =>
      previous.map((item, i) =>
        i === index ? { ...item, [field]: value } : item,
      ),
    )
  }

  function addItem() {
    setPackageItems((previous) => [...previous, { ...emptyItem }])
  }

  function removeItem(index) {
    setPackageItems((previous) => previous.filter((_, i) => i !== index))
  }

  async function handleSubmit(event) {
    event.preventDefault()
    setSubmitting(true)
    setErrors({})
    try {
      const response = await apiClient.post('/shipments', {
        customerId,
        recipientId,
        description: description.trim(),
        zone,
        declaredValue: declaredValue === '' ? null : Number(declaredValue),
        boxes: boxes.map((box) => ({
          label: box.label.trim() === '' ? null : box.label.trim(),
          weight: Number(box.weight),
        })),
        packageItems: packageItems.map((item) => ({
          itemName: item.itemName.trim(),
          pricingType: item.pricingType,
          quantity: Number(item.quantity),
          rate: Number(item.rate),
        })),
      })
      onCreated(response.data)
    } catch (error) {
      if (error.response?.status === 400 && error.response.data) {
        setErrors(error.response.data)
      } else {
        setErrors({ _general: 'Could not create shipment. Try again.' })
      }
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <form className="shipment-form" onSubmit={handleSubmit}>
      <h2>Shipment details</h2>

      {errors._general && <p className="status error">{errors._general}</p>}
      {Object.entries(errors)
        .filter(([field]) => field !== '_general')
        .map(([field, message]) => (
          <p className="field-error" key={field}>
            {field}: {message}
          </p>
        ))}

      <label>
        Description
        <textarea
          rows={2}
          value={description}
          onChange={(event) => setDescription(event.target.value)}
        />
      </label>

      <div className="form-row">
        <label>
          Delivery zone
          <select value={zone} onChange={(event) => setZone(event.target.value)}>
            <option value="CITY">City</option>
            <option value="OTHER">Other</option>
          </select>
        </label>

        <label>
          Declared value ($)
          <input
            type="number"
            min="0"
            step="0.01"
            value={declaredValue}
            onChange={(event) => setDeclaredValue(event.target.value)}
          />
        </label>
      </div>

      <div className="form-section">
        <p className="form-section-title">Boxes</p>
        {boxes.map((box, index) => (
          <div className="repeat-row" key={index}>
            <input
              type="text"
              placeholder="Label (optional)"
              value={box.label}
              onChange={(event) => updateBox(index, 'label', event.target.value)}
            />
            <input
              type="number"
              min="0"
              step="0.1"
              placeholder="Weight (lb)"
              value={box.weight}
              onChange={(event) => updateBox(index, 'weight', event.target.value)}
            />
            <button
              type="button"
              className="remove-row"
              onClick={() => removeBox(index)}
              disabled={boxes.length === 1}
              aria-label="Remove box"
            >
              &times;
            </button>
          </div>
        ))}
        <button type="button" className="secondary" onClick={addBox}>
          + Add box
        </button>
      </div>

      <div className="form-section">
        <p className="form-section-title">Package items (optional)</p>
        {packageItems.map((item, index) => (
          <div className="repeat-row item-row" key={index}>
            <input
              type="text"
              placeholder="Item name"
              value={item.itemName}
              onChange={(event) =>
                updateItem(index, 'itemName', event.target.value)
              }
            />
            <select
              value={item.pricingType}
              onChange={(event) =>
                updateItem(index, 'pricingType', event.target.value)
              }
            >
              <option value="PER_POUND">Per pound</option>
              <option value="PER_EACH">Per item</option>
            </select>
            <input
              type="number"
              min="0"
              step="0.01"
              placeholder="Qty"
              value={item.quantity}
              onChange={(event) =>
                updateItem(index, 'quantity', event.target.value)
              }
            />
            <input
              type="number"
              min="0"
              step="0.01"
              placeholder="Rate"
              value={item.rate}
              onChange={(event) => updateItem(index, 'rate', event.target.value)}
            />
            <button
              type="button"
              className="remove-row"
              onClick={() => removeItem(index)}
              aria-label="Remove item"
            >
              &times;
            </button>
          </div>
        ))}
        <button type="button" className="secondary" onClick={addItem}>
          + Add item
        </button>
      </div>

      <button type="submit" className="submit-shipment" disabled={!canSubmit}>
        {submitting ? 'Creating…' : 'Create Shipment'}
      </button>
    </form>
  )
}

export default ShipmentForm
