import React, { useEffect, useState } from 'react';
import { Container, Card, Row, Col, Badge, Spinner, Alert, Button } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios';

const MyPage = () => {
    const [userInfo, setUserInfo] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        loadUserInfo();
    }, []);

    const loadUserInfo = async () => {
        try {
            const response = await api.get('/members/me');
            setUserInfo(response.data);
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

    const getRoleBadgeVariant = (role) => {
        const roleStr = typeof role === 'string' ? role : role;
        switch (roleStr) {
            case 'ROLE_ADMIN':
                return 'danger';
            case 'ROLE_SELLER':
                return 'success';
            case 'ROLE_USER':
                return 'primary';
            default:
                return 'secondary';
        }
    };

    const getRoleDisplayName = (role) => {
        const roleStr = typeof role === 'string' ? role : role;
        switch (roleStr) {
            case 'ROLE_ADMIN':
                return '관리자';
            case 'ROLE_SELLER':
                return '판매자';
            case 'ROLE_USER':
                return '일반회원';
            default:
                return roleStr;
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

    if (error) {
        return (
            <Container className="my-5">
                <Alert variant="danger">{error}</Alert>
            </Container>
        );
    }

    return (
        <Container className="my-5">
            <h2 className="mb-4">마이페이지</h2>

            <Row>
                <Col lg={8} className="mx-auto">
                    {/* 기본 정보 */}
                    <Card className="mb-4">
                        <Card.Header>
                            <h5 className="mb-0">기본 정보</h5>
                        </Card.Header>
                        <Card.Body>
                            <Row className="mb-3">
                                <Col sm={3}>
                                    <strong>아이디</strong>
                                </Col>
                                <Col sm={9}>
                                    {userInfo?.loginId}
                                </Col>
                            </Row>

                            <Row className="mb-3">
                                <Col sm={3}>
                                    <strong>이름</strong>
                                </Col>
                                <Col sm={9}>
                                    {userInfo?.name || '-'}
                                </Col>
                            </Row>

                            <Row className="mb-3">
                                <Col sm={3}>
                                    <strong>이메일</strong>
                                </Col>
                                <Col sm={9}>
                                    {userInfo?.email || '-'}
                                </Col>
                            </Row>

                            <Row className="mb-3">
                                <Col sm={3}>
                                    <strong>전화번호</strong>
                                </Col>
                                <Col sm={9}>
                                    {userInfo?.phone || '-'}
                                </Col>
                            </Row>

                            <Row className="mb-3">
                                <Col sm={3}>
                                    <strong>우편번호</strong>
                                </Col>
                                <Col sm={9}>
                                    {userInfo?.zipcode || '-'}
                                </Col>
                            </Row>

                            <Row className="mb-3">
                                <Col sm={3}>
                                    <strong>상세주소</strong>
                                </Col>
                                <Col sm={9}>
                                    {userInfo?.detailAddress || '-'}
                                </Col>
                            </Row>

                            <Row>
                                <Col sm={3}>
                                    <strong>권한</strong>
                                </Col>
                                <Col sm={9}>
                                    {userInfo?.roles?.map((role, index) => (
                                        <Badge
                                            key={index}
                                            bg={getRoleBadgeVariant(role)}
                                            className="me-2"
                                        >
                                            {getRoleDisplayName(role)}
                                        </Badge>
                                    ))}
                                </Col>
                            </Row>
                        </Card.Body>
                    </Card>

                    {/* 가입 정보 */}
                    <Card className="mb-4">
                        <Card.Header>
                            <h5 className="mb-0">가입 정보</h5>
                        </Card.Header>
                        <Card.Body>
                            <Row className="mb-3">
                                <Col sm={3}>
                                    <strong>가입일</strong>
                                </Col>
                                <Col sm={9}>
                                    {userInfo?.createdAt ? new Date(userInfo.createdAt).toLocaleString('ko-KR') : '-'}
                                </Col>
                            </Row>

                            <Row>
                                <Col sm={3}>
                                    <strong>최종 수정일</strong>
                                </Col>
                                <Col sm={9}>
                                    {userInfo?.updatedAt ? new Date(userInfo.updatedAt).toLocaleString('ko-KR') : '-'}
                                </Col>
                            </Row>
                        </Card.Body>
                    </Card>

                    {/* 버튼 영역 */}
                    <div className="d-flex gap-2">
                        <Button variant="primary" onClick={() => navigate('/mypage/edit')}>
                            정보 수정
                        </Button>
                        <Button variant="outline-secondary" onClick={() => navigate('/')}>
                            홈으로
                        </Button>
                    </div>
                </Col>
            </Row>
        </Container>
    );
};

export default MyPage;
