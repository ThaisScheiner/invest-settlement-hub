package com.thais.investment.statementservice.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class StatementMetrics {

    private final Counter successCounter;
    private final Counter errorCounter;
    private final Counter s3UploadSuccessCounter;
    private final Counter s3UploadErrorCounter;

    public StatementMetrics(MeterRegistry meterRegistry) {
        this.successCounter = Counter.builder("statement_processing_success_total")
                .description("Total number of statement messages processed successfully")
                .register(meterRegistry);

        this.errorCounter = Counter.builder("statement_processing_error_total")
                .description("Total number of statement messages that failed processing")
                .register(meterRegistry);

        this.s3UploadSuccessCounter = Counter.builder("statement_s3_upload_success_total")
                .description("Total number of statement documents uploaded to S3 successfully")
                .register(meterRegistry);

        this.s3UploadErrorCounter = Counter.builder("statement_s3_upload_error_total")
                .description("Total number of statement document upload errors to S3")
                .register(meterRegistry);
    }

    public void incrementSuccess() {
        successCounter.increment();
    }

    public void incrementError() {
        errorCounter.increment();
    }

    public void incrementS3UploadSuccess() {
        s3UploadSuccessCounter.increment();
    }

    public void incrementS3UploadError() {
        s3UploadErrorCounter.increment();
    }
}