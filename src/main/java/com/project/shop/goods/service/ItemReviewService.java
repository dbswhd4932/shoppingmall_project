package com.project.shop.goods.service;

import com.project.shop.goods.repository.GoodsRepository;
import com.project.shop.goods.repository.ItemReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemReviewService {

    private final ItemReviewRepository itemReviewRepository;
    private final GoodsRepository goodsRepository;

}
