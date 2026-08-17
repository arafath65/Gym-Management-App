package com.arafath65.gymManagement.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class MemberImageStorageService {

    @Value("${gym.upload.base-path}")
    private String basePath;

    // SAVE IMAGE
    public String saveImage(
            MultipartFile file,
            Integer tenantId,
            Integer memberId) throws IOException {

        if (file == null || file.isEmpty()) {
            return null;
        }

        String contentType =
                file.getContentType();

        if (contentType == null ||
                !contentType.startsWith("image/")) {

            throw new IllegalArgumentException(
                    "Only image files are allowed."
            );
        }

        Path directory =
                Paths.get(
                        basePath,
                        "tenant-" + tenantId,
                        "members",
                        String.valueOf(memberId),
                        "profile"
                );

        Files.createDirectories(
                directory
        );

        String extension =
                getExtension(
                        file.getOriginalFilename()
                );

        String fileName =
                "profile_"
                        + UUID.randomUUID()
                        + extension;

        Path target =
                directory.resolve(
                        fileName
                );

        Files.copy(
                file.getInputStream(),
                target,
                StandardCopyOption.REPLACE_EXISTING
        );

        return target
                .toString()
                .replace(
                        "\\",
                        "/"
                );
    }

    // DELETE IMAGE
    public void deleteImage(
            String imagePath) {

        if (imagePath == null ||
                imagePath.trim().isEmpty()) {

            return;
        }

        try {

            Files.deleteIfExists(
                    Paths.get(imagePath)
            );

        } catch (Exception ignored) {
        }
    }

    // GET EXTENSION
    private String getExtension(
            String originalFileName) {

        if (originalFileName == null ||
                originalFileName.trim().isEmpty()) {

            return ".jpg";
        }

        int index =
                originalFileName.lastIndexOf(".");

        if (index < 0) {
            return ".jpg";
        }

        String extension =
                originalFileName
                        .substring(index)
                        .toLowerCase();

        if (extension.equals(".jpeg")) {
            return ".jpg";
        }

        if (!extension.equals(".jpg") &&
                !extension.equals(".png") &&
                !extension.equals(".webp")) {

            return ".jpg";
        }

        return extension;
    }
}