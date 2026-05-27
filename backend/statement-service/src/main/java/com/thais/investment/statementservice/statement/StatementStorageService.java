package com.thais.investment.statementservice.statement;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class StatementStorageService {

    private static final Logger log = LoggerFactory.getLogger(StatementStorageService.class);

    private final S3Client s3Client;
    private final ObjectMapper objectMapper;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public StatementStorageService(S3Client s3Client, ObjectMapper objectMapper) {
        this.s3Client = s3Client;
        this.objectMapper = objectMapper;
    }

    public void uploadStatement(Statement statement) {
        try {
            String documentKey = statement.getDocumentKey();

            Map<String, Object> statementDocument = Map.of(
                    "statementId", statement.getId(),
                    "settlementId", statement.getSettlementId(),
                    "orderId", statement.getOrderId(),
                    "customerId", statement.getCustomerId(),
                    "assetCode", statement.getAssetCode(),
                    "netAmount", statement.getNetAmount(),
                    "settlementDate", statement.getSettlementDate().toString(),
                    "statementType", statement.getStatementType().name(),
                    "status", statement.getStatus().name(),
                    "createdAt", statement.getCreatedAt().toString()
            );

            String content = objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(statementDocument);

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(documentKey)
                    .contentType("application/json")
                    .build();

            s3Client.putObject(
                    request,
                    RequestBody.fromString(content, StandardCharsets.UTF_8)
            );

            log.info(
                    "Statement document uploaded to S3 successfully: bucket={}, documentKey={}",
                    bucketName,
                    documentKey
            );

        } catch (Exception exception) {
            log.error(
                    "Error uploading statement document to S3: statementId={}",
                    statement.getId(),
                    exception
            );

            throw new RuntimeException("Error uploading statement document to S3", exception);
        }
    }
}