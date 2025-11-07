import React, { useEffect, useState } from 'react';
import { Container, Row, Col, Card, Button, Spinner } from 'react-bootstrap';
import { Link, useNavigate } from 'react-router-dom';
import api from '../api/axios';

const Home = () => {
    const [products, setProducts] = useState([]);
    const [categories, setCategories] = useState([]);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {
        loadData();
    }, []);

    const loadData = async () => {
        try {
            const [productsRes, categoriesRes] = await Promise.all([
                api.get('/goods?page=0&size=8'),
                api.get('/categories')
            ]);
            setProducts(productsRes.data.content || []);
            setCategories(categoriesRes.data || []);
        } catch (error) {
            console.error('Failed to load data:', error);
        } finally {
            setLoading(false);
        }
    };

    const handleProductRegisterClick = async () => {
        try {
            // 권한 체크 API 호출
            const response = await api.get('/goods/check-access');

            // SELLER 권한이 있으면 상품 등록 페이지로 이동
            if (response.data.hasAccess) {
                navigate('/goods/create');
            }
        } catch (error) {
            if (error.response) {
                // Backend에서 온 에러 메시지를 알럿으로 표시
                alert(error.response.data.errorMessage);

                // 401 에러 (비로그인)인 경우 로그인 페이지로 이동
                if (error.response.status === 401) {
                    navigate('/login');
                }
                // 403 에러 (권한 없음)인 경우는 알럿만 표시
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
            {/* 상품 등록 버튼 */}
            <div className="d-flex justify-content-end mb-3">
                <Button
                    variant="success"
                    onClick={handleProductRegisterClick}
                    size="lg"
                >
                    상품 등록
                </Button>
            </div>

            {/* Hero Section */}
            <section className="bg-light py-5 mb-5 rounded">
                <Row className="align-items-center">
                    <Col lg={6}>
                        <h1 className="display-4 fw-bold">Welcome to Shopping Mall</h1>
                        <p className="lead">Discover the best products at the lowest prices</p>
                        <Link to="/goods" className="btn btn-primary btn-lg">Browse Products</Link>
                    </Col>
                    <Col lg={6}>
                        <img
                            src="https://via.placeholder.com/600x400"
                            alt="Hero"
                            className="img-fluid rounded"
                        />
                    </Col>
                </Row>
            </section>

            {/* Featured Products */}
            <section>
                <h2 className="mb-4">Featured Products</h2>
                {products.length === 0 ? (
                    <p>No products available.</p>
                ) : (
                    <Row>
                        {products.map(product => (
                            <Col key={product.goodsId} md={3} className="mb-4">
                                <Card className="h-100">
                                    <Card.Img
                                        variant="top"
                                        src={product.imageList?.[0]?.fileUrl || 'https://via.placeholder.com/300'}
                                    />
                                    <Card.Body>
                                        <Card.Title>{product.goodsName}</Card.Title>
                                        <Card.Text className="text-primary fw-bold">
                                            ${product.price?.toLocaleString()}
                                        </Card.Text>
                                        <Link
                                            to={`/goods/${product.goodsId}`}
                                            className="btn btn-sm btn-outline-primary"
                                        >
                                            View Details
                                        </Link>
                                    </Card.Body>
                                </Card>
                            </Col>
                        ))}
                    </Row>
                )}
                <div className="text-center mt-4">
                    <Link to="/goods" className="btn btn-outline-primary">
                        View More Products
                    </Link>
                </div>
            </section>

            {/* Categories */}
            <section className="mt-5">
                <h2 className="mb-4">Categories</h2>
                <Row>
                    {categories.map(category => (
                        <Col key={category.categoryId} md={3} className="mb-3">
                            <Link
                                to={`/goods?category=${category.categoryId}`}
                                className="text-decoration-none"
                            >
                                <Card className="text-center">
                                    <Card.Body>
                                        <Card.Title>{category.category}</Card.Title>
                                    </Card.Body>
                                </Card>
                            </Link>
                        </Col>
                    ))}
                </Row>
            </section>
        </Container>
    );
};

export default Home;
