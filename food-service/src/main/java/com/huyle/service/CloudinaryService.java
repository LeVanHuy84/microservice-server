package com.huyle.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.huyle.utils.ImageInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {

    private final Cloudinary cloudinary;

    @Value("${CLOUDINARY_FOLDER:default-folder}")
    private String cloudinaryFolder;

    public void deleteImage(String publicId) throws IOException {
        if (publicId == null || publicId.isBlank()) return;

        cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("invalidate", true));
        log.info("Deleted image with public_id: {}", publicId);
    }

    public void deleteMultipleImages(List<String> publicIds) throws Exception {
        if (publicIds == null || publicIds.isEmpty()) return;

        cloudinary.api().deleteResources(publicIds, ObjectUtils.asMap("invalidate", true));
        log.info("Deleted multiple images: {}", publicIds);
    }

    public ImageInfo uploadImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;

        var uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", cloudinaryFolder,
                        "resource_type", "auto"
                )
        );

        return new ImageInfo(
                uploadResult.get("public_id").toString(),
                uploadResult.get("url").toString()
        );
    }

    public List<ImageInfo> uploadMultipleImages(List<MultipartFile> files) throws IOException {
        if (files == null || files.isEmpty()) return null;

        return files.stream()
                .filter(file -> file != null && !file.isEmpty())
                .map(file -> {
                    try {
                        return uploadImage(file);
                    } catch (IOException e) {
                        log.error("Error uploading image: {}", file.getOriginalFilename(), e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }
}
