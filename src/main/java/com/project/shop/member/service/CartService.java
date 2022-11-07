package com.project.shop.member.service;

import com.project.shop.global.error.exception.BusinessException;
import com.project.shop.goods.domain.enetity.Goods;
import com.project.shop.goods.repository.GoodsRepository;
import com.project.shop.member.domain.entity.Cart;
import com.project.shop.member.domain.entity.Member;
import com.project.shop.member.domain.request.CartCreateDto;
import com.project.shop.member.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.project.shop.global.error.ErrorCode.NOT_FOUND_GOODS;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final GoodsRepository goodsRepository;

    /**
     * 장바구니 담기 - 장바구니는 상품이 등록되면 자동 생성되며, 하나도 없을 경우에는 자동 삭제된다.
     */
    public void create(CartCreateDto cartCreateDto, Member member) {
        Goods goods = goodsRepository.findById(cartCreateDto.getGoodsId()).orElseThrow(
                () -> new BusinessException(NOT_FOUND_GOODS));

        if (!cartRepository.findCartByMember(member).isEmpty()) {
            CartCreateDto.builder()
                    .memberId(member.getId())
                    .goodsId(goods.getId())
                    .quantity(1); // 선택한 개수만큼 추가를 어떻게 ??..
        }

    }

}
