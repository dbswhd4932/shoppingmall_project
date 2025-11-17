package com.project.shop.order.domain;

import com.project.shop.global.common.BaseTimeEntity;
import com.project.shop.member.domain.Member;
import com.project.shop.order.controller.request.OrderCreateRequest;
import com.project.shop.payment.domain.Payment;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;                //주문번호(PK)

    @Column(nullable = false)
    private Long memberId;          //회원번호

    @Column(nullable = false, length = 20)
    private String name;            //수취인 이름

    @Column(nullable = false, length = 20)
    private String phone;           //수취인 전화번호

    @Column(nullable = false)
    private String zipcode;         //수취인 우편번호

    @Column(nullable = false)
    private String detailAddress;   //수취인 상세주소

    @Column
    private String requirement;     //요청사항

    @Column(nullable = false)
    private int totalPrice;    //총 주문금액

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;//주문상태

    @Column(nullable = false)
    private String impUid;         // 아임포트 발급 ID ex)imp_727855699150

    @Column(nullable = false)
    private String merchantId;     // 주문번호 UUID    ex)550e8400-e29b-41d4-a716-446655440000

    @Column(nullable = false, unique = true)
    private String orderNumber;    // 주문번호 표시용  ex)ORDER-20251109-A1B2C3

    @OneToOne(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Payment payment;       // 결제 정보

    @Builder
    public Order(Long memberId, String name, String phone, String zipcode, String detailAddress, String requirement, int totalPrice, String impUid, String merchantId, String orderNumber) {
        this.memberId = memberId;
        this.name = name;
        this.phone = phone;
        this.zipcode = zipcode;
        this.detailAddress = detailAddress;
        this.requirement = requirement;
        this.totalPrice = totalPrice;
        this.orderStatus = OrderStatus.ORDER;  // 초기 상태는 주문완료
        this.impUid = impUid;
        this.merchantId = merchantId;
        this.orderNumber = orderNumber;
    }

    // 주문 생성
    public static Order toOrder(OrderCreateRequest orderCreateRequest, Member member) {

        int totalPrice = 0;
        for (OrderCreateRequest.orderItemCreate orderItemCreate : orderCreateRequest.getOrderItemCreates()) {
            totalPrice += orderItemCreate.getOrderPrice();
        }

        return Order.builder()
                .memberId(member.getId())
                .name(orderCreateRequest.getName())
                .phone(orderCreateRequest.getPhone())
                .zipcode(orderCreateRequest.getZipcode())
                .detailAddress(orderCreateRequest.getDetailAddress())
                .requirement(orderCreateRequest.getRequirement())
                .totalPrice(totalPrice)
                .impUid(orderCreateRequest.getImpUid())
                .merchantId(orderCreateRequest.getMerchantId())
                .build();
    }

    // 주문 상태 변경 - 취소
    public void orderStatusChangeCancel() {
        this.orderStatus = OrderStatus.CANCEL;
    }

    // 주문 상태 변경 - 배송중
    public void orderStatusChangeDelivery() {
        this.orderStatus = OrderStatus.DELIVERY;
    }

    // 주문 상태 변경 - 배송완료
    public void orderStatusChangeComplete() {
        this.orderStatus = OrderStatus.COMPLETE;
    }
}
