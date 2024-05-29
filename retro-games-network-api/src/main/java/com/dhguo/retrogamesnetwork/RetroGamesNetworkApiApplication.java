package com.dhguo.retrogamesnetwork;

import com.dhguo.retrogamesnetwork.role.Role;
import com.dhguo.retrogamesnetwork.role.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@SpringBootApplication
@EnableAsync
public class RetroGamesNetworkApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(RetroGamesNetworkApiApplication.class, args);
//    System.out.println("Hello World");
//    ProfileCredentialsProvider credentialsProvider =
//        ProfileCredentialsProvider.create("dhguo-personal-projects-bucket");
//    S3Client s3Client =
//        S3Client.builder()
//            .region(Region.US_EAST_1)
//            .credentialsProvider(credentialsProvider)
//            .build();
//    List<Bucket> bucketList = s3Client.listBuckets().buckets();
//    System.out.println("Buckets:");
//    for (Bucket bucket : bucketList) {
//      System.out.println(bucket.name());
//    }
//
//    Path imagePath = Path.of("test.jpg"); // Replace with your actual path
//
//    byte[] imageBytes;
//    try (FileInputStream fileInputStream = new FileInputStream(imagePath.toFile())) {
//      imageBytes = fileInputStream.readAllBytes();
//    } catch (IOException e) {
//      System.err.println("Error reading image file: " + e.getMessage());
//      return; // Or handle the error as needed
//    }
//
//    PutObjectRequest objectRequest =
//        PutObjectRequest.builder()
//            .bucket("dhguo-personal-projects-bucket")
//            .key("retro-games-network-images/" + imagePath.getFileName())
//            .build();
//
//    s3Client.putObject(objectRequest, RequestBody.fromBytes(imageBytes));
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
