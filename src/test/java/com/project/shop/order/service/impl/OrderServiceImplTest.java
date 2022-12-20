package com.project.shop.order.service.impl;

import com.project.shop.goods.repository.GoodsRepository;
import com.project.shop.member.repository.CartRepository;
import com.project.shop.order.repository.OrderRepository;
import com.project.shop.order.repository.PayRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @InjectMocks
    OrderServiceImpl orderService;

    @Mock
    OrderRepository orderRepository;

    @Mock
    CartRepository cartRepository;

    @Mock
    PayRepository payRepository;

//    @Mock
//    CardRepository cardRepository;

//    @Mock
//    GoodsRepository goodsRepository;


}