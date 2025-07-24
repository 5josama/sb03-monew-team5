package com.sprint5team.monew.base.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
public class S3Storage {

    private S3Client s3Client;
    private final String bucket;
    private final String region;
    private final String accessKey;
    private final String secretKey;
    private final Path logDir = Paths.get(".logs");

    public S3Storage(
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

    public List<String> readArticlesFromBackup(Instant from, Instant to) {
        List<String> jsonFiles = new ArrayList<>();

        LocalDate start = from.atZone(ZoneOffset.UTC).toLocalDate();
        LocalDate end = to.atZone(ZoneOffset.UTC).toLocalDate();

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            String key = "backup/news_" + date + ".json";

            try {
                ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(
                        GetObjectRequest.builder()
                                .bucket(bucket)
                                .key(key)
                                .build()
                );
                String content = new BufferedReader(new InputStreamReader(s3Object))
                        .lines().collect(Collectors.joining("\n"));

                jsonFiles.add(content); // 또는 content → Article 변환까지 처리 가능
            } catch (NoSuchKeyException e) {
                // 해당 날짜 파일 없을 수 있으므로 무시
            }
        }
        return jsonFiles;
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void uploadLogs() {
        try (Stream<Path> files = Files.walk(logDir)) {
            files
                    .filter(Files::isRegularFile)
                    .filter(this::isDatePatternLogFile)
                    .forEach(this::upload);
        } catch (IOException e) {
            log.error("로그 파일 탐색 실패", e);
        }
    }

    private void upload(Path path) {
        String key = "logs/" + path.getFileName();

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType("text/plain")
                    .build();

            s3Client.putObject(request, path);

            log.info("S3 업로드 완료: {}", key);

            // 업로드 후 삭제
            Files.deleteIfExists(path);

        } catch (Exception e) {
            log.error("S3 업로드 실패 - {}", path, e);
        }
    }

    private boolean isDatePatternLogFile(Path path) {
        String fileName = path.getFileName().toString();
        return fileName.matches("application\\.\\d{4}-\\d{2}-\\d{2}\\.log");
    }
}
