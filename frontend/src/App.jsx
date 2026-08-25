import { BrowserRouter, Routes, Route } from 'react-router-dom'
import HomePage from './pages/HomePage'
import NewShipmentPage from './pages/NewShipmentPage'
import SearchShipmentPage from './pages/SearchShipmentPage'
import SearchCustomerPage from './pages/SearchCustomerPage'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/new-shipment" element={<NewShipmentPage />} />
        <Route path="/search-shipment" element={<SearchShipmentPage />} />
        <Route path="/search-customer" element={<SearchCustomerPage />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
