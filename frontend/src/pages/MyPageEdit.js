import React, { useEffect, useState } from 'react';
import { Container, Card, Form, Button, Alert, Spinner, Row, Col } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios';

const MyPageEdit = () => {
    const [userInfo, setUserInfo] = useState({
        name: '',
        email: '',
        phone: '',
        zipcode: '',
        detailAddress: '',
        password: ''
    });
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        loadUserInfo();
    }, []);

    const loadUserInfo = async () => {
        try {
            const response = await api.get('/members/me');
            setUserInfo({
                name: response.data.name || '',
                email: response.data.email || '',
                phone: response.data.phone || '',
                zipcode: response.data.zipcode || '',
                detailAddress: response.data.detailAddress || '',
                password: ''
            });
        } catch (error) {
            console.error('Failed to load user info:', error);
            if (error.response?.status === 401) {
                setError('로그인이 필요합니다.');
                setTimeout(() => navigate('/login'), 2000);
            } else {
                setError('사용자 정보를 불러오는데 실패했습니다.');
            }
        } finally {
            setLoading(false);
        }
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setUserInfo(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');
        setSaving(true);

        try {
            // 비밀번호가 비어있으면 제외
            const updateData = { ...userInfo };
            if (!updateData.password) {
                delete updateData.password;
            }

            await api.put('/members', updateData);
            setSuccess('회원 정보가 수정되었습니다.');
            setTimeout(() => navigate('/mypage'), 1500);
        } catch (error) {
            console.error('Failed to update user info:', error);
            if (error.response?.status === 401) {
                setError('로그인이 필요합니다.');
                setTimeout(() => navigate('/login'), 2000);
            } else {
                setError(error.response?.data?.message || '정보 수정에 실패했습니다.');
            }
        } finally {
            setSaving(false);
        }
    };

    if (loading) {
        return (
            <Container className="text-center my-5">
                <Spinner animation="border" />
                <p className="mt-3">로딩 중...</p>
            </Container>
        );
    }

    return (
        <Container className="my-5">
            <Row>
                <Col lg={8} className="mx-auto">
                    <h2 className="mb-4">회원 정보 수정</h2>

                    {error && <Alert variant="danger">{error}</Alert>}
                    {success && <Alert variant="success">{success}</Alert>}

                    <Card>
                        <Card.Body>
                            <Form onSubmit={handleSubmit}>
                                <Form.Group className="mb-3">
                                    <Form.Label>이름 *</Form.Label>
                                    <Form.Control
                                        type="text"
                                        name="name"
                                        value={userInfo.name}
                                        onChange={handleChange}
                                        required
                                        placeholder="이름을 입력하세요"
                                    />
                                </Form.Group>

                                <Form.Group className="mb-3">
                                    <Form.Label>이메일 *</Form.Label>
                                    <Form.Control
                                        type="email"
                                        name="email"
                                        value={userInfo.email}
                                        onChange={handleChange}
                                        required
                                        placeholder="example@email.com"
                                    />
                                </Form.Group>

                                <Form.Group className="mb-3">
                                    <Form.Label>전화번호</Form.Label>
                                    <Form.Control
                                        type="tel"
                                        name="phone"
                                        value={userInfo.phone}
                                        onChange={handleChange}
                                        placeholder="010-1234-5678"
                                    />
                                </Form.Group>

                                <Form.Group className="mb-3">
                                    <Form.Label>우편번호</Form.Label>
                                    <Form.Control
                                        type="text"
                                        name="zipcode"
                                        value={userInfo.zipcode}
                                        onChange={handleChange}
                                        placeholder="12345"
                                    />
                                </Form.Group>

                                <Form.Group className="mb-3">
                                    <Form.Label>상세주소</Form.Label>
                                    <Form.Control
                                        type="text"
                                        name="detailAddress"
                                        value={userInfo.detailAddress}
                                        onChange={handleChange}
                                        placeholder="상세주소를 입력하세요"
                                    />
                                </Form.Group>

                                <Form.Group className="mb-3">
                                    <Form.Label>비밀번호 변경 (선택)</Form.Label>
                                    <Form.Control
                                        type="password"
                                        name="password"
                                        value={userInfo.password}
                                        onChange={handleChange}
                                        placeholder="변경하지 않으려면 비워두세요"
                                    />
                                    <Form.Text className="text-muted">
                                        비밀번호를 변경하지 않으시려면 이 필드를 비워두세요.
                                    </Form.Text>
                                </Form.Group>

                                <div className="d-flex gap-2">
                                    <Button
                                        variant="primary"
                                        type="submit"
                                        disabled={saving}
                                    >
                                        {saving ? (
                                            <>
                                                <Spinner
                                                    as="span"
                                                    animation="border"
                                                    size="sm"
                                                    role="status"
                                                    className="me-2"
                                                />
                                                저장 중...
                                            </>
                                        ) : '저장'}
                                    </Button>
                                    <Button
                                        variant="outline-secondary"
                                        onClick={() => navigate('/mypage')}
                                        disabled={saving}
                                    >
                                        취소
                                    </Button>
                                </div>
                            </Form>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};

export default MyPageEdit;
