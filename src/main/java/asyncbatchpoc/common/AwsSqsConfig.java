package asyncbatchpoc.common;

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
  @Bean
  public SqsClient sqsClient() {
    AwsCredentials awsCredentials =
        AwsBasicCredentials.create("dummy", "dummy");

    SqsClient sqsClient =
        SqsClient
            .builder()
            .defaultsMode(DefaultsMode.AUTO)
            .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
            .region(Region.US_EAST_1)
            .endpointOverride(URI.create("http://localhost:4566"))
            .build();

    return sqsClient;
  }
}
