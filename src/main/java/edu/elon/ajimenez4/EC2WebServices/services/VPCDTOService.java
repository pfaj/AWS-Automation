package edu.elon.ajimenez4.EC2WebServices.services;

import edu.elon.ajimenez4.EC2WebServices.models.VPCDTO;
import edu.elon.ajimenez4.EC2WebServices.services.aws.AWSVPCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ec2.model.Vpc;

import java.util.List;

@Service
public class VPCDTOService {
    @Autowired
    private AWSVPCService awsVPCService;
    @Autowired
    private SubnetDTOService subnetDTOService;

    public VPCDTO convertVPCDTO(Vpc vpc) {
        VPCDTO vpcDTO = new VPCDTO();
        vpcDTO.setVpcId(vpc.vpcId());
        vpcDTO.setCidrBlock(vpc.cidrBlock());
        vpcDTO.setSubnets(subnetDTOService.listSubnets(vpc.vpcId()));
        return vpcDTO;
    }

    public VPCDTO createVpc(String cidrBlock) {
        Vpc vpc = awsVPCService.createVPC(cidrBlock);
        return convertVPCDTO(vpc);
    }

    public List<VPCDTO> listVPC() {
        return awsVPCService.listVPCs().stream().map(this::convertVPCDTO).toList();
    }

    public VPCDTO getVPC(String vpcId) {
        return convertVPCDTO(awsVPCService.getVPC(vpcId));
    }
}
