package com.project.shop.goods.service.Impl;

import com.project.shop.goods.domain.enetity.Goods;
import com.project.shop.goods.domain.enetity.Image;
import com.project.shop.goods.domain.request.GoodsCreateRequest;
import com.project.shop.goods.domain.response.GoodsResponse;
import com.project.shop.goods.repository.GoodsRepository;
import com.project.shop.goods.repository.ImageRepository;
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

    // 상품 생성 + 이미지 추가
    @Override
    public void goodsCreate(GoodsCreateRequest goodsCreateRequest, List<MultipartFile> files) throws IOException {
        // 저장할 경로 지정
        String url = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files";

        if (goodsRepository.findByGoodsName(goodsCreateRequest.getGoodsName()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 상품입니다.");
        }

        Goods goods = Goods.toGoods(goodsCreateRequest);
        goodsRepository.save(goods);
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

    // 상품 전체 검색 - 페이징 기능
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
    public List<GoodsResponse> goodsFindKeyword(String keyword) {
        List<Goods> goods = goodsRepository.findGoodsByGoodsNameContaining(keyword);

        List<GoodsResponse> list = new ArrayList<>();
        for (Goods good : goods) {
            List<Image> imageList = imageRepository.findByGoodsId(good.getId());
            GoodsResponse goodsResponse = GoodsResponse.toGoodsResponse(good);
            goodsResponse.setImageList(imageList);
            list.add(goodsResponse);
        }
        return list;
    }

    // 상품 수정

    // 상품 삭제
    @Override
    public void goodsDelete(Long goodsId) {
        goodsRepository.deleteById(goodsId);
    }

}
