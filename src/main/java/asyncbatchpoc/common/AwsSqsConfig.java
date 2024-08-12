package asyncbatchpoc.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.awscore.defaultsmode.DefaultsMode;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.net.URI;

@Configuration
public class AwsSqsConfig {

  @Value("${aws.credentials.accessKey}")
  private String accessKey;

  @Value("${aws.credentials.secretKey}")
  private String secretKey;

  @Value("${aws.region}")
  private String region;

  @Value("${aws.sqs.endpoint}")
  private String endpoint;

  @Bean
  public SqsClient sqsClient() {
    AwsCredentials awsCredentials =
        AwsBasicCredentials.create(accessKey, secretKey);

    SqsClient sqsClient =
        SqsClient
            .builder()
            .defaultsMode(DefaultsMode.AUTO)
            .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
            .region(Region.of(region))
            .endpointOverride(URI.create(endpoint))
            .build();

    return sqsClient;
  }
}
