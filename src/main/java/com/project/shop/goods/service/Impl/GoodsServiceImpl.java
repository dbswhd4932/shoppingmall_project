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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GoodsServiceImpl implements GoodsService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final S3Service s3Service;
    private final GoodsRepository goodsRepository;
    private final ImageRepository imageRepository;
    private final OptionRepository optionRepository;

    // 상품 등록 + 이미지 추가(필수) + 옵션 추가
    @Override
    public void goodsCreate(GoodsCreateRequest goodsCreateRequest, List<String> imgPaths) {

        if (goodsRepository.findByGoodsName(goodsCreateRequest.getGoodsName()).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATE_GOODS);
        }

        // 상품 정보저장
        Goods goods = Goods.toGoods(goodsCreateRequest);
        goodsRepository.save(goods);

        // 옵션 정보 저장
        List<OptionCreateRequest> optionCreateRequest = goodsCreateRequest.getOptionCreateRequest();
        for (OptionCreateRequest createRequest : optionCreateRequest) {
            Option option = Option.toOption(createRequest, goods);
            optionRepository.save(option);
        }

        // 이미지 정보 저장
        List<String> imgList = new ArrayList<>();
        for (String imgUrl : imgPaths) {
            Image image = Image.builder().fileUrl(imgUrl).goods(goods).build();
            imageRepository.save(image);
            imgList.add(image.getFileUrl());

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
    // s3 이미지 삭제 + s3 이미지 생성
    // 옵션 삭제 + 옵션 재생성
    @Override
    public void goodsEdit(Long goodsId, Long memberId, GoodsEditRequest goodsEditRequest, List<String> imgPaths) {
        Goods goods = goodsRepository.findByIdAndMemberId(goodsId, memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_SELLING_GOODS));

        goods.update(goodsEditRequest);

        // 기존 옵션 삭제
        List<Option> options = optionRepository.findAllByGoodsId(goodsId);
        for (Option option : options) {
            optionRepository.deleteById(option.getId());
        }

        // 상품 옵션 수정이 null 이 아니면 저장
        if (goodsEditRequest.getOptionCreateRequest() != null) {
            List<OptionCreateRequest> optionCreateRequest = goodsEditRequest.getOptionCreateRequest();
            for (OptionCreateRequest createRequest : optionCreateRequest) {
                Option option = Option.toOption(createRequest, goods);
                optionRepository.save(option);
            }
        }

        // s3 이미지 삭제
        List<Image> imageList = imageRepository.findByGoodsId(goods.getId());
        for (Image image : imageList) {
            String fileName = image.getFileUrl().substring(bucket.length() + 41);
            s3Service.deleteFile(fileName);
        }

        List<Image> images = imageRepository.findByGoodsId(goodsId);
        for (Image image : images) {
            imageRepository.deleteById(image.getId());
        }

        // 이미지 정보 저장
        List<String> imgList = new ArrayList<>();
        for (String imgUrl : imgPaths) {
            Image image = Image.builder().fileUrl(imgUrl).goods(goods).build();
            imageRepository.save(image);
            imgList.add(image.getFileUrl());

        }
    }

    // 상품 삭제
    @Override
    public void goodsDelete(Long goodsId, Long memberId) {
        Goods goods = goodsRepository.findByIdAndMemberId(goodsId, memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_SELLING_GOODS));

        // s3 이미지 삭제
        List<Image> imageList = imageRepository.findByGoodsId(goods.getId());
        for (Image image : imageList) {
            String fileName = image.getFileUrl().substring(bucket.length() + 41);
            s3Service.deleteFile(fileName);
        }

        goodsRepository.deleteById(goods.getId());
    }
}
