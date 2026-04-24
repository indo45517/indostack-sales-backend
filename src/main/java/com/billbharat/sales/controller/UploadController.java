package com.billbharat.sales.controller;

import com.billbharat.sales.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sales/upload")
@RequiredArgsConstructor
@Tag(name = "Upload", description = "File upload endpoints")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class UploadController {

    @Value("${upload.directory:./uploads}")
    private String uploadDir;

    @PostMapping("/image")
    @Operation(summary = "Upload an image")
    public ResponseEntity<Map<String, Object>> uploadImage(
            @RequestParam("file") MultipartFile file) {
        try {
            // Sanitize filename: strip any path separators and use only the base name
            String originalName = file.getOriginalFilename();
            String safeName = (originalName != null && !originalName.isBlank())
                    ? Paths.get(originalName).getFileName().toString().replaceAll("[^a-zA-Z0-9._-]", "_")
                    : "upload";
            String fileName = UUID.randomUUID() + "_" + safeName;

            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(fileName).normalize();

            // Ensure resolved path is inside the upload directory (prevent path traversal)
            if (!filePath.startsWith(uploadPath)) {
                return ResponseEntity.badRequest()
                        .body(ResponseUtil.error("Invalid file name"));
            }
            Files.write(filePath, file.getBytes());

            Map<String, Object> result = new HashMap<>();
            result.put("fileName", fileName);
            result.put("url", "/uploads/" + fileName);
            result.put("size", file.getSize());
            result.put("contentType", file.getContentType());

            return ResponseEntity.ok(ResponseUtil.success("Image uploaded successfully", result));
        } catch (IOException e) {
            log.error("Failed to upload file", e);
            return ResponseEntity.internalServerError()
                    .body(ResponseUtil.error("Failed to upload file: " + e.getMessage()));
        }
    }
}
