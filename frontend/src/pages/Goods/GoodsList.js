import React, { useEffect, useState } from 'react';
import { Container, Row, Col, Card, Spinner, Pagination } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import api from '../../api/axios';

const GoodsList = () => {
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    useEffect(() => {
        loadProducts();
    }, [page]);

    const loadProducts = async () => {
        try {
            const response = await api.get(`/goods?page=${page}&size=12`);
            setProducts(response.data.content || []);
            setTotalPages(response.data.totalPages || 0);
        } catch (error) {
            console.error('Failed to load products:', error);
        } finally {
            setLoading(false);
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
                            <Col key={product.id} md={3} className="mb-4">
                                <Card className="h-100">
                                    <Card.Img
                                        variant="top"
                                        src={product.imageUrl || 'https://via.placeholder.com/300'}
                                    />
                                    <Card.Body>
                                        <Card.Title>{product.goodsName}</Card.Title>
                                        <Card.Text className="text-primary fw-bold">
                                            ${product.price?.toLocaleString()}
                                        </Card.Text>
                                        <Link
                                            to={`/goods/${product.id}`}
                                            className="btn btn-sm btn-outline-primary"
                                        >
                                            View Details
                                        </Link>
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
