package edu.elon.ajimenez4.EC2WebServices.services.aws;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AWSEC2Services {

    private final Logger logger = LoggerFactory.getLogger(AWSEC2Services.class);
    @Getter
    private Ec2Client ec2Client;

    @Autowired
    public void AWSEC2Service(Ec2Client ec2Client){
        this.ec2Client = ec2Client;
    }

    public Instance createEC2Instance(String name, String instanceType) {
    RunInstancesRequest runRequest = RunInstancesRequest.builder()
            .imageId("ami-051f8a213df8bc089")
            .instanceType(InstanceType.fromValue(instanceType))
            .maxCount(1)
            .minCount(1)
            .build();
    RunInstancesResponse response = ec2Client.runInstances(runRequest);
    Instance instance = response.instances().get(0);
    String instanceId = instance.instanceId();
    Tag tag = Tag.builder()
            .key("Name")
            .value(name)
            .build();
    CreateTagsRequest tagRequest = CreateTagsRequest.builder()
            .resources(instanceId)
            .tags(tag)
            .build();

    try {
        ec2Client.createTags(tagRequest);
    } catch (Ec2Exception e) {
        System.err.println(e.awsErrorDetails().errorMessage());
        System.exit(1);
    }

    return instance;
}

    public Instance startInstance(String instanceId) {
        Ec2Client ec2 = getEc2Client();
        StartInstancesRequest request = StartInstancesRequest.builder()
                .instanceIds(instanceId)
                .build();
        ec2.startInstances(request);
        return ec2.describeInstances().reservations().get(0).instances().get(0);
    }

    public Instance stopInstance(String instanceId) {
        Ec2Client ec2 = getEc2Client();
        StopInstancesRequest request = StopInstancesRequest.builder()
                .instanceIds(instanceId)
                .build();
        ec2.stopInstances(request);
        return ec2.describeInstances().reservations().get(0).instances().get(0);
    }

    public Instance terminateInstance(String instanceId) {
        try {
            Ec2Client ec2 = getEc2Client();
            TerminateInstancesRequest terminate = TerminateInstancesRequest.builder()
                    .instanceIds(instanceId)
                    .build();
            ec2.terminateInstances(terminate);
            return ec2.describeInstances().reservations().get(0).instances().get(0);

        } catch (Ec2Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return null;
    }

    public List<Instance> getAllInstances() {
        DescribeInstancesResponse response = this.ec2Client.describeInstances();

        List<Instance> ec2List = response.reservations().stream()
                .flatMap(reservation -> reservation.instances().stream())
                .collect(Collectors.toList());

        return ec2List;
    }

    public Instance getInstance(String instanceId) {
        Ec2Client ec2 = getEc2Client();
        DescribeInstancesRequest request = DescribeInstancesRequest.builder()
                .instanceIds(instanceId)
                .build();
        DescribeInstancesResponse response = ec2.describeInstances(request);
        Instance instance = response.reservations().get(0).instances().get(0);

        return instance;
    }


}
