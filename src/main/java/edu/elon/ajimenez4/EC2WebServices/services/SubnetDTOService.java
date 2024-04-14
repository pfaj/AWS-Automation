package edu.elon.ajimenez4.EC2WebServices.services;

import edu.elon.ajimenez4.EC2WebServices.models.RouteTableDTO;
import edu.elon.ajimenez4.EC2WebServices.models.RoutesDTO;
import edu.elon.ajimenez4.EC2WebServices.models.SubnetDTO;
import edu.elon.ajimenez4.EC2WebServices.services.aws.AWSSubnetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ec2.model.Subnet;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubnetDTOService {
    @Autowired
    private AWSSubnetService awsSubnetService;

    public SubnetDTO createSubnet(String vpcId, String cidrBlock, boolean isPublic) {
        Subnet subnet = awsSubnetService.createSubnet(vpcId, cidrBlock, isPublic);

        return convertSubnetToDTO(subnet);
    }

    private SubnetDTO convertSubnetToDTO(Subnet subnet) {
        SubnetDTO subnetDTO = new SubnetDTO();
        RouteTableDTO routeTableDTO = new RouteTableDTO();
        RoutesDTO routesDTO = new RoutesDTO();

        subnetDTO.setSubnetId(subnet.subnetId());
        subnetDTO.setSubnetArn(subnet.subnetArn());
        subnetDTO.setCidrBlock(subnet.cidrBlock());
        subnetDTO.setAvailabilityZone(subnet.availabilityZone());
        subnetDTO.setVisibility(subnet.mapPublicIpOnLaunch() ? "Public" : "Private");

        routesDTO.setNatGatewayId(awsSubnetService.getNatGatewayId(subnet.subnetId()));
        routeTableDTO.setRouteTableId(awsSubnetService.getRouteTableId(subnet.subnetId()));
        routesDTO.setGatewayId(awsSubnetService.getGatewayId(subnet.subnetId()));
        routesDTO.setDestinationCidrBlock(subnet.cidrBlock());

        routeTableDTO.setRoutes(routesDTO);
        subnetDTO.setRouteTable(routeTableDTO);

        return subnetDTO;

    }

    private List<SubnetDTO> convertSubnetListToDTO(List<Subnet> subnets) {
        return subnets.stream()
                .map(this::convertSubnetToDTO)
                .collect(Collectors.toList());
    }

    public SubnetDTO getSubnet(String subnetId) {
        return convertSubnetToDTO(awsSubnetService.getSubnet(subnetId));
    }

    public List<SubnetDTO> listSubnets(String vpcId) {
        List<Subnet> subnets = awsSubnetService.listSubnets(vpcId);
        return convertSubnetListToDTO(subnets);
    }

    public void deleteSubnet(String vpcId,String subnetId) {
        awsSubnetService.deleteSubnet(vpcId,subnetId);
    }

}
