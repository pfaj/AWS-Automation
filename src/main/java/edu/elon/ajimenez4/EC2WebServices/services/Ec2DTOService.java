package edu.elon.ajimenez4.EC2WebServices.services;

import edu.elon.ajimenez4.EC2WebServices.models.EC2DTO;
import edu.elon.ajimenez4.EC2WebServices.models.IpDetailDTO;
import edu.elon.ajimenez4.EC2WebServices.services.aws.AWSEC2Services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.Tag;

import java.util.ArrayList;
import java.util.List;

@Service
public class Ec2DTOService {
    @Autowired
    private AWSEC2Services awsEc2Service;

    private EC2DTO convertEC2ToDTO(Instance ec2Client){
        EC2DTO ec2DTO = new EC2DTO();
        IpDetailDTO ipDetailDTO = new IpDetailDTO();
        //I did not realize we did not need to get the instance name for this assignment until I was almost done
        ec2DTO.setName(getInstanceName(ec2Client));
        ec2DTO.setInstanceId(ec2Client.instanceId());
        ec2DTO.setVpcId(ec2Client.vpcId());
        ec2DTO.setSubnetId(ec2Client.subnetId());
        ec2DTO.setState(ec2Client.state().name().toString());
            ipDetailDTO.setPublicIPAddress(ec2Client.publicIpAddress());
            ipDetailDTO.setPublicDNSName(ec2Client.publicDnsName());
            ipDetailDTO.setPrivateIPAddress(ec2Client.privateIpAddress());
            ipDetailDTO.setPrivateDNSName(ec2Client.privateDnsName());
        ec2DTO.setIpDetail(ipDetailDTO);
        ec2DTO.setPlatform(ec2Client.platformDetails());
        ec2DTO.setInstanceType(ec2Client.instanceType().toString());
        ec2DTO.setLaunchTime(ec2Client.launchTime().toString());
        return ec2DTO;
    }

    private String getInstanceName(Instance instance) {
    String instanceName = null;
    for (Tag tag : instance.tags()) {
        if ("Name".equals(tag.key())) {
            instanceName = tag.value();
            break;
        }
    }
    return instanceName;
}

    private List<EC2DTO> convertInstanceList(List<Instance> EC2List)
    {
        //Gets each instance and converts it to a DTO Object
        List<EC2DTO> ec2List = new ArrayList<EC2DTO>();
        EC2List.forEach(instance -> {
            ec2List.add(convertEC2ToDTO(instance));
        });

        return ec2List;
    }

    private EC2DTO convertInstance(String instanceId){
        Instance ec2 = awsEc2Service.getInstance(instanceId);
        return convertEC2ToDTO(ec2);
    }

    public EC2DTO createEc2Instance(String name, String instanceType){
        return convertEC2ToDTO(awsEc2Service.createEC2Instance(name, instanceType));
    }

    public EC2DTO startInstance(String instanceId){
        Instance instance = awsEc2Service.startInstance(instanceId);
        return convertEC2ToDTO(instance);
    }

    public EC2DTO stopInstance(String instanceId){
        Instance instance = awsEc2Service.stopInstance(instanceId);
        return convertEC2ToDTO(instance);
    }

    public EC2DTO terminateInstance(String instanceId){
        Instance instance = awsEc2Service.terminateInstance(instanceId);
        return convertEC2ToDTO(instance);
    }

    public List<EC2DTO> getAllInstances(){
        List<Instance> instances = awsEc2Service.getAllInstances();
        return convertInstanceList(instances);
    }

    public EC2DTO getInstance(String instanceId) {
        Instance instance = awsEc2Service.getInstance(instanceId);
        return convertEC2ToDTO(instance);
    }
}