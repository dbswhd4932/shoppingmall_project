package com.project.shop.order.service.impl;

import com.project.shop.global.error.ErrorCode;
import com.project.shop.global.error.exception.BusinessException;
import com.project.shop.goods.domain.Goods;
import com.project.shop.goods.repository.GoodsRepository;
import com.project.shop.member.domain.Cart;
import com.project.shop.member.domain.Member;
import com.project.shop.member.repository.CartRepository;
import com.project.shop.member.repository.MemberRepository;
import com.project.shop.member.service.RedisCartService;
import com.project.shop.order.controller.request.OrderCreateRequest;
import com.project.shop.order.controller.request.PayCancelRequest;
import com.project.shop.order.controller.response.OrderPageResponse;
import com.project.shop.order.controller.response.OrderResponse;
import com.project.shop.order.domain.*;
import com.project.shop.order.event.OrderCreatedEvent;
import com.project.shop.order.publisher.OrderEventPublisher;
import com.project.shop.order.repository.OrderItemRepository;
import com.project.shop.order.repository.OrderRepository;
import com.project.shop.order.repository.PayCancelRepository;
import com.project.shop.order.repository.PayRepository;
import com.project.shop.order.service.OrderService;
import com.project.shop.payment.domain.Payment;
import com.project.shop.payment.domain.PaymentStatus;
import com.project.shop.payment.dto.PaymentConfirmRequest;
import com.project.shop.payment.dto.PaymentRequest;
import com.project.shop.payment.dto.PaymentResponse;
import com.project.shop.payment.repository.PaymentRepository;
import com.project.shop.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.project.shop.global.error.ErrorCode.*;

@Slf4j
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final GoodsRepository goodsRepository;
    private final PayRepository payRepository;
    private final OrderItemRepository orderItemRepository;
    private final MemberRepository memberRepository;
    private final PayCancelRepository payCancelRepository;
    private final RedisCartService redisCartService;
    private final OrderEventPublisher orderEventPublisher;
    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;

    // 생성자에서 @Qualifier로 TossPaymentService 주입
    public OrderServiceImpl(
            OrderRepository orderRepository,
            CartRepository cartRepository,
            GoodsRepository goodsRepository,
            PayRepository payRepository,
            OrderItemRepository orderItemRepository,
            MemberRepository memberRepository,
            PayCancelRepository payCancelRepository,
            RedisCartService redisCartService,
            OrderEventPublisher orderEventPublisher,
            @Qualifier("tossPaymentService") PaymentService paymentService,
            PaymentRepository paymentRepository
    ) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.goodsRepository = goodsRepository;
        this.payRepository = payRepository;
        this.orderItemRepository = orderItemRepository;
        this.memberRepository = memberRepository;
        this.payCancelRepository = payCancelRepository;
        this.redisCartService = redisCartService;
        this.orderEventPublisher = orderEventPublisher;
        this.paymentService = paymentService;
        this.paymentRepository = paymentRepository;
    }

    // 주문번호 생성 (ORDER-20251109-A1B2C3 형식)
    private String generateOrderNumber() {
        // 현재 날짜 (yyyyMMdd 형식)
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // 랜덤 6자리 영문+숫자
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder randomCode = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            randomCode.append(chars.charAt(random.nextInt(chars.length())));
        }

        return "ORDER-" + dateStr + "-" + randomCode.toString();
    }

    // 주문 생성 (결제 예시 버전)
    @Override
    public void cartOrder(OrderCreateRequest orderCreateRequest) {

        Member member = getMember();

        // 주문번호 생성
        String orderNumber = generateOrderNumber();

        // 1. 결제 준비 요청 (TossPayments)
        PaymentRequest paymentRequest = new PaymentRequest(
                orderCreateRequest.getMerchantId(),
                orderCreateRequest.getTotalPrice(),
                orderNumber + " 외 " + orderCreateRequest.getOrderItemCreates().size() + "건", // 주문명
                member.getEmail(),
                member.getName(),
                "CARD" // 기본 결제수단, 프론트엔드에서 선택 가능하도록 개선 필요
        );

        PaymentResponse paymentResponse = paymentService.requestPayment(paymentRequest);
        log.info("결제 준비 완료 - merchantId: {}, orderId: {}, amount: {}, status: {}",
                paymentResponse.getMerchantId(),
                paymentResponse.getOrderId(),
                paymentResponse.getAmount(),
                paymentResponse.getStatus());

        // ========== 결제 승인 로직 (주석 처리) ==========
        // 실제 운영 환경에서는 프론트엔드에서 TossPayments SDK로 결제 후
        // PaymentController의 confirmPayment API를 호출해야 함
        // 아래는 테스트용 자동 승인 예시 코드
        /*
        try {
            // TossPayments에서 결제 완료 후 받는 paymentKey를 가정
            // 실제로는 프론트엔드에서 TossPayments SDK 응답으로 받음
            String mockPaymentKey = "test_payment_key_" + System.currentTimeMillis();

            PaymentConfirmRequest confirmRequest = new PaymentConfirmRequest(
                    mockPaymentKey,                          // TossPayments에서 발급한 결제 키
                    paymentResponse.getOrderId(),            // 주문 ID
                    paymentResponse.getAmount()              // 결제 금액
            );

            // 결제 최종 승인 요청
            PaymentResponse confirmedResponse = paymentService.confirmPayment(confirmRequest);
            log.info("결제 승인 완료 - paymentKey: {}, status: {}, approvedAt: {}",
                    confirmedResponse.getPaymentKey(),
                    confirmedResponse.getStatus(),
                    confirmedResponse.getApprovedAt());

        } catch (Exception e) {
            log.error("결제 승인 실패 - merchantId: {}, error: {}",
                    paymentResponse.getMerchantId(), e.getMessage());
            // 결제 승인 실패 시 주문도 취소 처리해야 함
            throw new BusinessException(ErrorCode.PAYMENT_FAILED);
        }
        */
        // ========== 결제 승인 로직 끝 ==========

        // 2. Order 생성
        Order order = Order.builder()
                .memberId(member.getId())
                .name(orderCreateRequest.getName())
                .phone(orderCreateRequest.getPhone())
                .zipcode(orderCreateRequest.getZipcode())
                .detailAddress(orderCreateRequest.getDetailAddress())
                .requirement(orderCreateRequest.getRequirement())
                .totalPrice(orderCreateRequest.getTotalPrice())
                .impUid(orderCreateRequest.getImpUid())
                .merchantId(orderCreateRequest.getMerchantId())
                .orderNumber(orderNumber)
                .build();

        // 3. 주문_상품 DB 저장
        for (OrderCreateRequest.orderItemCreate orderItemCreate : orderCreateRequest.getOrderItemCreates()) {
            Goods goods = goodsRepository.findById(orderItemCreate.getGoodsId()).orElseThrow(
                    () -> new BusinessException(NOT_FOUND_GOODS));
            OrderItem orderItem =
                    OrderItem.createOrderItem(member, goods.getId(), orderItemCreate.getOrderPrice(), orderItemCreate.getAmount(),
                            order, goods.getGoodsName(), orderItemCreate.getOrderPrice()/orderItemCreate.getAmount());
            orderItemRepository.save(orderItem);

            // Redis 장바구니에서 주문된 상품 삭제 (있는 경우만)
            Long optionNumber = orderItemCreate.getOptionNumber();
            try {
                redisCartService.removeFromCart(goods.getId(), optionNumber);
            } catch (BusinessException e) {
                // 장바구니에 없는 경우 무시 (직접 주문 시)
                if (!e.getErrorCode().equals(NOT_FOUND_CART)) {
                    throw e;
                }
            }
        }

        // 4. 주문 DB 저장
        orderRepository.save(order);

        // 5. Payment 엔티티와 Order 연결 (선택 사항, 추후 확인용)
        // Payment는 이미 requestPayment()에서 생성되었으므로 별도 처리 불필요

        // 6. 기존 Pay 엔티티도 저장 (하위 호환성)
        Pay pay = Pay.builder()
                .cardCompany(orderCreateRequest.getCardCompany())
                .cardNumber(orderCreateRequest.getCardNumber())
                .order(order)
                .payPrice(order.getTotalPrice())
                .build();
        payRepository.save(pay);

        // 7. RabbitMQ 이벤트 발행 - 주문 생성 알림
        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(order.getId())
                .merchantId(order.getMerchantId())
                .memberId(member.getId())
                .memberLoginId(member.getLoginId())
                .memberEmail(member.getEmail())
                .totalPrice(order.getTotalPrice())
                .orderStatus(order.getOrderStatus().name())
                .createdAt(order.getCratedAt())
                .build();

        orderEventPublisher.publishOrderCreated(event);
    }

    // 주문 조회
    @Override
    @Transactional(readOnly = true)
    public List<OrderPageResponse> orderFindMember(Pageable pageable) {
        Member member = getMember();
        Page<Order> orderList = orderRepository.findAll(pageable);

        return orderList.stream()
                .filter(findOrder -> findOrder.getMemberId().equals(member.getId()))
                .map(order -> {
                    // 주문에 속한 상품 ID 목록 조회
                    List<OrderItem> orderItems = orderItemRepository.findAllByOrder(order);
                    List<Long> goodsIds = orderItems.stream()
                            .map(OrderItem::getGoodsId)
                            .toList();

                    // OrderPageResponse 생성 후 goodsId 설정
                    OrderPageResponse response = OrderPageResponse.toResponse(order, orderList);
                    return OrderPageResponse.builder()
                            .orderId(response.getOrderId())
                            .memberId(response.getMemberId())
                            .name(response.getName())
                            .phone(response.getPhone())
                            .zipcode(response.getZipcode())
                            .detailAddress(response.getDetailAddress())
                            .requirement(response.getRequirement())
                            .merchantId(response.getMerchantId())
                            .orderNumber(response.getOrderNumber())
                            .totalPrice(response.getTotalPrice())
                            .orderStatus(response.getOrderStatus())
                            .orderTime(response.getOrderTime())
                            .totalPage(response.getTotalPage())
                            .totalCount(response.getTotalCount())
                            .pageNumber(response.getPageNumber())
                            .currentPageSize(response.getCurrentPageSize())
                            .goodsId(goodsIds)
                            .build();
                })
                .toList();
    }

    // 주문 단건 조회
    @Override
    public OrderResponse orderFindOne(Long orderId) {
        Member member = getMember();
        Order order = orderRepository.findByIdAndMemberId(orderId, member.getId()).orElseThrow(
                () -> new BusinessException(NOT_FOUND_ORDERS));

        OrderResponse orderResponse = OrderResponse.toResponse(order);
        List<OrderItem> orderItems = orderItemRepository.findAllByOrder(order);
        List<Long> list = new ArrayList<>();

        for (OrderItem orderItem : orderItems) {
            list.add(orderItem.getGoodsId());
        }
        orderResponse.setGoodsId(list);
        return orderResponse;
    }

    // 결제취소
    @Override
    public void payCancel(PayCancelRequest payCancelRequest) {
        Member member = getMember();

        Order order = orderRepository.findByMerchantId(payCancelRequest.getMerchantId())
                .orElseThrow(() -> new BusinessException(NOT_EQUAL_MERCHANT_ID));


        Pay pay = payRepository.findByOrderId(order.getId()).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND_PAY));

        // 결제 회원과 로그인한 회원이 다르면 예외처리
        if(!pay.getMemberId().equals(member.getId())) {
            throw new BusinessException(CAN_NOT_CANCEL_PAY);
        }

        // 이미 취소된 결제는 예외처리
        if (pay.getPayStatus().equals(PayStatus.CANCEL)) {
            throw new BusinessException(ALREADY_CANCEL_PAY);
        }

        PayCancel payCancel = PayCancel.builder()
                .order(pay.getOrder())
                .merchantId(payCancelRequest.getMerchantId())
                .cancelReason(payCancelRequest.getCancelReason())
                .cancelPrice(pay.getPayPrice())
                .cardCompany(pay.getCardCompany())
                .cardNumber(pay.getCardNumber())
                .build();

        // ORDER_ITEM DB 상품 삭제
        List<OrderItem> orderItems = orderItemRepository.findAllByOrder(pay.getOrder());
        orderItemRepository.deleteAll(orderItems);

        // 결제취소 DB 저장
        payCancelRepository.save(payCancel);
        // 결제 DB 상태 변경
        pay.PayStatusChangeCancel();
        // 주문 DB 상태 변경
        pay.getOrder().orderStatusChangeCancel();
    }

    private Member getMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName();

        return memberRepository.findByLoginId(loginId).orElseThrow(() -> new BusinessException(NOT_FOUND_MEMBER));
    }
}
