package com.dhguo.book;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;

import java.util.List;

@SpringBootApplication
@EnableAsync
public class BookNetworkApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookNetworkApiApplication.class, args);
//		System.out.println("Hello World");
//		ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create("dhguo-personal-projects-bucket");
//		S3Client s3Client = S3Client.builder()
//				.region(Region.US_WEST_2)
//				.credentialsProvider(credentialsProvider)
//				.build();
//		List<Bucket> bucketList = s3Client.listBuckets().buckets();
//		System.out.println("Buckets:");
//		for (Bucket bucket : bucketList) {
//			System.out.println(bucket.name());
//		}
	}

}
