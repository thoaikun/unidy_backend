package com.unidy.backend.S3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.auth.credentials.*;

@Configuration
public class S3Config {
    @Value("${aws.region}")
    private String awsRegion;

    @Value("${aws.credentials.access-key}")
    private String awsAccessKey;

    @Value("${aws.credentials.secret-key}")
    private String awsSecretKey;

    @Bean
    public S3Client s3Client(){
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
            awsAccessKey,
            awsSecretKey
        );

        return S3Client.builder()
            .region(Region.of(awsRegion))
            .credentialsProvider(() -> credentials)
            .build();
    }
}
