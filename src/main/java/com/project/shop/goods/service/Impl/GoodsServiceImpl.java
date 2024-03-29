package com.project.shop.goods.service.Impl;

import com.project.shop.global.aspect.TimerAop;
import com.project.shop.global.error.ErrorCode;
import com.project.shop.global.error.exception.BusinessException;
import com.project.shop.goods.controller.request.*;
import com.project.shop.goods.controller.response.GoodsResponse;
import com.project.shop.goods.controller.response.UpdateGoodsResponse;
import com.project.shop.goods.domain.Category;
import com.project.shop.goods.domain.Goods;
import com.project.shop.goods.domain.Image;
import com.project.shop.goods.domain.Options;
import com.project.shop.goods.repository.CategoryRepository;
import com.project.shop.goods.repository.GoodsRepository;
import com.project.shop.goods.repository.ImageRepository;
import com.project.shop.goods.repository.OptionRepository;
import com.project.shop.goods.service.GoodsService;
import com.project.shop.member.domain.Member;
import com.project.shop.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.project.shop.global.error.ErrorCode.*;

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
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;

    // 상품 등록 + 이미지 추가(필수) + 옵션 추가(필수X)
    @Override
    public void goodsCreate(GoodsCreateRequest goodsCreateRequest, List<MultipartFile> imgPaths) {

        Member member = getMember();

        // 같은 이름의 상품이 있으면 예외처리
        if (goodsRepository.findByGoodsName(goodsCreateRequest.getGoodsName()).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATE_GOODS);
        }

        Category category = categoryRepository.findById(goodsCreateRequest.getCategoryId()).orElseThrow(
                () -> new BusinessException(NOT_FOUND_CATEGORY));

        // 상품 정보저장
        Goods goods = Goods.create(goodsCreateRequest, category, member);
        goodsRepository.save(goods);

        // 옵션 정보 저장
        List<Options> optionsList = goodsCreateRequest.getOptionCreateRequest().stream().map(
                optionCreateRequest -> Options.toOption(optionCreateRequest, goods)).collect(Collectors.toList());

        for (Options options : optionsList) {
            options.setGoods(goods);
        }

        optionRepository.saveAll(optionsList);

        // S3 저장
        List<String> list = s3Service.upload(imgPaths);

        // 이미지 DB 저장
        List<Image> imageList = list.stream().map(img -> Image.builder().fileUrl(img).goods(goods).build()).collect(Collectors.toList());
        for (Image image : imageList) {
            image.setGoods(goods);
        }
        imageRepository.saveAll(imageList);

    }

    // 상품 전체 검색
    @Override
    @TimerAop
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "goods", key = "#pageable")
    public Page<GoodsResponse> goodsFindAll(Pageable pageable) {
        Page<Goods> goods = goodsRepository.findAll(pageable);

        Page<GoodsResponse> responses = goods.map(good -> new GoodsResponse(good));
        return responses;

    }

    // 상품 가격 변경 확인
    @Transactional(readOnly = true)
    public List<UpdateGoodsResponse> checkGoodsUpdate(List<UpdateCheckRequest> updateCheckRequest) {
        List<UpdateGoodsResponse> list = new ArrayList<>();
        for (UpdateCheckRequest request : updateCheckRequest) {
            Goods goods = goodsRepository.findById(request.getGoodsId()).orElseThrow(
                    () -> new BusinessException(NOT_FOUND_GOODS));

            // 옵션이 없는 상품이면
            if (goods.getOptions().isEmpty()) {
                // 상품 옵션 ID 를 입력하면 예외처리
                if (request.getOptionId() != null)
                    throw new BusinessException(NOT_FOUND_OPTION);
                UpdateGoodsResponse goodsResponse = UpdateGoodsResponse.builder()
                        .goodsId(request.getGoodsId()).goodsPrice(request.getGoodsPrice()).changeCheck(false).build();
                // DB 가격 != 입력한 가격 -> ChangeCheck true 로 변경
                if (goods.getPrice() != request.getGoodsPrice())
                    goodsResponse.setChangeCheck(true);
                list.add(goodsResponse);
                return list;
            }

            // 옵션이 있는 상품이면
            Options options = optionRepository.findByIdAndGoodsId(request.getOptionId(), request.getGoodsId()).orElseThrow(
                    () -> new BusinessException(NOT_FOUND_OPTION));
            UpdateGoodsResponse goodsResponse = UpdateGoodsResponse.builder()
                    .goodsId(request.getGoodsId()).goodsPrice(options.getTotalPrice()).changeCheck(false).build();
            // DB 옵션 가격 != 입력한 가격 -> ChangeCheck true 로 변경
            if (options.getTotalPrice() != request.getGoodsPrice())
                goodsResponse.setChangeCheck(true);
            list.add(goodsResponse);
        }
        return list;
    }

    // 상품 상세(정보)조회
    @Override
    @TimerAop
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "goods", key = "#goodsId")
    public GoodsResponse goodsDetailFind(Long goodsId) {
        Goods goods = goodsRepository.findById(goodsId).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND_GOODS));

        return new GoodsResponse(goods);
    }

    // 상품 검색 ( 키워드 )
    @Override
    @TimerAop
    @Transactional(readOnly = true)
    public Page<GoodsResponse> goodsFindKeyword(String keyword, Pageable pageable) {
        // keyword 로 검색 후 모든 상품 찾기
        Page<Goods> goods = goodsRepository.findGoodsByGoodsNameContaining(pageable, keyword);
        return goods.map(GoodsResponse::new);
    }

    // 상품 수정
    @Override
    public void goodsEdit(Long goodsId, GoodsEditRequest goodsEditRequest, List<MultipartFile> imgPaths) {

        Member member = getMember();

        // 수정할 상품이름이 이미 존재하면 예외처리
        if (goodsRepository.findByGoodsName(goodsEditRequest.getGoodsName()).isPresent())
            throw new BusinessException(ErrorCode.DUPLICATE_GOODS);

        Goods goods = goodsRepository.findById(goodsId).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_GOODS));
        // 수정할 상품의 회원과 로그인한 회원이 다르면 예외처리
        if (!goods.getMemberId().equals(member.getId()))
            throw new BusinessException(NOT_SELLING_GOODS);

        goods.update(goodsEditRequest);

        //기존 옵션 삭제
        List<Options> options = optionRepository.findByGoodsId(goodsId);
        optionRepository.deleteAll(options);

        // 수정할 상품의 옵션이 비어있거나 null 이 아니면 옵션 DB 에 저장
        if (!goodsEditRequest.getOptionCreateRequest().isEmpty() && goodsEditRequest.getGoodsDescription() != null) {
            List<OptionCreateRequest> optionCreateRequestList = goodsEditRequest.getOptionCreateRequest();

            List<Options> optionsList = optionCreateRequestList.stream().map(optionCreateRequest -> Options.toOption(optionCreateRequest, goods)).collect(Collectors.toList());
            for (Options option : optionsList) {
                option.setGoods(goods);
            }
            optionRepository.saveAll(optionsList);
        }

        // s3 , 이미지DB 삭제
        List<Image> imageList = imageRepository.findByGoodsId(goods.getId());
        for (Image image : imageList) {
            String fileName = image.getFileUrl();
            s3Service.deleteFile(fileName);
            imageRepository.deleteById(image.getId());
        }

        // S3 이미지 저장
        List<String> list = s3Service.upload(imgPaths);

        // 이미지 정보 저장
        List<Image> images = list.stream().map(img -> Image.builder().fileUrl(img).goods(goods).build()).collect(Collectors.toList());
        for (Image image : images) {
            image.setGoods(goods);
        }
        imageRepository.saveAll(images);

    }

    // 상품 삭제
    @Override
    public void goodsDelete(Long goodsId) {

        Member member = getMember();

        Goods goods = goodsRepository.findById(goodsId).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_GOODS));
        // 상품 회원과 로그인한 회원이 다르면 예외처리
        if (!goods.getMemberId().equals(member.getId()))
            throw new BusinessException(NOT_SELLING_GOODS);

        // s3 이미지 삭제
        List<Image> imageList = imageRepository.findByGoodsId(goods.getId());

        for (Image image : imageList) {
            String fileName = image.getFileUrl();
            s3Service.deleteFile(fileName);
        }
        goodsRepository.deleteById(goods.getId());
    }

    // 상품 가격으로 조회 (000원 이상 0000 이하)
    @Override
    public Page<GoodsResponse> searchBetweenPrice(GoodsSearchCondition condition, Pageable pageable) {
        GoodsSearchCondition result =
                new GoodsSearchCondition(condition.getPriceMin(), condition.getPriceMax(), condition.getCategory());

        return goodsRepository.searchBetweenPrice(result, pageable);

    }

    private Member getMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return memberRepository.findByLoginId(authentication.getName()).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));
    }
}
