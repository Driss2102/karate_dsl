package com.karate.karatespring.controller;


import com.karate.karatespring.service.S3Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("/s3")
public class S3Controller {

    private final S3Service s3Service;

    public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @PostMapping(value = "/upload", consumes = "application/octet-stream")
    public ResponseEntity<String> uploadFile(@RequestBody byte[] fileBytes) throws IOException {
        File tempFile = File.createTempFile("upload_", ".tmp");
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(fileBytes);
        }

        // Upload the file to S3
        String result = s3Service.uploadFile(tempFile);

        // Clean up the temporary file
        tempFile.delete();

        return ResponseEntity.ok(result);
    }
}