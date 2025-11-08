import React, { useState } from 'react';
import { Container, Form, Button, Alert, Spinner, Card } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import api from '../../api/axios';

const CategoryCreate = () => {
    const navigate = useNavigate();
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const [formData, setFormData] = useState({
        category: ''
    });

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({
            ...formData,
            [name]: value
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');

        // 유효성 검사
        if (!formData.category || formData.category.trim().length === 0) {
            setError('카테고리명을 입력해주세요.');
            return;
        }

        if (formData.category.trim().length < 2) {
            setError('카테고리명은 최소 2글자 이상이어야 합니다.');
            return;
        }

        setLoading(true);

        try {
            await api.post('/categories', {
                category: formData.category.trim()
            });

            setSuccess('카테고리가 성공적으로 등록되었습니다!');
            setTimeout(() => {
                navigate('/categories');
            }, 1500);

        } catch (error) {
            console.error('Failed to create category:', error);
            if (error.response && error.response.data) {
                setError(error.response.data.errorMessage || '카테고리 등록에 실패했습니다.');
            } else {
                setError('카테고리 등록에 실패했습니다.');
            }
        } finally {
            setLoading(false);
        }
    };

    return (
        <Container className="my-5">
            <Card>
                <Card.Header>
                    <h2>카테고리 등록</h2>
                </Card.Header>
                <Card.Body>
                    {error && <Alert variant="danger">{error}</Alert>}
                    {success && <Alert variant="success">{success}</Alert>}

                    <Form onSubmit={handleSubmit}>
                        {/* 카테고리명 */}
                        <Form.Group className="mb-3">
                            <Form.Label>카테고리명 <span className="text-danger">*</span></Form.Label>
                            <Form.Control
                                type="text"
                                name="category"
                                value={formData.category}
                                onChange={handleInputChange}
                                placeholder="카테고리명을 입력하세요"
                                required
                            />
                            <Form.Text className="text-muted">
                                카테고리명은 최소 2글자 이상이어야 합니다.
                            </Form.Text>
                        </Form.Group>

                        {/* 버튼 */}
                        <div className="d-flex gap-2">
                            <Button
                                variant="primary"
                                type="submit"
                                disabled={loading}
                            >
                                {loading ? (
                                    <>
                                        <Spinner
                                            as="span"
                                            animation="border"
                                            size="sm"
                                            role="status"
                                            aria-hidden="true"
                                            className="me-2"
                                        />
                                        등록 중...
                                    </>
                                ) : (
                                    '카테고리 등록'
                                )}
                            </Button>
                            <Button
                                variant="secondary"
                                onClick={() => navigate(-1)}
                                disabled={loading}
                            >
                                취소
                            </Button>
                        </div>
                    </Form>
                </Card.Body>
            </Card>
        </Container>
    );
};

export default CategoryCreate;
