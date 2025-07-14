package com.sprint5team.monew.base.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
public class S3Uploader {

    private S3Client s3Client;
    private final String bucket;
    private final String region;
    private final String accessKey;
    private final String secretKey;

    public S3Uploader(
            @Value("${aws.s3.access-key}") String accessKey,
            @Value("${aws.s3.secret-key}") String secretKey,
            @Value("${aws.s3.region}") String region,
            @Value("${aws.s3.bucket}") String bucket
    ) {
        this.bucket = bucket;
        this.region = region;
        this.accessKey = accessKey;
        this.secretKey = secretKey;

        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    public void upload(String fileName, String json) {
        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(fileName)
                        .contentType("application/json")
                        .build(),
                RequestBody.fromString(json));
    }
}
