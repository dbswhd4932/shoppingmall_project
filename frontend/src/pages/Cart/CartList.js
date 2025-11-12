import React, { useEffect, useState } from 'react';
import { Container, Row, Col, Card, Button, Form, Spinner, Alert, Table } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import api from '../../api/axios';

const CartList = () => {
    const navigate = useNavigate();
    const [cartItems, setCartItems] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        loadCart();
    }, []);

    const loadCart = async () => {
        try {
            const response = await api.get('/carts?page=0&size=100');
            console.log('Cart API Response:', response.data); // 디버깅용
            setCartItems(response.data.content || []);
        } catch (error) {
            console.error('Failed to load cart:', error);
            if (error.response?.status === 401) {
                alert('로그인이 필요합니다.');
                navigate('/login');
            } else {
                setError('장바구니를 불러오는데 실패했습니다.');
            }
        } finally {
            setLoading(false);
        }
    };

    // 수량 변경 (+ - 버튼)
    const handleQuantityChange = async (cartId, newAmount, currentItem) => {
        if (newAmount < 1) return;

        try {
            const requestData = {
                amount: newAmount
            };

            // optionNumber가 있으면 포함, 없으면 필드 자체를 보내지 않음
            if (currentItem.optionNumber !== null && currentItem.optionNumber !== undefined) {
                requestData.optionNumber = currentItem.optionNumber;
            }

            await api.put(`/carts/${cartId}`, requestData);

            // 로컬 상태 업데이트 - 총 가격 재계산
            setCartItems(prevItems => prevItems.map(item =>
                item.cartId === cartId
                    ? { ...item, totalAmount: newAmount, totalPrice: item.price * newAmount }
                    : item
            ));
        } catch (error) {
            console.error('Failed to update quantity:', error);
            setError(error.response?.data?.errorMessage || '수량 변경에 실패했습니다.');
        }
    };

    // 수량 직접 입력 (로컬 상태만 업데이트)
    const handleQuantityInputChange = (cartId, value, currentItem) => {
        const newAmount = parseInt(value);
        if (isNaN(newAmount) || newAmount < 1) {
            return;
        }

        // 로컬 상태만 즉시 업데이트 (UI 반응성 향상)
        setCartItems(prevItems => prevItems.map(item =>
            item.cartId === cartId
                ? { ...item, totalAmount: newAmount, totalPrice: item.price * newAmount }
                : item
        ));
    };

    // 수량 입력 완료 시 (blur 이벤트)
    const handleQuantityBlur = async (cartId, currentItem) => {
        try {
            const requestData = {
                amount: currentItem.totalAmount
            };

            // optionNumber가 있으면 포함, 없으면 필드 자체를 보내지 않음
            if (currentItem.optionNumber !== null && currentItem.optionNumber !== undefined) {
                requestData.optionNumber = currentItem.optionNumber;
            }

            await api.put(`/carts/${cartId}`, requestData);
        } catch (error) {
            console.error('Failed to update quantity:', error);
            setError(error.response?.data?.errorMessage || '수량 변경에 실패했습니다.');
            // 실패 시 다시 로드
            loadCart();
        }
    };

    // 장바구니 상품 삭제
    const handleDelete = async (cartId) => {
        if (!window.confirm('이 상품을 장바구니에서 삭제하시겠습니까?')) {
            return;
        }

        try {
            await api.delete(`/carts/${cartId}`);
            setCartItems(cartItems.filter(item => item.cartId !== cartId));
        } catch (error) {
            console.error('Failed to delete cart item:', error);
            setError(error.response?.data?.errorMessage || '삭제에 실패했습니다.');
        }
    };

    // 총 금액 계산
    const getTotalPrice = () => {
        return cartItems.reduce((sum, item) => sum + item.totalPrice, 0);
    };

    if (loading) {
        return (
            <Container className="text-center my-5">
                <Spinner animation="border" />
                <p className="mt-3">장바구니를 불러오는 중...</p>
            </Container>
        );
    }

    return (
        <Container className="my-5">
            <h2 className="mb-4">장바구니</h2>

            {error && <Alert variant="danger" dismissible onClose={() => setError('')}>{error}</Alert>}

            {cartItems.length === 0 ? (
                <Card className="text-center py-5">
                    <Card.Body>
                        <p className="text-muted mb-4">장바구니가 비어있습니다.</p>
                        <Button variant="primary" onClick={() => navigate('/goods')}>
                            쇼핑 계속하기
                        </Button>
                    </Card.Body>
                </Card>
            ) : (
                <>
                    {/* 데스크탑 뷰 (테이블) */}
                    <div className="d-none d-md-block">
                        <Table responsive bordered hover>
                            <thead className="table-light">
                                <tr>
                                    <th style={{ width: '10%' }}>이미지</th>
                                    <th style={{ width: '30%' }}>상품명</th>
                                    <th style={{ width: '15%' }}>개당 가격</th>
                                    <th style={{ width: '20%' }}>수량</th>
                                    <th style={{ width: '15%' }}>총 가격</th>
                                    <th style={{ width: '10%' }}>삭제</th>
                                </tr>
                            </thead>
                            <tbody>
                                {cartItems.map(item => (
                                    <tr key={item.cartId}>
                                        <td>
                                            <img
                                                src={item.imageUrl ? `http://localhost:8080${item.imageUrl}` : 'https://via.placeholder.com/100'}
                                                alt={item.goodsName}
                                                style={{ width: '80px', height: '80px', objectFit: 'cover' }}
                                                className="rounded"
                                            />
                                        </td>
                                        <td className="align-middle">
                                            <div
                                                style={{ cursor: 'pointer' }}
                                                onClick={() => navigate(`/goods/${item.goodsId}`)}
                                            >
                                                <strong>{item.goodsName}</strong>
                                            </div>
                                        </td>
                                        <td className="align-middle">
                                            <strong>₩{item.price?.toLocaleString()}</strong>
                                        </td>
                                        <td className="align-middle">
                                            <div className="d-flex align-items-center justify-content-center gap-2">
                                                <Button
                                                    variant="outline-secondary"
                                                    size="sm"
                                                    onClick={() => handleQuantityChange(item.cartId, item.totalAmount - 1, item)}
                                                    disabled={item.totalAmount <= 1}
                                                >
                                                    -
                                                </Button>
                                                <Form.Control
                                                    type="number"
                                                    value={item.totalAmount}
                                                    onChange={(e) => handleQuantityInputChange(item.cartId, e.target.value, item)}
                                                    onBlur={() => handleQuantityBlur(item.cartId, item)}
                                                    style={{ width: '70px', textAlign: 'center' }}
                                                    min="1"
                                                />
                                                <Button
                                                    variant="outline-secondary"
                                                    size="sm"
                                                    onClick={() => handleQuantityChange(item.cartId, item.totalAmount + 1, item)}
                                                >
                                                    +
                                                </Button>
                                            </div>
                                        </td>
                                        <td className="align-middle">
                                            <strong className="text-primary">₩{item.totalPrice?.toLocaleString()}</strong>
                                        </td>
                                        <td className="align-middle text-center">
                                            <Button
                                                variant="outline-danger"
                                                size="sm"
                                                onClick={() => handleDelete(item.cartId)}
                                            >
                                                <i className="fas fa-trash"></i>
                                            </Button>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </Table>
                    </div>

                    {/* 모바일 뷰 (카드) */}
                    <div className="d-md-none">
                        {cartItems.map(item => (
                            <Card key={item.cartId} className="mb-3">
                                <Card.Body>
                                    <Row>
                                        <Col xs={4}>
                                            <img
                                                src={item.imageUrl ? `http://localhost:8080${item.imageUrl}` : 'https://via.placeholder.com/100'}
                                                alt={item.goodsName}
                                                className="img-fluid rounded"
                                                onClick={() => navigate(`/goods/${item.goodsId}`)}
                                                style={{ cursor: 'pointer' }}
                                            />
                                        </Col>
                                        <Col xs={8}>
                                            <h6
                                                onClick={() => navigate(`/goods/${item.goodsId}`)}
                                                style={{ cursor: 'pointer' }}
                                            >
                                                {item.goodsName}
                                            </h6>
                                            <p className="text-muted mb-1">개당 가격: ₩{item.price?.toLocaleString()}</p>
                                            <div className="d-flex align-items-center gap-2 mb-2">
                                                <Button
                                                    variant="outline-secondary"
                                                    size="sm"
                                                    onClick={() => handleQuantityChange(item.cartId, item.totalAmount - 1, item)}
                                                    disabled={item.totalAmount <= 1}
                                                >
                                                    -
                                                </Button>
                                                <Form.Control
                                                    type="number"
                                                    value={item.totalAmount}
                                                    onChange={(e) => handleQuantityInputChange(item.cartId, e.target.value, item)}
                                                    onBlur={() => handleQuantityBlur(item.cartId, item)}
                                                    style={{ width: '60px', textAlign: 'center' }}
                                                    min="1"
                                                    size="sm"
                                                />
                                                <Button
                                                    variant="outline-secondary"
                                                    size="sm"
                                                    onClick={() => handleQuantityChange(item.cartId, item.totalAmount + 1, item)}
                                                >
                                                    +
                                                </Button>
                                            </div>
                                            <div className="d-flex justify-content-between align-items-center">
                                                <strong className="text-primary">₩{item.totalPrice?.toLocaleString()}</strong>
                                                <Button
                                                    variant="outline-danger"
                                                    size="sm"
                                                    onClick={() => handleDelete(item.cartId)}
                                                >
                                                    삭제
                                                </Button>
                                            </div>
                                        </Col>
                                    </Row>
                                </Card.Body>
                            </Card>
                        ))}
                    </div>

                    {/* 총 금액 및 주문 버튼 */}
                    <Card className="mt-4">
                        <Card.Body>
                            <Row className="align-items-center">
                                <Col md={6}>
                                    <h4 className="mb-0">
                                        총 주문 금액: <span className="text-primary">₩{getTotalPrice().toLocaleString()}</span>
                                    </h4>
                                </Col>
                                <Col md={6} className="text-md-end mt-3 mt-md-0">
                                    <Button
                                        variant="outline-secondary"
                                        size="lg"
                                        className="me-2"
                                        onClick={() => navigate('/goods')}
                                    >
                                        쇼핑 계속하기
                                    </Button>
                                    <Button
                                        variant="primary"
                                        size="lg"
                                        onClick={() => navigate('/order')}
                                    >
                                        주문하기
                                    </Button>
                                </Col>
                            </Row>
                        </Card.Body>
                    </Card>
                </>
            )}
        </Container>
    );
};

export default CartList;
