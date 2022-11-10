package com.project.shop.order.domain.entity;

import com.project.shop.global.common.BaseTimeEntity;
import com.project.shop.member.domain.entity.Member;
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

    @Column(nullable = false)
    private String zipcode;         //우편번호

    @Column(nullable = false)
    private String detailAddress;   //상세주소

    private String requirement; //요청사항

    private int totalPrice;     //결제금액



}
