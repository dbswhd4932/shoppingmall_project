package com.project.shop.goods.service.Impl;

import com.project.shop.global.error.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.project.shop.global.error.ErrorCode.*;

@Service
@Transactional
public class LocalFileService {

    @Value("${file.upload.directory:uploads}")
    private String uploadDirectory;

    public List<String> upload(List<MultipartFile> multipartFiles) {
        List<String> fileUrlList = new ArrayList<>();

        if (multipartFiles == null) {
            throw new BusinessException(REQUIRED_IMAGE);
        }

        // 업로드 디렉토리 생성
        File uploadDir = new File(uploadDirectory);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        for (MultipartFile file : multipartFiles) {
            String fileName = createFileName(file.getOriginalFilename());
            Path filePath = Paths.get(uploadDirectory, fileName);

            try {
                Files.write(filePath, file.getBytes());
                // 파일 URL 반환 (로컬 경로)
                fileUrlList.add("/uploads/" + fileName);
            } catch (IOException e) {
                throw new BusinessException(UPLOAD_ERROR_IMAGE);
            }
        }
        return fileUrlList;
    }

    // 이미지 파일명 중복 방지
    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    // 파일 유효성 검사
    private String getFileExtension(String fileName) {
        if (fileName.length() == 0) {
            throw new BusinessException(REQUIRED_IMAGE);
        }
        ArrayList<String> fileValidate = new ArrayList<>();
        fileValidate.add(".jpg");
        fileValidate.add(".jpeg");
        fileValidate.add(".png");
        fileValidate.add(".JPG");
        fileValidate.add(".JPEG");
        fileValidate.add(".PNG");
        String idxFileName = fileName.substring(fileName.lastIndexOf("."));
        if (!fileValidate.contains(idxFileName)) {
            throw new BusinessException(VALID_ERROR_IMAGE);
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

    // 로컬 파일 삭제
    public void deleteFile(String fileUrl) {
        try {
            // "/uploads/" 접두사 제거
            String fileName = fileUrl.replace("/uploads/", "");
            Path filePath = Paths.get(uploadDirectory, fileName);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // 파일 삭제 실패는 무시 (이미 없을 수도 있음)
        }
    }
}
