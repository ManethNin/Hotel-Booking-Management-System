import './App.css';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Navbar from './components/common/NavBar';
import HomePage from './components/home/HomePage';
import ApiService from './service/ApiService';
import { ProtectedRoute, AdminRoute } from './service/guard';
import Footer from './components/common/Footer';

function App() {
  return (
    <BrowserRouter>
      <div className="App">
        <Navbar />
        <div className="content">
          <Routes>
            {/* Public Routes */}
            <Route exact path="/home" element={<HomePage />} />
          </Routes>
        </div>
      </div>
    </BrowserRouter>
  );
}

export default App;
