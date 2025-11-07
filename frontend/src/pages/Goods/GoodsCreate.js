import React, { useState, useEffect } from 'react';
import { Container, Form, Button, Alert, Spinner, Card, Row, Col } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import api from '../../api/axios';

const GoodsCreate = () => {
    const navigate = useNavigate();
    const [loading, setLoading] = useState(false);
    const [categories, setCategories] = useState([]);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const [formData, setFormData] = useState({
        goodsName: '',
        categoryId: '',
        price: '',
        goodsDescription: ''
    });

    const [images, setImages] = useState([]);
    const [imagePreviews, setImagePreviews] = useState([]);

    useEffect(() => {
        loadCategories();
    }, []);

    const loadCategories = async () => {
        try {
            const response = await api.get('/categories');
            setCategories(response.data || []);
        } catch (error) {
            console.error('Failed to load categories:', error);
            setError('카테고리를 불러오는데 실패했습니다.');
        }
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({
            ...formData,
            [name]: value
        });
    };

    const handleImageChange = (e) => {
        const files = Array.from(e.target.files);
        setImages(files);

        // 이미지 미리보기
        const previews = files.map(file => URL.createObjectURL(file));
        setImagePreviews(previews);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');

        // 유효성 검사
        if (!formData.goodsName || formData.goodsName.length < 2 || formData.goodsName.length > 50) {
            setError('상품명은 2~50글자 사이로 입력해주세요.');
            return;
        }

        if (!formData.categoryId) {
            setError('카테고리를 선택해주세요.');
            return;
        }

        if (!formData.price || formData.price < 1000) {
            setError('가격은 1000원 이상이어야 합니다.');
            return;
        }

        if (images.length === 0) {
            setError('최소 1개 이상의 이미지를 업로드해주세요.');
            return;
        }

        setLoading(true);

        try {
            // FormData 생성
            const submitData = new FormData();

            // GoodsCreateRequest를 JSON으로 추가
            const goodsCreateRequest = {
                goodsName: formData.goodsName,
                categoryId: parseInt(formData.categoryId),
                price: parseInt(formData.price),
                goodsDescription: formData.goodsDescription || null,
                optionCreateRequest: null
            };

            submitData.append('goodsCreateRequest', new Blob([JSON.stringify(goodsCreateRequest)], {
                type: 'application/json'
            }));

            // 이미지 파일들 추가
            images.forEach(image => {
                submitData.append('multipartFiles', image);
            });

            await api.post('/goods', submitData, {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            });

            setSuccess('상품이 성공적으로 등록되었습니다!');
            setTimeout(() => {
                navigate('/goods');
            }, 1500);

        } catch (error) {
            console.error('Failed to create goods:', error);
            if (error.response && error.response.data) {
                setError(error.response.data.errorMessage || '상품 등록에 실패했습니다.');
            } else {
                setError('상품 등록에 실패했습니다.');
            }
        } finally {
            setLoading(false);
        }
    };

    return (
        <Container className="my-5">
            <Card>
                <Card.Header>
                    <h2>상품 등록</h2>
                </Card.Header>
                <Card.Body>
                    {error && <Alert variant="danger">{error}</Alert>}
                    {success && <Alert variant="success">{success}</Alert>}

                    <Form onSubmit={handleSubmit}>
                        {/* 상품명 */}
                        <Form.Group className="mb-3">
                            <Form.Label>상품명 <span className="text-danger">*</span></Form.Label>
                            <Form.Control
                                type="text"
                                name="goodsName"
                                value={formData.goodsName}
                                onChange={handleInputChange}
                                placeholder="상품명을 입력하세요 (2-50자)"
                                required
                            />
                            <Form.Text className="text-muted">
                                2~50글자 사이로 입력해주세요.
                            </Form.Text>
                        </Form.Group>

                        {/* 카테고리 */}
                        <Form.Group className="mb-3">
                            <Form.Label>카테고리 <span className="text-danger">*</span></Form.Label>
                            <Form.Select
                                name="categoryId"
                                value={formData.categoryId}
                                onChange={handleInputChange}
                                required
                            >
                                <option value="">카테고리를 선택하세요</option>
                                {categories.map(category => (
                                    <option key={category.categoryId} value={category.categoryId}>
                                        {category.category}
                                    </option>
                                ))}
                            </Form.Select>
                        </Form.Group>

                        {/* 가격 */}
                        <Form.Group className="mb-3">
                            <Form.Label>가격 <span className="text-danger">*</span></Form.Label>
                            <Form.Control
                                type="number"
                                name="price"
                                value={formData.price}
                                onChange={handleInputChange}
                                placeholder="가격을 입력하세요 (최소 1000원)"
                                min="1000"
                                required
                            />
                            <Form.Text className="text-muted">
                                가격은 1000원 이상이어야 합니다.
                            </Form.Text>
                        </Form.Group>

                        {/* 상품 설명 */}
                        <Form.Group className="mb-3">
                            <Form.Label>상품 설명</Form.Label>
                            <Form.Control
                                as="textarea"
                                rows={5}
                                name="goodsDescription"
                                value={formData.goodsDescription}
                                onChange={handleInputChange}
                                placeholder="상품 설명을 입력하세요 (선택사항)"
                            />
                        </Form.Group>

                        {/* 이미지 업로드 */}
                        <Form.Group className="mb-3">
                            <Form.Label>상품 이미지 <span className="text-danger">*</span></Form.Label>
                            <Form.Control
                                type="file"
                                multiple
                                accept="image/*"
                                onChange={handleImageChange}
                                required
                            />
                            <Form.Text className="text-muted">
                                최소 1개 이상의 이미지를 업로드해주세요.
                            </Form.Text>
                        </Form.Group>

                        {/* 이미지 미리보기 */}
                        {imagePreviews.length > 0 && (
                            <div className="mb-3">
                                <h5>이미지 미리보기</h5>
                                <Row>
                                    {imagePreviews.map((preview, index) => (
                                        <Col key={index} md={3} className="mb-3">
                                            <img
                                                src={preview}
                                                alt={`Preview ${index + 1}`}
                                                className="img-fluid rounded"
                                                style={{ maxHeight: '200px', objectFit: 'cover' }}
                                            />
                                        </Col>
                                    ))}
                                </Row>
                            </div>
                        )}

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
                                    '상품 등록'
                                )}
                            </Button>
                            <Button
                                variant="secondary"
                                onClick={() => navigate('/goods')}
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

export default GoodsCreate;
