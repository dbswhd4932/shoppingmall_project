package com.project.shop.goods.service.Impl;

import com.project.shop.global.error.ErrorCode;
import com.project.shop.global.error.exception.BusinessException;
import com.project.shop.goods.controller.request.OptionCreateRequest;
import com.project.shop.goods.domain.Goods;
import com.project.shop.goods.domain.Image;
import com.project.shop.goods.controller.request.GoodsCreateRequest;
import com.project.shop.goods.controller.request.GoodsEditRequest;
import com.project.shop.goods.controller.response.GoodsResponse;
import com.project.shop.goods.domain.Option;
import com.project.shop.goods.repository.GoodsRepository;
import com.project.shop.goods.repository.ImageRepository;
import com.project.shop.goods.repository.OptionRepository;
import com.project.shop.goods.service.GoodsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class GoodsServiceImpl implements GoodsService {

    private final S3Upload s3Upload;
    private final GoodsRepository goodsRepository;
    private final ImageRepository imageRepository;
    private final OptionRepository optionRepository;

    // 상품 등록 + 이미지 추가 + 옵션 추가
    @Override
    public void goodsCreate(GoodsCreateRequest goodsCreateRequest, List<MultipartFile> files) throws IOException {
        // 저장할 경로 지정
//        String url = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files";

        if (goodsRepository.findByGoodsName(goodsCreateRequest.getGoodsName()).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATE_GOODS);
        }

        // 상품 정보저장
        Goods goods = Goods.toGoods(goodsCreateRequest);
        goodsRepository.save(goods);

        // 옵션 정보저장
//        for (OptionCreateRequest optionCreateRequest : goodsCreateRequest.getOptionCreateRequest()) {
//            Option option = Option.toOption(optionCreateRequest, goods);
//            optionRepository.save(option);
//        }

        OptionCreateRequest optionCreateRequest = goodsCreateRequest.getOptionCreateRequest();
        Option option = Option.toOption(optionCreateRequest, goods);
        optionRepository.save(option);

        // 이미지 정보 저장
        if (!files.isEmpty()) {
            for (MultipartFile file : files) {
                String storedFileName = s3Upload.upload(file, "images");
                goods.setImages(storedFileName);
                Image image = Image.builder().fileUrl(file.getOriginalFilename())
                        .goods(goods)
                        .build();
                imageRepository.save(image);
            }
        }
    }

    // 상품 전체 검색
    @Override
    @Transactional(readOnly = true)
    public List<GoodsResponse> goodsFindAll(Pageable pageable) {
        Page<Goods> goods = goodsRepository.findAll(pageable);
        List<GoodsResponse> list = new ArrayList<>();
        for (Goods good : goods) {
            list.add(GoodsResponse.toGoodsResponse(good));
        }
        return list;
    }

    // 상품 검색 ( 키워드 )
    @Override
    @Transactional(readOnly = true)
    public List<GoodsResponse> goodsFindKeyword(Pageable pageable, String keyword) {
        // keyword 로 검색 후 모든 상품 찾기
        List<Goods> goods = goodsRepository.findGoodsByGoodsNameContaining(pageable, keyword);

        List<GoodsResponse> list = new ArrayList<>();
        // 상품의 이미지 찾아서 응답에 추가 설정
        for (Goods good : goods) {
            List<Image> imageList = imageRepository.findByGoodsId(good.getId());
            GoodsResponse goodsResponse = GoodsResponse.toGoodsResponse(good);
            goodsResponse.setImageList(imageList);
            list.add(goodsResponse);
        }
        return list;
    }

    // 상품 수정
    // todo 이미지 수정 구현필요
    @Override
    public void goodsEdit(Long goodsId, Long memberId, GoodsEditRequest goodsEditRequest) {
        Goods goods = goodsRepository.findByIdAndMemberId(goodsId, memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_SELLING_GOODS));

        goods.update(goodsEditRequest);
    }

    // 상품 삭제
    @Override
    public void goodsDelete(Long goodsId, Long memberId) {
        Goods goods = goodsRepository.findByIdAndMemberId(goodsId, memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_SELLING_GOODS));

        goodsRepository.deleteById(goods.getId());
    }
}
