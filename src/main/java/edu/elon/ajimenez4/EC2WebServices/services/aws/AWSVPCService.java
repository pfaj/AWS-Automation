package edu.elon.ajimenez4.EC2WebServices.services.aws;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

import java.util.List;

@Service
public class AWSVPCService {
    private final Logger logger = LoggerFactory.getLogger(AWSVPCService.class);
    @Getter
    private Ec2Client ec2Client;

    public AWSVPCService(Ec2Client ec2Client) {
        this.ec2Client = ec2Client;
    }

    public Vpc createVPC(String cidrBlock) {
        CreateVpcRequest request = CreateVpcRequest.builder()
                .cidrBlock(cidrBlock)
                .build();
        CreateVpcResponse response = ec2Client.createVpc(request);
        return response.vpc();
    }

    public List<Vpc> listVPCs() {
        DescribeVpcsRequest request = DescribeVpcsRequest.builder().build();
        DescribeVpcsResponse response = ec2Client.describeVpcs(request);
        return response.vpcs();
    }

    public Vpc getVPC(String vpcId) {
        DescribeVpcsRequest request = DescribeVpcsRequest.builder()
                .vpcIds(vpcId)
                .build();
        DescribeVpcsResponse response = ec2Client.describeVpcs(request);
        return response.vpcs().get(0);
    }
}
