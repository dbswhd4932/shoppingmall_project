package com.project.shop.order.domain;

import com.project.shop.global.common.BaseTimeEntity;
import com.project.shop.member.domain.Cart;
import com.project.shop.order.controller.request.OrderCreateRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    private String zipcode;         //우편번호

    @Column(nullable = false)
    private String detailAddress;   //상세주소

    private String requirement;     //요청사항

    @Column(nullable = false)
    private int totalPrice;        //결제금액

    @Enumerated(EnumType.STRING)
    private OrderStatus status;    //주문상태

    private String impUid;         // 아임포트 발급 ID ex)imp_727855699150
    private String merchantId;     // 가맹점 ID        ex)ORD20180131-0000014


    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems = new ArrayList<>();

    @Builder
    public Order(Long memberId, String name, String phone, String zipcode, String detailAddress, String requirement, int totalPrice, String impUid, String merchantId) {
        this.memberId = memberId;
        this.name = name;
        this.phone = phone;
        this.zipcode = zipcode;
        this.detailAddress = detailAddress;
        this.requirement = requirement;
        this.totalPrice = totalPrice;
        this.status = OrderStatus.COMPLETE;
        this.impUid = impUid;
        this.merchantId = merchantId;
    }

    // 주문 생성
    public static Order toOrder(OrderCreateRequest orderCreateRequest, int payTotalPrice) {
        return Order.builder()
                .memberId(orderCreateRequest.getMemberId())
                .name(orderCreateRequest.getName())
                .phone(orderCreateRequest.getPhone())
                .zipcode(orderCreateRequest.getZipcode())
                .detailAddress(orderCreateRequest.getDetailAddress())
                .requirement(orderCreateRequest.getRequirement())
                .totalPrice(payTotalPrice)
                .impUid(orderCreateRequest.getImpUid())
                .merchantId(orderCreateRequest.getMerchantId())
                .build();
    }
}
