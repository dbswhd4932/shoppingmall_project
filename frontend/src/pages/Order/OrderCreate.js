import React, { useEffect, useState } from 'react';
import { Container, Row, Col, Card, Button, Form, Alert, Spinner, Table } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import api from '../../api/axios';

const OrderCreate = () => {
    const navigate = useNavigate();
    const [cartItems, setCartItems] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [submitting, setSubmitting] = useState(false);

    // 배송 정보
    const [orderForm, setOrderForm] = useState({
        name: '',
        phone: '',
        zipcode: '',
        detailAddress: '',
        requirement: ''
    });

    useEffect(() => {
        loadCart();
    }, []);

    const loadCart = async () => {
        try {
            const response = await api.get('/carts?page=0&size=100');
            const items = response.data.content || [];

            if (items.length === 0) {
                alert('장바구니가 비어있습니다.');
                navigate('/cart');
                return;
            }

            setCartItems(items);
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

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setOrderForm(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const getTotalPrice = () => {
        return cartItems.reduce((sum, item) => sum + item.totalPrice, 0);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSubmitting(true);

        // 유효성 검사
        if (!orderForm.name || !orderForm.phone || !orderForm.zipcode || !orderForm.detailAddress) {
            setError('필수 항목을 모두 입력해주세요.');
            setSubmitting(false);
            return;
        }

        try {
            // merchantId 생성
            const merchantIdResponse = await api.get('/merchantId');
            const merchantId = merchantIdResponse.data.merchantId;

            // 주문 상품 목록 생성
            const orderItemCreates = cartItems.map(item => ({
                goodsId: item.goodsId,
                amount: item.totalAmount,
                orderPrice: item.totalPrice
            }));

            // 주문 데이터 구성 (결제 없이 테스트용)
            const orderData = {
                name: orderForm.name,
                phone: orderForm.phone,
                zipcode: orderForm.zipcode,
                detailAddress: orderForm.detailAddress,
                requirement: orderForm.requirement || '',
                orderItemCreates: orderItemCreates,
                totalPrice: getTotalPrice(),
                impUid: 'test_' + Date.now(), // 테스트용 결제 ID
                merchantId: merchantId,
                cardCompany: '테스트카드',
                cardNumber: '0000-0000-0000-0000'
            };

            // 주문 생성
            await api.post('/orders', orderData);

            alert('주문이 완료되었습니다!');
            navigate('/orders'); // 주문 완료 후 주문 목록으로 이동
        } catch (error) {
            console.error('Order failed:', error);
            setError(error.response?.data?.errorMessage || '주문 처리 중 오류가 발생했습니다.');
        } finally {
            setSubmitting(false);
        }
    };

    if (loading) {
        return (
            <Container className="text-center my-5">
                <Spinner animation="border" />
                <p className="mt-3">주문 정보를 불러오는 중...</p>
            </Container>
        );
    }

    return (
        <Container className="my-5">
            <h2 className="mb-4">주문/결제</h2>

            {error && <Alert variant="danger" dismissible onClose={() => setError('')}>{error}</Alert>}

            <Row>
                {/* 주문 상품 정보 */}
                <Col lg={7}>
                    <Card className="mb-4">
                        <Card.Header>
                            <h5 className="mb-0">주문 상품 정보</h5>
                        </Card.Header>
                        <Card.Body>
                            <Table responsive>
                                <thead>
                                    <tr>
                                        <th>상품명</th>
                                        <th>수량</th>
                                        <th>가격</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {cartItems.map(item => (
                                        <tr key={item.cartId}>
                                            <td>{item.goodsName}</td>
                                            <td>{item.totalAmount}개</td>
                                            <td>₩{item.totalPrice?.toLocaleString()}</td>
                                        </tr>
                                    ))}
                                </tbody>
                            </Table>
                        </Card.Body>
                    </Card>

                    {/* 배송 정보 입력 */}
                    <Card>
                        <Card.Header>
                            <h5 className="mb-0">배송 정보</h5>
                        </Card.Header>
                        <Card.Body>
                            <Form onSubmit={handleSubmit}>
                                <Form.Group className="mb-3">
                                    <Form.Label>수취인 이름 <span className="text-danger">*</span></Form.Label>
                                    <Form.Control
                                        type="text"
                                        name="name"
                                        value={orderForm.name}
                                        onChange={handleInputChange}
                                        placeholder="홍길동"
                                        required
                                    />
                                </Form.Group>

                                <Form.Group className="mb-3">
                                    <Form.Label>전화번호 <span className="text-danger">*</span></Form.Label>
                                    <Form.Control
                                        type="tel"
                                        name="phone"
                                        value={orderForm.phone}
                                        onChange={handleInputChange}
                                        placeholder="010-1234-5678"
                                        required
                                    />
                                </Form.Group>

                                <Form.Group className="mb-3">
                                    <Form.Label>우편번호 <span className="text-danger">*</span></Form.Label>
                                    <Form.Control
                                        type="text"
                                        name="zipcode"
                                        value={orderForm.zipcode}
                                        onChange={handleInputChange}
                                        placeholder="12345"
                                        required
                                    />
                                </Form.Group>

                                <Form.Group className="mb-3">
                                    <Form.Label>상세주소 <span className="text-danger">*</span></Form.Label>
                                    <Form.Control
                                        type="text"
                                        name="detailAddress"
                                        value={orderForm.detailAddress}
                                        onChange={handleInputChange}
                                        placeholder="상세주소를 입력하세요"
                                        required
                                    />
                                </Form.Group>

                                <Form.Group className="mb-3">
                                    <Form.Label>배송 요청사항</Form.Label>
                                    <Form.Control
                                        as="textarea"
                                        rows={3}
                                        name="requirement"
                                        value={orderForm.requirement}
                                        onChange={handleInputChange}
                                        placeholder="배송 시 요청사항을 입력하세요"
                                    />
                                </Form.Group>
                            </Form>
                        </Card.Body>
                    </Card>
                </Col>

                {/* 결제 정보 */}
                <Col lg={5}>
                    <Card className="sticky-top" style={{ top: '20px' }}>
                        <Card.Header>
                            <h5 className="mb-0">결제 정보</h5>
                        </Card.Header>
                        <Card.Body>
                            <div className="d-flex justify-content-between mb-2">
                                <span>상품 금액</span>
                                <span>₩{getTotalPrice().toLocaleString()}</span>
                            </div>
                            <div className="d-flex justify-content-between mb-2">
                                <span>배송비</span>
                                <span>무료</span>
                            </div>
                            <hr />
                            <div className="d-flex justify-content-between mb-4">
                                <h5>총 결제 금액</h5>
                                <h5 className="text-primary">₩{getTotalPrice().toLocaleString()}</h5>
                            </div>

                            <div className="d-grid gap-2">
                                <Button
                                    variant="primary"
                                    size="lg"
                                    onClick={handleSubmit}
                                    disabled={submitting}
                                >
                                    {submitting ? '처리 중...' : '주문하기'}
                                </Button>
                                <Button
                                    variant="outline-secondary"
                                    onClick={() => navigate('/cart')}
                                >
                                    장바구니로 돌아가기
                                </Button>
                            </div>

                            <div className="mt-3">
                                <small className="text-muted">
                                    * 주문하기 버튼을 클릭하면 주문이 완료됩니다.
                                </small>
                            </div>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};

export default OrderCreate;
