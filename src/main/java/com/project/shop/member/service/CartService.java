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
     * todo 작성 중  CartItem 같은 중간테이블이 있어야 할 것 같음.
     */
    public void create(CartCreateDto cartCreateDto, Member member) {
        Goods goods = goodsRepository.findById(cartCreateDto.getGoods().getId()).orElseThrow(
                () -> new BusinessException(NOT_FOUND_GOODS));

        CartCreateDto createDto = CartCreateDto.builder()
                .member(member)
                .goods(goods)
                .quantity(1) // 선택한 개수만큼 추가를 어떻게 ??..
                .build();

        cartRepository.save(new Cart(cartCreateDto));

    }

}
