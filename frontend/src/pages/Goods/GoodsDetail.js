import React, { useEffect, useState } from 'react';
import { Container, Row, Col, Card, Button, Spinner, Alert, Form, Badge } from 'react-bootstrap';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../../api/axios';

const GoodsDetail = () => {
    const { goodsId } = useParams();
    const navigate = useNavigate();
    const [product, setProduct] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [quantity, setQuantity] = useState(1);
    const [selectedImage, setSelectedImage] = useState(0);

    useEffect(() => {
        loadProduct();
    }, [goodsId]);

    const loadProduct = async () => {
        try {
            const response = await api.get(`/goods/${goodsId}`);
            setProduct(response.data);
        } catch (error) {
            console.error('Failed to load product:', error);
            setError('상품 정보를 불러오는데 실패했습니다.');
        } finally {
            setLoading(false);
        }
    };

    const handleAddToCart = async () => {
        setError('');
        setSuccess('');

        try {
            await api.post('/carts', {
                goodsId: parseInt(goodsId),
                amount: quantity,
                optionNumber: null
            });
            setSuccess('장바구니에 상품이 추가되었습니다!');
            setTimeout(() => setSuccess(''), 3000);
        } catch (error) {
            console.error('Failed to add to cart:', error);
            if (error.response) {
                if (error.response.status === 401) {
                    alert('로그인이 필요합니다.');
                    navigate('/login');
                } else {
                    setError(error.response.data.errorMessage || '장바구니 추가에 실패했습니다.');
                }
            } else {
                setError('서버와 연결할 수 없습니다.');
            }
        }
    };

    if (loading) {
        return (
            <Container className="text-center my-5">
                <Spinner animation="border" />
                <p className="mt-3">상품 정보를 불러오는 중...</p>
            </Container>
        );
    }

    if (error && !product) {
        return (
            <Container className="my-5">
                <Alert variant="danger">{error}</Alert>
                <Button onClick={() => navigate('/goods')}>상품 목록으로</Button>
            </Container>
        );
    }

    return (
        <Container className="my-5">
            {error && <Alert variant="danger" dismissible onClose={() => setError('')}>{error}</Alert>}
            {success && <Alert variant="success" dismissible onClose={() => setSuccess('')}>{success}</Alert>}

            <Row>
                {/* 상품 이미지 */}
                <Col lg={6}>
                    <Card className="mb-3">
                        <Card.Img
                            variant="top"
                            src={product?.imageList?.[selectedImage]?.fileUrl ? `http://localhost:8080${product.imageList[selectedImage].fileUrl}` : 'https://via.placeholder.com/500'}
                            style={{ height: '500px', objectFit: 'cover' }}
                        />
                    </Card>
                    {product?.imageList?.length > 1 && (
                        <Row>
                            {product.imageList.map((image, index) => (
                                <Col key={index} xs={3} className="mb-2">
                                    <img
                                        src={`http://localhost:8080${image.fileUrl}`}
                                        alt={`Thumbnail ${index + 1}`}
                                        className={`img-fluid rounded cursor-pointer ${selectedImage === index ? 'border border-primary border-3' : ''}`}
                                        style={{ cursor: 'pointer', height: '100px', objectFit: 'cover' }}
                                        onClick={() => setSelectedImage(index)}
                                    />
                                </Col>
                            ))}
                        </Row>
                    )}
                </Col>

                {/* 상품 정보 */}
                <Col lg={6}>
                    <Card>
                        <Card.Body>
                            <h2 className="mb-3">{product?.goodsName}</h2>

                            <div className="mb-3">
                                <Badge bg="secondary">{product?.category?.category || '카테고리 없음'}</Badge>
                            </div>

                            <h3 className="text-primary mb-4">
                                ₩{product?.price?.toLocaleString()}
                            </h3>

                            {product?.goodsDescription && (
                                <div className="mb-4">
                                    <h5>상품 설명</h5>
                                    <p style={{ whiteSpace: 'pre-wrap' }}>{product.goodsDescription}</p>
                                </div>
                            )}

                            <hr />

                            {/* 수량 선택 */}
                            <Form.Group className="mb-4">
                                <Form.Label><strong>수량</strong></Form.Label>
                                <div className="d-flex align-items-center gap-3">
                                    <Button
                                        variant="outline-secondary"
                                        onClick={() => setQuantity(Math.max(1, quantity - 1))}
                                    >
                                        -
                                    </Button>
                                    <Form.Control
                                        type="number"
                                        value={quantity}
                                        onChange={(e) => setQuantity(Math.max(1, parseInt(e.target.value) || 1))}
                                        style={{ width: '80px', textAlign: 'center' }}
                                        min="1"
                                    />
                                    <Button
                                        variant="outline-secondary"
                                        onClick={() => setQuantity(quantity + 1)}
                                    >
                                        +
                                    </Button>
                                </div>
                            </Form.Group>

                            {/* 총 가격 */}
                            <div className="mb-4">
                                <h5>총 가격</h5>
                                <h4 className="text-primary">
                                    ₩{(product?.price * quantity)?.toLocaleString()}
                                </h4>
                            </div>

                            {/* 버튼 */}
                            <div className="d-flex gap-2">
                                <Button
                                    variant="primary"
                                    size="lg"
                                    className="flex-grow-1"
                                    onClick={handleAddToCart}
                                >
                                    <i className="fas fa-shopping-cart me-2"></i>
                                    장바구니 담기
                                </Button>
                                <Button
                                    variant="outline-secondary"
                                    size="lg"
                                    onClick={() => navigate('/goods')}
                                >
                                    목록으로
                                </Button>
                            </div>

                            {/* 판매자 정보 */}
                            {product?.memberLoginId && (
                                <div className="mt-4 pt-3 border-top">
                                    <small className="text-muted">
                                        판매자: {product.memberLoginId}
                                    </small>
                                </div>
                            )}
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};

export default GoodsDetail;
