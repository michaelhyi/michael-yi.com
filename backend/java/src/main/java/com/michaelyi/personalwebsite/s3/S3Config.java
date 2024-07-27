package com.michaelyi.personalwebsite.s3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import static com.michaelyi.personalwebsite.util.Constants.AWS_ACCESS_KEY;
import static com.michaelyi.personalwebsite.util.Constants.AWS_REGION;
import static com.michaelyi.personalwebsite.util.Constants.AWS_SECRET_KEY;

@Configuration
public class S3Config {
    @Value(AWS_ACCESS_KEY)
    private String accessKey;

    @Value(AWS_SECRET_KEY)
    private String secretKey;

    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials
                .create(accessKey, secretKey);

        return S3Client
                .builder()
                .credentialsProvider(
                        StaticCredentialsProvider
                                .create(credentials))
                .region(Region.of(AWS_REGION))
                .build();
    }
}