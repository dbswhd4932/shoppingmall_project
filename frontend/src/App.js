import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import Navbar from './components/layout/Navbar';
import Footer from './components/layout/Footer';
import Home from './pages/Home';
import Login from './pages/Login';
import Signup from './pages/Signup';
import GoodsList from './pages/Goods/GoodsList';
import GoodsCreate from './pages/Goods/GoodsCreate';
import CategoryCreate from './pages/Category/CategoryCreate';
import 'bootstrap/dist/css/bootstrap.min.css';

function App() {
    return (
        <Router>
            <AuthProvider>
                <div className="d-flex flex-column min-vh-100">
                    <Navbar />
                    <main className="flex-grow-1">
                        <Routes>
                            <Route path="/" element={<Home />} />
                            <Route path="/login" element={<Login />} />
                            <Route path="/signup" element={<Signup />} />
                            <Route path="/goods" element={<GoodsList />} />
                            <Route path="/goods/create" element={<GoodsCreate />} />
                            <Route path="/categories/create" element={<CategoryCreate />} />
                            {/* 추가 라우트는 여기에 */}
                        </Routes>
                    </main>
                    <Footer />
                </div>
            </AuthProvider>
        </Router>
    );
}

export default App;
