package com.project.shop.order.domain.entity;

import com.project.shop.global.common.BaseTimeEntity;
import com.project.shop.goods.domain.enetity.Goods;
import com.project.shop.member.domain.entity.Cart;
import com.project.shop.order.domain.entity.request.OrderCreateRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    private int totalPrice;         //결제금액

    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems = new ArrayList<>();

    @Builder
    public Order(Long memberId, String name, String phone, String zipcode, String detailAddress, String requirement, int totalPrice) {
        this.memberId = memberId;
        this.name = name;
        this.phone = phone;
        this.zipcode = zipcode;
        this.detailAddress = detailAddress;
        this.requirement = requirement;
        this.totalPrice = totalPrice;
    }


    public static Order toOrder(OrderCreateRequest orderCreateRequest, Cart cart) {
        return Order.builder()
                .name(orderCreateRequest.getName())
                .memberId(orderCreateRequest.getMemberId())
                .phone(orderCreateRequest.getPhone())
                .zipcode(orderCreateRequest.getZipcode())
                .detailAddress(orderCreateRequest.getDetailAddress())
                .requirement(orderCreateRequest.getRequirement())
                .totalPrice(cart.getTotalPrice())
                .build();
    }

    // 주문에 같은 회원이 있을 경우 가격 증가
    public void addTotalPrice(int addPrice) {
        this.totalPrice += addPrice;
    }
}
