import { BrowserRouter, Routes, Route } from 'react-router-dom'
import HomePage from './pages/HomePage'
import NewShipmentPage from './pages/NewShipmentPage'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/new-shipment" element={<NewShipmentPage />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
