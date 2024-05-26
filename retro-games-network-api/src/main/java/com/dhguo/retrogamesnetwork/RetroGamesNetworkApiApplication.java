package com.dhguo.retrogamesnetwork;

import com.dhguo.retrogamesnetwork.role.Role;
import com.dhguo.retrogamesnetwork.role.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;

import java.util.List;

@EnableJpaAuditing()
@SpringBootApplication
@EnableAsync
public class RetroGamesNetworkApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(RetroGamesNetworkApiApplication.class, args);
    //		System.out.println("Hello World");
    //		ProfileCredentialsProvider credentialsProvider =
    // ProfileCredentialsProvider.create("dhguo-personal-projects-bucket");
    //		S3Client s3Client = S3Client.builder()
    //				.region(Region.US_EAST_1)
    //				.credentialsProvider(credentialsProvider)
    //				.build();
    //		List<Bucket> bucketList = s3Client.listBuckets().buckets();
    //		System.out.println("Buckets:");
    //		for (Bucket bucket : bucketList) {
    //			System.out.println(bucket.name());
    //		}
  }

  @Bean
  public CommandLineRunner runner(RoleRepository roleRepository) {
    return args -> {
      if (roleRepository.findByName("USER").isEmpty()) {
        roleRepository.save(Role.builder().name("USER").build());
      }
    };
  }
}
