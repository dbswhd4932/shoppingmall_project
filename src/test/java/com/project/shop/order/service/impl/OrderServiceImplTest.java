package com.project.shop.order.service.impl;

import com.project.shop.factory.CardFactory;
import com.project.shop.factory.CartFactory;
import com.project.shop.factory.GoodsFactory;
import com.project.shop.factory.MemberFactory;
import com.project.shop.goods.domain.Goods;
import com.project.shop.goods.repository.GoodsRepository;
import com.project.shop.member.domain.Card;
import com.project.shop.member.domain.Cart;
import com.project.shop.member.domain.Member;
import com.project.shop.member.repository.CardRepository;
import com.project.shop.member.repository.CartRepository;
import com.project.shop.order.controller.request.OrderCreateRequest;
import com.project.shop.order.domain.Order;
import com.project.shop.order.domain.OrderStatus;
import com.project.shop.order.repository.OrderRepository;
import com.project.shop.order.repository.PayRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
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

    @Mock
    CardRepository cardRepository;

    @Mock
    GoodsRepository goodsRepository;


}