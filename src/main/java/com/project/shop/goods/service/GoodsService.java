package com.project.shop.goods.service;

import com.project.shop.goods.domain.dto.CreateGoodsDto;
import com.project.shop.goods.domain.enetity.Goods;
import com.project.shop.goods.domain.enetity.ItemImage;
import com.project.shop.goods.domain.enetity.dto.CreateGoodsRequestDto;
import com.project.shop.goods.repository.GoodsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class GoodsService {

    private final GoodsRepository goodsRepository;

    /**
     *  상품 생성
     */
    public void create(CreateGoodsRequestDto requestDto){

    }

    /**
     *  상품 전체 조회
     */

    /**
     *  상품 단건 조회
     */


}
