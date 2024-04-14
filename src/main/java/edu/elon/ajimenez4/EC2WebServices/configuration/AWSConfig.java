package edu.elon.ajimenez4.EC2WebServices.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;


@Configuration
public class AWSConfig {
    @Value("${aws.region}")
    private String awsRegion;
    @Value("${aws.accessKey}")
    private String awsAccessKey;
    @Value("${aws.secretKey}")
    private String awsSecretKey;
    @Value("${aws.sessionToken}")
    private String awsSessionToken;

    @Bean
    public Ec2Client ec2Client(){
        AwsSessionCredentials credentials = AwsSessionCredentials.create(awsAccessKey,awsSecretKey,awsSessionToken);

        return Ec2Client.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(() -> credentials)
                .build();
    }
}
