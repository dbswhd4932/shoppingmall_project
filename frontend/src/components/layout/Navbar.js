import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Navbar as BSNavbar, Nav, Container } from 'react-bootstrap';
import { useAuth } from '../../context/AuthContext';

const Navbar = () => {
    const { user, logout, hasRole, isAuthenticated } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        if (window.confirm('로그아웃 하시겠습니까?')) {
            logout();
            alert('로그아웃 되었습니다.');
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
                        <Nav.Link as={Link} to="/goods">
                            <i className="fas fa-list"></i> 전체 상품
                        </Nav.Link>
                        {isAuthenticated && hasRole('ROLE_SELLER') && (
                            <Nav.Link as={Link} to="/goods/create">
                                <i className="fas fa-plus-circle"></i> 상품 등록
                            </Nav.Link>
                        )}
                    </Nav>
                    <Nav>
                        {isAuthenticated ? (
                            <>
                                <Nav.Link as={Link} to="/cart">
                                    <i className="fas fa-shopping-cart"></i> 장바구니
                                </Nav.Link>
                                <Nav.Link as={Link} to="/orders">
                                    <i className="fas fa-box"></i> 주문내역
                                </Nav.Link>
                                <Nav.Link as={Link} to="/mypage">
                                    <i className="fas fa-user"></i> 마이페이지
                                </Nav.Link>
                                <Nav.Link onClick={handleLogout}>로그아웃</Nav.Link>
                            </>
                        ) : (
                            <>
                                <Nav.Link as={Link} to="/login">로그인</Nav.Link>
                                <Nav.Link as={Link} to="/signup">회원가입</Nav.Link>
                            </>
                        )}
                    </Nav>
                </BSNavbar.Collapse>
            </Container>
        </BSNavbar>
    );
};

export default Navbar;
