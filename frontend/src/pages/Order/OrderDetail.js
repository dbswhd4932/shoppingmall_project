import React, { useEffect, useState } from 'react';
import { Container, Row, Col, Card, Spinner, Alert, Badge, Button } from 'react-bootstrap';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../../api/axios';

const OrderDetail = () => {
    const { orderId } = useParams();
    const navigate = useNavigate();
    const [order, setOrder] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        loadOrderDetail();
    }, [orderId]);

    const loadOrderDetail = async () => {
        try {
            const response = await api.get(`/orders/${orderId}`);
            console.log('Order detail:', response.data);
            setOrder(response.data);
        } catch (error) {
            console.error('Failed to load order detail:', error);
            if (error.response?.status === 401) {
                alert('로그인이 필요합니다.');
                navigate('/login');
            } else {
                setError('주문 상세 정보를 불러오는데 실패했습니다.');
            }
        } finally {
            setLoading(false);
        }
    };

    const getOrderStatusBadge = (status) => {
        const statusMap = {
            'ORDER': { variant: 'primary', text: '주문완료' },
            'CANCEL': { variant: 'danger', text: '주문취소' },
            'DELIVERY': { variant: 'info', text: '배송중' },
            'COMPLETE': { variant: 'success', text: '배송완료' }
        };

        const statusInfo = statusMap[status] || { variant: 'secondary', text: status };
        return <Badge bg={statusInfo.variant}>{statusInfo.text}</Badge>;
    };

    const formatDate = (dateString) => {
        if (!dateString) return '-';
        const date = new Date(dateString);
        return date.toLocaleString('ko-KR', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit'
        });
    };

    if (loading) {
        return (
            <Container className="text-center my-5">
                <Spinner animation="border" />
                <p className="mt-3">주문 상세 정보를 불러오는 중...</p>
            </Container>
        );
    }

    if (error || !order) {
        return (
            <Container className="my-5">
                <Alert variant="danger">{error || '주문 정보를 찾을 수 없습니다.'}</Alert>
                <Button onClick={() => navigate('/orders')}>주문 목록으로</Button>
            </Container>
        );
    }

    return (
        <Container className="my-5">
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h2>주문 상세</h2>
                <Button variant="outline-secondary" onClick={() => navigate('/orders')}>
                    목록으로
                </Button>
            </div>

            <Row>
                <Col lg={8}>
                    {/* 주문 정보 */}
                    <Card className="mb-4">
                        <Card.Header>
                            <div className="d-flex justify-content-between align-items-center">
                                <h5 className="mb-0">주문 정보</h5>
                                {getOrderStatusBadge(order.orderStatus)}
                            </div>
                        </Card.Header>
                        <Card.Body>
                            <Row className="mb-3">
                                <Col sm={3}><strong>주문번호</strong></Col>
                                <Col sm={9}><strong>{order.orderNumber}</strong></Col>
                            </Row>
                            <Row className="mb-3">
                                <Col sm={3}><strong>주문일시</strong></Col>
                                <Col sm={9}>{formatDate(order.orderTime)}</Col>
                            </Row>
                            <Row>
                                <Col sm={3}><strong>주문 상품수</strong></Col>
                                <Col sm={9}>{order.goodsId?.length || 0}개</Col>
                            </Row>
                        </Card.Body>
                    </Card>

                    {/* 배송 정보 */}
                    <Card className="mb-4">
                        <Card.Header>
                            <h5 className="mb-0">배송 정보</h5>
                        </Card.Header>
                        <Card.Body>
                            <Row className="mb-3">
                                <Col sm={3}><strong>수취인</strong></Col>
                                <Col sm={9}>{order.name}</Col>
                            </Row>
                            <Row className="mb-3">
                                <Col sm={3}><strong>연락처</strong></Col>
                                <Col sm={9}>{order.phone}</Col>
                            </Row>
                            <Row className="mb-3">
                                <Col sm={3}><strong>배송지</strong></Col>
                                <Col sm={9}>
                                    ({order.zipcode}) {order.detailAddress}
                                </Col>
                            </Row>
                            {order.requirement && (
                                <Row>
                                    <Col sm={3}><strong>배송 요청사항</strong></Col>
                                    <Col sm={9}>{order.requirement}</Col>
                                </Row>
                            )}
                        </Card.Body>
                    </Card>
                </Col>

                <Col lg={4}>
                    {/* 결제 정보 */}
                    <Card className="sticky-top" style={{ top: '20px' }}>
                        <Card.Header>
                            <h5 className="mb-0">결제 정보</h5>
                        </Card.Header>
                        <Card.Body>
                            <div className="d-flex justify-content-between mb-2">
                                <span>주문 금액</span>
                                <span>₩{order.totalPrice?.toLocaleString()}</span>
                            </div>
                            <div className="d-flex justify-content-between mb-2">
                                <span>배송비</span>
                                <span>무료</span>
                            </div>
                            <hr />
                            <div className="d-flex justify-content-between mb-3">
                                <h5>총 결제 금액</h5>
                                <h5 className="text-primary">₩{order.totalPrice?.toLocaleString()}</h5>
                            </div>

                            {order.orderStatus === 'ORDER' && (
                                <div className="d-grid">
                                    <Button variant="outline-danger" size="lg">
                                        주문 취소
                                    </Button>
                                </div>
                            )}
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};

export default OrderDetail;
