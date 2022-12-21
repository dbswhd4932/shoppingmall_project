package com.project.shop.member.repository;

import com.project.shop.member.domain.Cart;
import com.project.shop.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository  extends JpaRepository<Cart, Long> {

    Optional<List<Cart>> findByMemberId(Long memberId);

    Optional<Cart> findByIdAndMember(Long cartId, Member member);

    Optional<Cart> findByGoodsIdAndMember(Long goodsId, Member member);

}
