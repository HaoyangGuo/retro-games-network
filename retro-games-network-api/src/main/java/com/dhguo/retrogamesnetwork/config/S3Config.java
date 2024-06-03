package com.dhguo.retrogamesnetwork.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@RequiredArgsConstructor
public class S3Config {

  @Bean
  @Profile("dev")
  public S3Client s3ClientDev() {
    ProfileCredentialsProvider credentialsProvider =
        ProfileCredentialsProvider.create("dhguo-personal-projects-bucket");

    return S3Client.builder()
        .region(Region.US_EAST_1)
        .credentialsProvider(credentialsProvider)
        .build();
  }

  @Bean
  @Profile("prod")
  public S3Client s3ClientProd() {
    return S3Client.builder().region(Region.US_EAST_1).build();
  }
}
