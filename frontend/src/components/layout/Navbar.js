import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Navbar as BSNavbar, Nav, Container } from 'react-bootstrap';
import { useAuth } from '../../context/AuthContext';

const Navbar = () => {
    const { user, logout, hasRole, isAuthenticated } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        if (window.confirm('Are you sure you want to logout?')) {
            logout();
            alert('You have been logged out.');
            navigate('/');
        }
    };

    return (
        <BSNavbar bg="dark" variant="dark" expand="lg">
            <Container>
                <BSNavbar.Brand as={Link} to="/">
                    <i className="fas fa-shopping-cart"></i> Shopping Mall
                </BSNavbar.Brand>
                <BSNavbar.Toggle aria-controls="basic-navbar-nav" />
                <BSNavbar.Collapse id="basic-navbar-nav">
                    <Nav className="me-auto">
                        <Nav.Link as={Link} to="/goods">Products</Nav.Link>
                        {isAuthenticated && hasRole('ROLE_SELLER') && (
                            <Nav.Link as={Link} to="/goods/register">
                                <i className="fas fa-plus-circle"></i> Add Product
                            </Nav.Link>
                        )}
                    </Nav>
                    <Nav>
                        {isAuthenticated ? (
                            <>
                                <Nav.Link as={Link} to="/cart">
                                    <i className="fas fa-shopping-cart"></i> Cart
                                </Nav.Link>
                                <Nav.Link as={Link} to="/orders">
                                    <i className="fas fa-box"></i> Orders
                                </Nav.Link>
                                <Nav.Link as={Link} to="/mypage">
                                    <i className="fas fa-user"></i> My Page
                                </Nav.Link>
                                <Nav.Link onClick={handleLogout}>Logout</Nav.Link>
                            </>
                        ) : (
                            <>
                                <Nav.Link as={Link} to="/login">Login</Nav.Link>
                                <Nav.Link as={Link} to="/signup">Sign Up</Nav.Link>
                            </>
                        )}
                    </Nav>
                </BSNavbar.Collapse>
            </Container>
        </BSNavbar>
    );
};

export default Navbar;
