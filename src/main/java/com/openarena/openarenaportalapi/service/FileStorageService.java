package com.openarena.openarenaportalapi.service; // Or your service package

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class FileStorageService {

    private final AmazonS3 s3client;
    private final String bucketName;
    private final String endpointUrl; // To construct full URLs if needed

    @Autowired
    public FileStorageService(AmazonS3 s3client,
                              @Value("${aws.s3.bucket-name}") String bucketName,
                              @Value("${aws.s3.endpoint-url}") String endpointUrl) {
        this.s3client = s3client;
        this.bucketName = bucketName;
        this.endpointUrl = endpointUrl;
    }

    /**
     * Uploads a file to S3 (MinIO).
     * @param file The MultipartFile to upload.
     * @param directory The directory within the bucket (e.g., "resumes", "profile-pictures").
     * @return The object key (path within the bucket) of the uploaded file.
     * @throws IOException if file upload fails.
     */
    public String uploadFile(MultipartFile file, String directory) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        // Create a unique file name to avoid collisions
        String uniqueFileName = UUID.randomUUID().toString() + extension;
        String objectKey = directory + "/" + uniqueFileName;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        try {
            s3client.putObject(new PutObjectRequest(bucketName, objectKey, file.getInputStream(), metadata));
            System.out.println("File uploaded successfully to: " + objectKey);
            // Return the key, or you can construct the full URL
            // For MinIO, the URL is typically endpointUrl/bucketName/objectKey
            // return endpointUrl + "/" + bucketName + "/" + objectKey;
            return objectKey; // Returning the key is often more flexible
        } catch (Exception e) {
            System.err.println("Error uploading file to S3: " + e.getMessage());
            throw new IOException("Failed to upload file: " + e.getMessage(), e);
        }
    }
}