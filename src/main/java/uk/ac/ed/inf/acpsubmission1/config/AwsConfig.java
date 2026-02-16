package uk.ac.ed.inf.acpsubmission1.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;


import java.net.URI;

@Configuration
public class AwsConfig {

    @Bean
    public S3Client s3Client(@Value("${ACP_S3:http://localhost:4566}") String s3Endpoint) {
        return S3Client.builder()
                .endpointOverride(URI.create(s3Endpoint))
                .forcePathStyle(true)
                .region(Region.US_EAST_1)
                .credentialsProvider(
                        StaticCredentialsProvider.create(AwsBasicCredentials.create("test", "test"))
                )
                .httpClient(UrlConnectionHttpClient.create())
                .forcePathStyle(true)
                .build();
    }

    @Bean
    public DynamoDbClient dynamoDbClient(@Value("${ACP_DYNAMODB:http://localhost:4566}") String dynamoDbEndpoint){
        return DynamoDbClient.builder()
                .endpointOverride(URI.create(dynamoDbEndpoint))
                .region(Region.US_EAST_1)
                .credentialsProvider(
                        StaticCredentialsProvider.create(AwsBasicCredentials.create("test", "test"))
                )
                .httpClient(UrlConnectionHttpClient.create())
                .build();

    }
}
