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

    private final GoodsRepository goodsRepository;
    private final ImageRepository imageRepository;
    private final OptionRepository optionRepository;

    // 상품 등록 + 이미지 추가
    @Override
    public void goodsCreate(GoodsCreateRequest goodsCreateRequest, List<MultipartFile> files, OptionCreateRequest optionCreateRequest) throws IOException {
        // 저장할 경로 지정
        String url = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files";

        if (goodsRepository.findByGoodsName(goodsCreateRequest.getGoodsName()).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATE_GOODS);
        }

        // 상품 정보저장
        Goods goods = Goods.toGoods(goodsCreateRequest);
        goodsRepository.save(goods);

        // 옵션 정보저장
        if (optionCreateRequest != null) {
            Option option = Option.toOption(optionCreateRequest);
            option.setGoods(goods);
            optionRepository.save(option);
        }

        // 이미지 정보 저장
        if (!files.isEmpty()) {
            for (MultipartFile file : files) {
                // 랜덤 값 생성
                UUID uuid = UUID.randomUUID();
                // 파일 이름 앞에 붙이기
                String fileName = uuid + "_" + file.getOriginalFilename();
                // 파일 저장
                File saveFile = new File(url, fileName);
                file.transferTo(saveFile);

                Image image = Image.builder()
                        .fileName(fileName)
                        .fileUrl(url)
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
        goods.forEach(g -> list.add(GoodsResponse.toGoodsResponse(g)));
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
    public void goodsEdit(Long goodsId, GoodsEditRequest goodsEditRequest) {
        Goods goods = goodsRepository.findById(goodsId).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND_GOODS));

        // 상품 정보 변경 확인 메서드
        goods.updateCheckChange();
        goods.update(goodsEditRequest);
    }

    // 상품 삭제
    @Override
    public void goodsDelete(Long goodsId) {
        goodsRepository.deleteById(goodsId);
    }


}
