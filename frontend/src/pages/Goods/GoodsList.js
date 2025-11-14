import React, { useEffect, useState } from 'react';
import { Container, Row, Col, Card, Spinner, Pagination, Button, ButtonGroup, Form, InputGroup } from 'react-bootstrap';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import api from '../../api/axios';

const GoodsList = () => {
    const navigate = useNavigate();
    const [searchParams] = useSearchParams();
    const categoryParam = searchParams.get('category');
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    // 검색 키워드 상태
    const [keyword, setKeyword] = useState('');
    const [searchKeyword, setSearchKeyword] = useState(''); // 실제 검색에 사용되는 키워드

    useEffect(() => {
        loadProducts();
    }, [page, searchKeyword]);

    const loadProducts = async () => {
        setLoading(true);
        try {
            let url = `/goods?page=${page}&size=12`;

            // 검색 키워드가 있으면 검색 API 사용
            if (searchKeyword.trim()) {
                url = `/goods/search?keyword=${encodeURIComponent(searchKeyword.trim())}&page=${page}&size=12`;
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

    const handleSearch = () => {
        setPage(0); // 검색 시 첫 페이지로
        setSearchKeyword(keyword); // 검색 실행
    };

    const handleReset = () => {
        setKeyword('');
        setSearchKeyword('');
        setPage(0);
    };

    return (
        <Container className="my-4">
            <h2 className="mb-4">전체 상품</h2>

            {/* 검색 바 */}
            <div className="mb-4">
                <InputGroup size="lg">
                    <Form.Control
                        type="text"
                        placeholder="상품명을 입력하세요"
                        value={keyword}
                        onChange={(e) => setKeyword(e.target.value)}
                        onKeyPress={(e) => {
                            if (e.key === 'Enter') {
                                handleSearch();
                            }
                        }}
                    />
                    <Button variant="primary" onClick={handleSearch}>
                        <i className="fas fa-search"></i> 검색
                    </Button>
                    {searchKeyword && (
                        <Button variant="secondary" onClick={handleReset}>
                            <i className="fas fa-times"></i> 초기화
                        </Button>
                    )}
                </InputGroup>
                {searchKeyword && (
                    <div className="mt-2 text-muted">
                        검색어: <strong>"{searchKeyword}"</strong>
                    </div>
                )}
            </div>

            {loading ? (
                <div className="text-center my-5">
                    <Spinner animation="border" />
                </div>
            ) : products.length === 0 ? (
                <p className="text-center text-muted">검색 결과가 없습니다.</p>
            ) : (
                <>
                    <div className="mb-3 text-muted">
                        총 {products.length}개의 상품
                    </div>
                    <Row>
                        {products.map(product => (
                            <Col key={product.goodsId} md={3} className="mb-4">
                                <Card className="h-100 shadow-sm">
                                    <Card.Img
                                        variant="top"
                                        src={product.imageUrl ? `http://localhost:8080${product.imageUrl}` : 'https://via.placeholder.com/300'}
                                        style={{ height: '200px', objectFit: 'cover' }}
                                    />
                                    <Card.Body className="d-flex flex-column">
                                        <Card.Title className="text-truncate" title={product.goodsName}>
                                            {product.goodsName}
                                        </Card.Title>
                                        {product.categoryName && (
                                            <small className="text-muted mb-2">
                                                <i className="fas fa-tag me-1"></i>
                                                {product.categoryName}
                                            </small>
                                        )}
                                        <Card.Text className="text-primary fw-bold fs-5">
                                            ₩{product.price?.toLocaleString()}
                                        </Card.Text>
                                        <div className="mt-auto">
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
                                        </div>
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
