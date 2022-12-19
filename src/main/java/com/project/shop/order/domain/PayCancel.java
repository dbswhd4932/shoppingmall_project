package com.project.shop.order.domain;

import com.project.shop.global.common.BaseTimeEntity;
import com.project.shop.goods.domain.Goods;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "pay_cancel")
@Entity
public class PayCancel extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_cancel_id")
    private Long id;             //주문취소번호(PK)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;         //주문(다대일)

    @Column(nullable = false)
    private String merchantId;   // 가맹점ID

    @Column(nullable = false)
    private String cancelReason; // 주문 취소 사유

    @Column(nullable = false)
    private int cancelPrice;     //주문가격

    @Column(nullable = false)
    private String cardCompany;     // 카드사

    @Column(nullable = false)
    private String cardNumber;      // 카드일련번호

    @Builder
    public PayCancel(Order order, String merchantId, String cancelReason, int cancelPrice, String cardCompany, String cardNumber) {
        this.order = order;
        this.merchantId = merchantId;
        this.cancelReason = cancelReason;
        this.cancelPrice = cancelPrice;
        this.cardCompany = cardCompany;
        this.cardNumber = cardNumber;
    }
}
