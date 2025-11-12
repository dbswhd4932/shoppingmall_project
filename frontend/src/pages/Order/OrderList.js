import React, { useEffect, useState } from 'react';
import { Container, Row, Col, Card, Spinner, Alert, Badge, Table } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import api from '../../api/axios';

const OrderList = () => {
    const navigate = useNavigate();
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        loadOrders();
    }, []);

    const loadOrders = async () => {
        try {
            const response = await api.get('/orders?page=0&size=100');
            console.log('Orders:', response.data);
            setOrders(response.data || []);
        } catch (error) {
            console.error('Failed to load orders:', error);
            if (error.response?.status === 401) {
                alert('로그인이 필요합니다.');
                navigate('/login');
            } else {
                setError('주문 내역을 불러오는데 실패했습니다.');
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
        return date.toLocaleDateString('ko-KR', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
        });
    };

    if (loading) {
        return (
            <Container className="text-center my-5">
                <Spinner animation="border" />
                <p className="mt-3">주문 내역을 불러오는 중...</p>
            </Container>
        );
    }

    return (
        <Container className="my-5">
            <h2 className="mb-4">주문 내역</h2>

            {error && <Alert variant="danger" dismissible onClose={() => setError('')}>{error}</Alert>}

            {orders.length === 0 ? (
                <Card className="text-center py-5">
                    <Card.Body>
                        <p className="text-muted mb-4">주문 내역이 없습니다.</p>
                        <a href="/goods" className="btn btn-primary">쇼핑하러 가기</a>
                    </Card.Body>
                </Card>
            ) : (
                <>
                    {/* 데스크탑 뷰 */}
                    <div className="d-none d-md-block">
                        <Table responsive bordered hover>
                            <thead className="table-light">
                                <tr>
                                    <th style={{ width: '15%' }}>주문번호</th>
                                    <th style={{ width: '20%' }}>주문일시</th>
                                    <th style={{ width: '20%' }}>수취인</th>
                                    <th style={{ width: '15%' }}>총 금액</th>
                                    <th style={{ width: '15%' }}>주문상태</th>
                                    <th style={{ width: '15%' }}>상품수</th>
                                </tr>
                            </thead>
                            <tbody>
                                {orders.map(order => (
                                    <tr
                                        key={order.orderId}
                                        style={{ cursor: 'pointer' }}
                                        onClick={() => navigate(`/orders/${order.orderId}`)}
                                    >
                                        <td>
                                            <small className="text-muted">
                                                {order.orderNumber || '-'}
                                            </small>
                                        </td>
                                        <td>{formatDate(order.orderTime)}</td>
                                        <td>
                                            <div>{order.name}</div>
                                            <small className="text-muted">{order.phone}</small>
                                        </td>
                                        <td>
                                            <strong className="text-primary">
                                                ₩{order.totalPrice?.toLocaleString()}
                                            </strong>
                                        </td>
                                        <td>{getOrderStatusBadge(order.orderStatus)}</td>
                                        <td>{order.goodsId?.length || 0}개</td>
                                    </tr>
                                ))}
                            </tbody>
                        </Table>
                    </div>

                    {/* 모바일 뷰 */}
                    <div className="d-md-none">
                        {orders.map(order => (
                            <Card
                                key={order.orderId}
                                className="mb-3"
                                style={{ cursor: 'pointer' }}
                                onClick={() => navigate(`/orders/${order.orderId}`)}
                            >
                                <Card.Body>
                                    <div className="d-flex justify-content-between align-items-start mb-2">
                                        <div>
                                            <small className="text-muted">주문번호</small>
                                            <div className="fw-bold">
                                                {order.orderNumber || '-'}
                                            </div>
                                        </div>
                                        {getOrderStatusBadge(order.orderStatus)}
                                    </div>

                                    <div className="mb-2">
                                        <small className="text-muted">주문일시</small>
                                        <div>{formatDate(order.orderTime)}</div>
                                    </div>

                                    <div className="mb-2">
                                        <small className="text-muted">수취인</small>
                                        <div>{order.name} ({order.phone})</div>
                                    </div>

                                    <div className="d-flex justify-content-between align-items-center mt-3 pt-3 border-top">
                                        <div>
                                            <small className="text-muted">상품수</small>
                                            <div>{order.goodsId?.length || 0}개</div>
                                        </div>
                                        <div className="text-end">
                                            <small className="text-muted">총 금액</small>
                                            <div className="h5 text-primary mb-0">
                                                ₩{order.totalPrice?.toLocaleString()}
                                            </div>
                                        </div>
                                    </div>
                                </Card.Body>
                            </Card>
                        ))}
                    </div>
                </>
            )}
        </Container>
    );
};

export default OrderList;
