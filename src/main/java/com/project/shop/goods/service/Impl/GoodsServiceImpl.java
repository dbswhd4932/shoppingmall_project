package com.project.shop.goods.service.Impl;

import com.project.shop.global.error.ErrorCode;
import com.project.shop.global.error.exception.BusinessException;
import com.project.shop.goods.controller.request.GoodsCreateRequest;
import com.project.shop.goods.controller.request.GoodsEditRequest;
import com.project.shop.goods.controller.request.OptionCreateRequest;
import com.project.shop.goods.controller.request.UpdateCheckRequest;
import com.project.shop.goods.controller.response.GoodsResponse;
import com.project.shop.goods.controller.response.UpdateGoodsResponse;
import com.project.shop.goods.domain.Goods;
import com.project.shop.goods.domain.Image;
import com.project.shop.goods.domain.Option;
import com.project.shop.goods.repository.GoodsRepository;
import com.project.shop.goods.repository.ImageRepository;
import com.project.shop.goods.repository.OptionRepository;
import com.project.shop.goods.service.GoodsService;
import com.project.shop.member.domain.Member;
import com.project.shop.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.project.shop.global.error.ErrorCode.NOT_FOUND_GOODS;
import static com.project.shop.global.error.ErrorCode.NOT_SELLING_GOODS;

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

    // 상품 등록 + 이미지 추가(필수) + 옵션 추가(필수X)
    @Override
    public void goodsCreate(GoodsCreateRequest goodsCreateRequest, List<String> imgPaths) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Member member = memberRepository.findByLoginId(authentication.getName()).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));

        // 상품 정보저장
        Goods goods = Goods.create(goodsCreateRequest, member);
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

    // 상품 가격 변경 확인
    @Transactional(readOnly = true)
    public List<UpdateGoodsResponse> checkGoodsUpdate(List<UpdateCheckRequest> updateCheckRequest) {
        List<UpdateGoodsResponse> list = new ArrayList<>();

        for (UpdateCheckRequest request : updateCheckRequest) {
            Goods goods = goodsRepository.findById(request.getGoodsId()).orElseThrow(
                    () -> new BusinessException(NOT_FOUND_GOODS));
            // 상품 옵션이 없으면, 상품 가격으로 비교
            if (goods.getOptions().isEmpty()) {
                if (goods.getPrice() != request.getGoodsPrice()) {
                    UpdateGoodsResponse response = UpdateGoodsResponse.builder()
                            .goodsId(request.getGoodsId())
                            .goodsPrice(goods.getPrice())
                            .build();
                    list.add(response);
                }
            }
            // 상품에 관련된 옵션을 조회
            List<Option> optionList = optionRepository.findByGoodsId(request.getGoodsId());
            // 상품 옵션이 있으면, 옵션 DB 의 최종가격으로 비교
            for (Option option : optionList) {
                if (option.getId().equals(request.getOptionId()) &&
                        option.getTotalPrice() != request.getGoodsTotalPrice()) {
                    UpdateGoodsResponse response = UpdateGoodsResponse.builder()
                            .goodsId(request.getGoodsId())
                            .goodsPrice(option.getTotalPrice())
                            .build();
                    list.add(response);
                }
            }
        }
        return list;
    }


    // 상품 상세(정보)조회
    @Override
    @Transactional(readOnly = true)
    public GoodsResponse goodsDetailFind(Long goodsId) {
        Goods goods = goodsRepository.findById(goodsId).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND_GOODS));

        return GoodsResponse.toGoodsResponse(goods);
    }

    // 상품 검색 ( 키워드 )
    @Override
    @Transactional(readOnly = true)
    public List<GoodsResponse> goodsFindKeyword(String keyword, Pageable pageable) {
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
    public void goodsEdit(Long goodsId, GoodsEditRequest goodsEditRequest, List<String> imgPaths) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName();
        Member member = memberRepository.findByLoginId(loginId).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));

        Goods goods = goodsRepository.findById(goodsId).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_GOODS));
        if (!goods.getMemberId().equals(member.getId()))
            throw new BusinessException(NOT_SELLING_GOODS);

        goods.update(goodsEditRequest);

        //기존 옵션 삭제
        List<Option> options = optionRepository.findByGoodsId(goodsId);
        for (Option option : options) {
            optionRepository.deleteById(option.getId());
        }

        //상품 옵션 수정이 null 이 아니면 저장
        if (goodsEditRequest.getOptionCreateRequest().isEmpty()) {
            List<OptionCreateRequest> optionCreateRequest = goodsEditRequest.getOptionCreateRequest();
            for (OptionCreateRequest createRequest : optionCreateRequest) {
                Option option = Option.toOption(createRequest, goods);
                optionRepository.save(option);
            }
        }

        // s3 , 이미지DB 삭제
        List<Image> imageList = imageRepository.findByGoodsId(goods.getId());
        for (Image image : imageList) {
            String fileName = image.getFileUrl().substring(bucket.length() + 41);
            s3Service.deleteFile(fileName);
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
    public void goodsDelete(Long goodsId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName();

        Member member = memberRepository.findByLoginId(loginId).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));

        Goods goods = goodsRepository.findById(goodsId).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_GOODS));
        if (!goods.getMemberId().equals(member.getId()))
            throw new BusinessException(NOT_SELLING_GOODS);

        // s3 이미지 삭제
        List<Image> imageList = imageRepository.findByGoodsId(goods.getId());
        for (Image image : imageList) {
            String fileName = image.getFileUrl().substring(bucket.length() + 41);
            s3Service.deleteFile(fileName);
        }

        goodsRepository.deleteById(goods.getId());
    }
}
