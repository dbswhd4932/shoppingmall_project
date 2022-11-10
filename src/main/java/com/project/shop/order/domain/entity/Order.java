package com.project.shop.order.domain.entity;

import com.project.shop.global.common.BaseTimeEntity;
import com.project.shop.member.domain.entity.Member;
import com.project.shop.order.domain.request.CreateOrderRequestDto;
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
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "order_id")
    private Long id;            //주문번호(PK)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;      //회원(다대일)

    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems = new ArrayList<>();

    @Column(nullable = false, length = 20)
    private String name;        //수취인 이름

    @Column(nullable = false, length = 20)
    private String phone;       //수취인 전화번호

    @Embedded
    private Address address;     //수취인 상세주소

    private String requirement; //요청사항
    private int totalPrice;     //결제금액

    @Builder
    public Order(Member member, List<OrderItem> orderItems, String name, String phone, Address address, String requirement, int totalPrice) {
        this.id = member.getId();
        this.member = member;
        this.orderItems = orderItems;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.requirement = requirement;
        this.totalPrice = totalPrice;
    }
    public static Order createOrder(CreateOrderRequestDto createOrderRequestDto) {
        Order order = Order.builder()
                .member(createOrderRequestDto.getMember())
                .name(createOrderRequestDto.getName())
                .phone(createOrderRequestDto.getPhone())
                .address(createOrderRequestDto.getAddress())
                .requirement(createOrderRequestDto.getRequirement())
                .totalPrice(createOrderRequestDto.getTotalPrice())
                .build();

        return order;
    }


}
