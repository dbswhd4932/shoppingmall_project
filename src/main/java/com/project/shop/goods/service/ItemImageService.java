package com.project.shop.goods.service;

import com.project.shop.goods.repository.ItemImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemImageService {

    private final ItemImageRepository itemImageRepository;

    private String location = "C:\\Users\\dbswh\\goodsImages";

    public void upload(MultipartFile file, String filename) throws IOException {
        try {
            file.transferTo(new File(location + filename));
        } catch (IOException e) {
            throw new IllegalArgumentException("이미지 업로드 실패");
        }
    }

}
