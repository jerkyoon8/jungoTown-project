package com.juwon.springcommunity.util;

import com.juwon.springcommunity.domain.ProductImage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class FileStore {

    @Value("${file.dir}")
    private String fileDir;

    // 파일명을 받아 전체 저장 경로를 반환합니다.
    public String getFullPath(String filename) {
        return fileDir + "/post/" + filename;
    }

    // 여러 개의 파일을 받아 저장하고, 저장된 파일 정보 리스트를 반환합니다.
    public List<ProductImage> storeFiles(List<MultipartFile> multipartFiles, Long postId) throws IOException {
        List<ProductImage> storeFileResult = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            if (!multipartFile.isEmpty()) {
                storeFileResult.add(storeFile(multipartFile, postId));
            }
        }
        return storeFileResult;
    }

    // 단일 파일을 서버에 저장하고, 저장된 파일 정보를 반환합니다.
    public ProductImage storeFile(MultipartFile multipartFile, Long postId) throws IOException {
        if (multipartFile.isEmpty()) {
            return null;
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(originalFilename);
        multipartFile.transferTo(new File(getFullPath(storeFileName)));

        return new ProductImage(null, postId, originalFilename, storeFileName, getFullPath(storeFileName), multipartFile.getSize(), null, null);
    }

    // 서버에 저장된 파일을 삭제합니다.
    public void deleteFile(String storeFileName) {
        File file = new File(getFullPath(storeFileName));
        if (file.exists()) {
            file.delete();
        }
    }

    // 서버에 저장될 고유한 파일명을 생성합니다. (UUID.확장자)
    private String createStoreFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    // 파일명에서 확장자를 추출합니다.
    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }
}
