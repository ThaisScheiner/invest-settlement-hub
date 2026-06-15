package com.thais.investment.statementservice.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class StatementMetrics {

    private final Counter processingSuccessCounter;
    private final Counter processingErrorCounter;
    private final Counter generatedCounter;
    private final Counter duplicateSkippedCounter;
    private final Counter s3UploadSuccessCounter;
    private final Counter s3UploadErrorCounter;
    private final Counter downloadSuccessCounter;
    private final Counter downloadErrorCounter;

    public StatementMetrics(MeterRegistry meterRegistry) {
        this.processingSuccessCounter = Counter.builder("statement_processing_success_total")
                .description("Total number of statement messages processed successfully")
                .register(meterRegistry);

        this.processingErrorCounter = Counter.builder("statement_processing_error_total")
                .description("Total number of statement messages that failed processing")
                .register(meterRegistry);

        this.generatedCounter = Counter.builder("statement_generated_total")
                .description("Total number of statements generated successfully")
                .register(meterRegistry);

        this.duplicateSkippedCounter = Counter.builder("statement_duplicate_skipped_total")
                .description("Total number of duplicated statement events skipped")
                .register(meterRegistry);

        this.s3UploadSuccessCounter = Counter.builder("statement_s3_upload_success_total")
                .description("Total number of statement documents uploaded to S3 successfully")
                .register(meterRegistry);

        this.s3UploadErrorCounter = Counter.builder("statement_s3_upload_error_total")
                .description("Total number of statement document upload errors to S3")
                .register(meterRegistry);

        this.downloadSuccessCounter = Counter.builder("statement_download_success_total")
                .description("Total number of statement downloads completed successfully")
                .register(meterRegistry);

        this.downloadErrorCounter = Counter.builder("statement_download_error_total")
                .description("Total number of statement download errors")
                .register(meterRegistry);
    }

    public void incrementSuccess() {
        processingSuccessCounter.increment();
    }

    public void incrementError() {
        processingErrorCounter.increment();
    }

    public void incrementGenerated() {
        generatedCounter.increment();
    }

    public void incrementDuplicateSkipped() {
        duplicateSkippedCounter.increment();
    }

    public void incrementS3UploadSuccess() {
        s3UploadSuccessCounter.increment();
    }

    public void incrementS3UploadError() {
        s3UploadErrorCounter.increment();
    }

    public void incrementDownloadSuccess() {
        downloadSuccessCounter.increment();
    }

    public void incrementDownloadError() {
        downloadErrorCounter.increment();
    }
}