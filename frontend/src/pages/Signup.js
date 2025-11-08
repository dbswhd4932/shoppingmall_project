import React, { useState } from 'react';
import { Container, Row, Col, Card, Form, Button, Alert } from 'react-bootstrap';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Signup = () => {
    const [formData, setFormData] = useState({
        loginId: '',
        password: '',
        passwordConfirm: '',
        name: '',
        phoneNumber: '',
        email: '',
        roleType: 'USER'
    });
    const [error, setError] = useState('');
    const { signup } = useAuth();
    const navigate = useNavigate();

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        if (formData.password !== formData.passwordConfirm) {
            setError('Passwords do not match.');
            return;
        }

        try {
            const { passwordConfirm, ...signupData } = formData;
            await signup(signupData);
            alert('Sign up successful!');
            navigate('/login');
        } catch (error) {
            console.error('Sign up failed:', error);
            setError(error.response?.data?.message || 'Sign up failed. Please try again.');
        }
    };

    return (
        <Container className="my-4">
            <Row className="justify-content-center">
                <Col md={6}>
                    <Card className="shadow">
                        <Card.Body>
                            <h2 className="text-center mb-4">Sign Up</h2>
                            {error && <Alert variant="danger">{error}</Alert>}
                            <Form onSubmit={handleSubmit}>
                                <Form.Group className="mb-3">
                                    <Form.Label>Username</Form.Label>
                                    <Form.Control
                                        type="text"
                                        name="loginId"
                                        value={formData.loginId}
                                        onChange={handleChange}
                                        required
                                    />
                                </Form.Group>

                                <Form.Group className="mb-3">
                                    <Form.Label>Password</Form.Label>
                                    <Form.Control
                                        type="password"
                                        name="password"
                                        value={formData.password}
                                        onChange={handleChange}
                                        required
                                    />
                                </Form.Group>

                                <Form.Group className="mb-3">
                                    <Form.Label>Confirm Password</Form.Label>
                                    <Form.Control
                                        type="password"
                                        name="passwordConfirm"
                                        value={formData.passwordConfirm}
                                        onChange={handleChange}
                                        required
                                    />
                                </Form.Group>

                                <Form.Group className="mb-3">
                                    <Form.Label>Name</Form.Label>
                                    <Form.Control
                                        type="text"
                                        name="name"
                                        value={formData.name}
                                        onChange={handleChange}
                                        required
                                    />
                                </Form.Group>

                                <Form.Group className="mb-3">
                                    <Form.Label>Phone Number</Form.Label>
                                    <Form.Control
                                        type="tel"
                                        name="phoneNumber"
                                        value={formData.phoneNumber}
                                        onChange={handleChange}
                                        required
                                    />
                                </Form.Group>

                                <Form.Group className="mb-3">
                                    <Form.Label>Email</Form.Label>
                                    <Form.Control
                                        type="email"
                                        name="email"
                                        value={formData.email}
                                        onChange={handleChange}
                                        required
                                    />
                                </Form.Group>

                                <Form.Group className="mb-3">
                                    <Form.Label>Account Type</Form.Label>
                                    <Form.Select
                                        name="roleType"
                                        value={formData.roleType}
                                        onChange={handleChange}
                                        required
                                    >
                                        <option value="USER">User (일반 사용자)</option>
                                        <option value="SELLER">Seller (판매자)</option>
                                        <option value="ADMIN">Admin (관리자)</option>
                                    </Form.Select>
                                    <Form.Text className="text-muted">
                                        User: 일반 회원 | Seller: 상품 등록 가능 | Admin: 모든 권한
                                    </Form.Text>
                                </Form.Group>

                                <div className="d-grid">
                                    <Button variant="primary" type="submit">
                                        Sign Up
                                    </Button>
                                </div>
                            </Form>
                            <div className="mt-3 text-center">
                                <p>
                                    Already have an account?{' '}
                                    <Link to="/login">Login</Link>
                                </p>
                            </div>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};

export default Signup;
