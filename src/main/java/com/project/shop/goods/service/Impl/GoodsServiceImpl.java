package com.project.shop.goods.service.Impl;

import com.project.shop.global.aspect.TimerAop;
import com.project.shop.global.error.ErrorCode;
import com.project.shop.global.error.exception.BusinessException;
import com.project.shop.goods.controller.request.GoodsCreateRequest;
import com.project.shop.goods.controller.request.GoodsEditRequest;
import com.project.shop.goods.controller.request.OptionCreateRequest;
import com.project.shop.goods.controller.request.UpdateCheckRequest;
import com.project.shop.goods.controller.response.GoodsPageResponse;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    // ?????? ?????? + ????????? ??????(??????) + ?????? ??????(??????X)
    @Override
    public void goodsCreate(GoodsCreateRequest goodsCreateRequest, List<MultipartFile> imgPaths) throws IOException {

        Member member = getMember();

        // ?????? ????????? ????????? ????????? ????????????
        if (goodsRepository.findByGoodsName(goodsCreateRequest.getGoodsName()).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATE_GOODS);
        }

        Category category = categoryRepository.findById(goodsCreateRequest.getCategoryId()).orElseThrow(
                () -> new BusinessException(NOT_FOUND_CATEGORY));

        // ?????? ????????????
        Goods goods = Goods.create(goodsCreateRequest, category, member);
        goodsRepository.save(goods);

        // ?????? ?????? ??????
        for (OptionCreateRequest optionCreateRequest : goodsCreateRequest.getOptionCreateRequest()) {
            Options options = Options.toOption(optionCreateRequest, goods);
            optionRepository.save(options);
        }

        // S3 ??????
        List<String> list = s3Service.upload(imgPaths);

        // ????????? DB ??????
        for (String img : list) {
            Image image = Image.builder().fileUrl(img).goods(goods).build();
            imageRepository.save(image);
        }
    }

    // ?????? ?????? ??????
    @Override
    @TimerAop
    @Transactional(readOnly = true)
    @Cacheable(value = "goodsFind", key = "#pageable")
    public List<GoodsPageResponse> goodsFindAll(Pageable pageable) {
        System.out.println("=============== EHcache ?????? ===============");
        Page<Goods> goods = goodsRepository.findAll(pageable);
        List<GoodsPageResponse> list = new ArrayList<>();

        for (Goods good : goods) {
            GoodsPageResponse goodsPageResponse = GoodsPageResponse.toResponse(good, goods);
            list.add(goodsPageResponse);
        }
        System.out.println("=============== EHcache ?????? ===============");
        return list;

    }

    // ?????? ?????? ?????? ??????
    @Transactional(readOnly = true)
    public List<UpdateGoodsResponse> checkGoodsUpdate(List<UpdateCheckRequest> updateCheckRequest) {
        List<UpdateGoodsResponse> list = new ArrayList<>();
        for (UpdateCheckRequest request : updateCheckRequest) {
            Goods goods = goodsRepository.findById(request.getGoodsId()).orElseThrow(
                    () -> new BusinessException(NOT_FOUND_GOODS));

            // ????????? ?????? ????????????
            if (goods.getOptions().isEmpty()) {
                // ?????? ?????? ID ??? ???????????? ????????????
                if (request.getOptionId() != null)
                    throw new BusinessException(NOT_FOUND_OPTION);
                UpdateGoodsResponse goodsResponse = UpdateGoodsResponse.builder()
                        .goodsId(request.getGoodsId()).goodsPrice(request.getGoodsPrice()).changeCheck(false).build();
                // DB ?????? != ????????? ?????? -> ChangeCheck true ??? ??????
                if (goods.getPrice() != request.getGoodsPrice())
                    goodsResponse.setChangeCheck(true);
                list.add(goodsResponse);
                return list;
            }

            // ????????? ?????? ????????????
            Options options = optionRepository.findByIdAndGoodsId(request.getOptionId(), request.getGoodsId()).orElseThrow(
                    () -> new BusinessException(NOT_FOUND_OPTION));
            UpdateGoodsResponse goodsResponse = UpdateGoodsResponse.builder()
                    .goodsId(request.getGoodsId()).goodsPrice(options.getTotalPrice()).changeCheck(false).build();
            // DB ?????? ?????? != ????????? ?????? -> ChangeCheck true ??? ??????
            if (options.getTotalPrice() != request.getGoodsPrice())
                goodsResponse.setChangeCheck(true);
            list.add(goodsResponse);
        }
        return list;
    }

    // ?????? ??????(??????)??????
    @Override
    @TimerAop
    @Transactional(readOnly = true)
    public GoodsResponse goodsDetailFind(Long goodsId) {
        Goods goods = goodsRepository.findById(goodsId).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND_GOODS));
        return GoodsResponse.toResponse(goods);
    }

    // ?????? ?????? ( ????????? )
    @Override
    @TimerAop
    @Transactional(readOnly = true)
    public List<GoodsPageResponse> goodsFindKeyword(String keyword, Pageable pageable) {
        // keyword ??? ?????? ??? ?????? ?????? ??????
        Page<Goods> goods = goodsRepository.findGoodsByGoodsNameContaining(pageable, keyword);

        List<GoodsPageResponse> list = new ArrayList<>();
        // ????????? ????????? ????????? ????????? ?????? ??????
        for (Goods good : goods) {
            GoodsPageResponse goodsPageResponse = GoodsPageResponse.toResponse(good, goods);
            list.add(goodsPageResponse);
        }
        return list;
    }

    // ?????? ??????
    @Override
    public void goodsEdit(Long goodsId, GoodsEditRequest goodsEditRequest, List<MultipartFile> imgPaths) {

        Member member = getMember();


        // ????????? ??????????????? ?????? ???????????? ????????????
        if (goodsRepository.findByGoodsName(goodsEditRequest.getGoodsName()).isPresent())
            throw new BusinessException(ErrorCode.DUPLICATE_GOODS);

        Goods goods = goodsRepository.findById(goodsId).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_GOODS));
        // ????????? ????????? ????????? ???????????? ????????? ????????? ????????????
        if (!goods.getMemberId().equals(member.getId()))
            throw new BusinessException(NOT_SELLING_GOODS);

        goods.update(goodsEditRequest);

        //?????? ?????? ??????
        List<Options> options = optionRepository.findByGoodsId(goodsId);
        for (Options option : options) {
            optionRepository.deleteById(option.getId());
        }

        // ????????? ????????? ????????? ??????????????? null ??? ????????? ?????? DB ??? ??????
        if (!goodsEditRequest.getOptionCreateRequest().isEmpty() && goodsEditRequest.getGoodsDescription() != null) {
            List<OptionCreateRequest> optionCreateRequest = goodsEditRequest.getOptionCreateRequest();
            for (OptionCreateRequest createRequest : optionCreateRequest) {
                Options option = Options.toOption(createRequest, goods);
                optionRepository.save(option);
            }
        }

        // s3 , ?????????DB ??????
        List<Image> imageList = imageRepository.findByGoodsId(goods.getId());
        for (Image image : imageList) {
            String fileName = image.getFileUrl().substring(bucket.length() + 41);
            s3Service.deleteFile(fileName);
            imageRepository.deleteById(image.getId());
        }

        // S3 ????????? ??????
        List<String> list = s3Service.upload(imgPaths);

        // ????????? ?????? ??????
        List<String> imgList = new ArrayList<>();
        for (String img : list) {
            Image image = Image.builder().fileUrl(img).goods(goods).build();
            imageRepository.save(image);
            imgList.add(image.getFileUrl());
        }
    }

    // ?????? ??????
    @Override
    public void goodsDelete(Long goodsId) {

        Member member = getMember();

        Goods goods = goodsRepository.findById(goodsId).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_GOODS));
        // ?????? ????????? ???????????? ????????? ????????? ????????????
        if (!goods.getMemberId().equals(member.getId()))
            throw new BusinessException(NOT_SELLING_GOODS);

        // s3 ????????? ??????
        List<Image> imageList = imageRepository.findByGoodsId(goods.getId());
        for (Image image : imageList) {
            String fileName = image.getFileUrl().substring(bucket.length() + 41);
            s3Service.deleteFile(fileName);
        }

        goodsRepository.deleteById(goods.getId());
    }

    private Member getMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Member member = memberRepository.findByLoginId(authentication.getName()).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));
        return member;
    }
}
