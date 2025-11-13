import React, { useEffect, useState } from 'react';
import { Container, Row, Col, Card, Spinner, Pagination, Button, ButtonGroup } from 'react-bootstrap';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import api from '../../api/axios';

const GoodsList = () => {
    const navigate = useNavigate();
    const [searchParams] = useSearchParams();
    const categoryId = searchParams.get('category');
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    useEffect(() => {
        loadProducts();
    }, [page, categoryId]);

    const loadProducts = async () => {
        try {
            let url = `/goods?page=${page}&size=12`;
            if (categoryId) {
                url += `&categoryId=${categoryId}`;
            }
            const response = await api.get(url);
            setProducts(response.data.content || []);
            setTotalPages(response.data.totalPages || 0);
        } catch (error) {
            console.error('Failed to load products:', error);
        } finally {
            setLoading(false);
        }
    };

    const handleAddToCart = async (goodsId) => {
        try {
            await api.post('/carts', {
                goodsId: goodsId,
                amount: 1,
                optionNumber: null
            });
            alert('장바구니에 상품이 추가되었습니다!');
        } catch (error) {
            if (error.response) {
                if (error.response.status === 401) {
                    alert('로그인이 필요합니다.');
                    navigate('/login');
                } else {
                    alert(error.response.data.errorMessage || '장바구니 추가에 실패했습니다.');
                }
            } else {
                alert('서버와 연결할 수 없습니다.');
            }
        }
    };

    if (loading) {
        return (
            <Container className="text-center my-5">
                <Spinner animation="border" />
            </Container>
        );
    }

    return (
        <Container className="my-4">
            <h2 className="mb-4">Products</h2>
            {products.length === 0 ? (
                <p>No products available.</p>
            ) : (
                <>
                    <Row>
                        {products.map(product => (
                            <Col key={product.goodsId} md={3} className="mb-4">
                                <Card className="h-100">
                                    <Card.Img
                                        variant="top"
                                        src={product.imageUrl ? `http://localhost:8080${product.imageUrl}` : 'https://via.placeholder.com/300'}
                                    />
                                    <Card.Body>
                                        <Card.Title>{product.goodsName}</Card.Title>
                                        <Card.Text className="text-primary fw-bold">
                                            ₩{product.price?.toLocaleString()}
                                        </Card.Text>
                                        <ButtonGroup className="w-100">
                                            <Button
                                                as={Link}
                                                to={`/goods/${product.goodsId}`}
                                                variant="outline-primary"
                                                size="sm"
                                            >
                                                상세보기
                                            </Button>
                                            <Button
                                                variant="primary"
                                                size="sm"
                                                onClick={(e) => {
                                                    e.preventDefault();
                                                    handleAddToCart(product.goodsId);
                                                }}
                                            >
                                                <i className="fas fa-shopping-cart"></i>
                                            </Button>
                                        </ButtonGroup>
                                    </Card.Body>
                                </Card>
                            </Col>
                        ))}
                    </Row>

                    {/* Pagination */}
                    {totalPages > 1 && (
                        <Pagination className="justify-content-center mt-4">
                            <Pagination.Prev
                                onClick={() => setPage(p => Math.max(0, p - 1))}
                                disabled={page === 0}
                            />
                            {[...Array(totalPages)].map((_, i) => (
                                <Pagination.Item
                                    key={i}
                                    active={i === page}
                                    onClick={() => setPage(i)}
                                >
                                    {i + 1}
                                </Pagination.Item>
                            ))}
                            <Pagination.Next
                                onClick={() => setPage(p => Math.min(totalPages - 1, p + 1))}
                                disabled={page === totalPages - 1}
                            />
                        </Pagination>
                    )}
                </>
            )}
        </Container>
    );
};

export default GoodsList;
