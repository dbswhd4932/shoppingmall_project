package com.project.shop.member.repository;

import com.project.shop.member.domain.Cart;
import com.project.shop.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CartRepository  extends JpaRepository<Cart, Long> {

    Optional<List<Cart>> findByMemberId(Long memberId);
    Optional<Cart> findByGoodsId(Long goodsId);
    Optional<Cart> findByIdAndGoodsIdAndMemberId(Long cartId, Long goodsId , Long memberId);

    Optional<Cart> findByGoodsIdAndMember(Long goodsId, Member member);

}
