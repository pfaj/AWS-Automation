package edu.elon.ajimenez4.EC2WebServices.services.aws;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

import java.util.List;

@Service
public class AWSSubnetService {
    private Ec2Client ec2Client;

    public AWSSubnetService(Ec2Client ec2Client) {
        this.ec2Client = ec2Client;
    }

    public Subnet createSubnet(String vpcId, String cidrBlock, boolean isPublic) {
        CreateSubnetRequest subnetRequest = CreateSubnetRequest.builder()
                .vpcId(vpcId)
                .cidrBlock(cidrBlock)
                .build();
        Subnet subnet = ec2Client.createSubnet(subnetRequest).subnet();

        if (isPublic) {
            createPublicSubnet(subnet);
        } else {
            createPrivateSubnet(subnet);
        }

        return subnet;
    }

    private void createPublicSubnet(Subnet subnet) {
        CreateRouteTableRequest routeTableRequest = CreateRouteTableRequest.builder()
                .vpcId(subnet.vpcId())
                .build();
        RouteTable routeTable = ec2Client.createRouteTable(routeTableRequest).routeTable();


        CreateInternetGatewayRequest igwRequest = CreateInternetGatewayRequest.builder().build();
        InternetGateway internetGateway = ec2Client.createInternetGateway(igwRequest).internetGateway();

        AttachInternetGatewayRequest attachIgwRequest = AttachInternetGatewayRequest.builder()
                .internetGatewayId(internetGateway.internetGatewayId())
                .vpcId(subnet.vpcId())
                .build();
        ec2Client.attachInternetGateway(attachIgwRequest);


        AssociateRouteTableRequest associateRouteTableRequest = AssociateRouteTableRequest.builder()
                .subnetId(subnet.subnetId())
                .routeTableId(routeTable.routeTableId())
                .build();
        ec2Client.associateRouteTable(associateRouteTableRequest);

        CreateRouteRequest routeRequest = CreateRouteRequest.builder()
                .routeTableId(routeTable.routeTableId())
                .destinationCidrBlock("0.0.0.0/0")
                .gatewayId(internetGateway.internetGatewayId())
                .build();
        ec2Client.createRoute(routeRequest);
    }

    private void createPrivateSubnet(Subnet subnet) {
        CreateRouteTableRequest routeTableRequest = CreateRouteTableRequest.builder()
                .vpcId(subnet.vpcId())
                .build();
        RouteTable routeTable = ec2Client.createRouteTable(routeTableRequest).routeTable();

        AllocateAddressRequest allocateRequest = AllocateAddressRequest.builder()
                .domain(DomainType.VPC)
                .build();
        AllocateAddressResponse allocateResponse = ec2Client.allocateAddress(allocateRequest);
        String allocationId = allocateResponse.allocationId();

        CreateNatGatewayRequest natGatewayRequest = CreateNatGatewayRequest.builder()
                .subnetId(subnet.subnetId())
                .allocationId(allocationId)
                .build();
        NatGateway natGateway = ec2Client.createNatGateway(natGatewayRequest).natGateway();

        ec2Client.waiter().waitUntilNatGatewayAvailable(ngw -> ngw.natGatewayIds(natGateway.natGatewayId()));

        AssociateRouteTableRequest associateRouteTableRequest = AssociateRouteTableRequest.builder()
                .subnetId(subnet.subnetId())
                .routeTableId(routeTable.routeTableId())
                .build();
        ec2Client.associateRouteTable(associateRouteTableRequest);

        CreateRouteRequest routeRequest = CreateRouteRequest.builder()
                .routeTableId(routeTable.routeTableId())
                .destinationCidrBlock("0.0.0.0/0")
                .natGatewayId(natGateway.natGatewayId())
                .build();
        ec2Client.createRoute(routeRequest);
    }

    public void deleteSubnet(String vpcId, String subnetId) {
        Subnet subnet = getSubnet(subnetId);
        if (subnet != null && subnet.vpcId().equals(vpcId)) {
            DescribeNatGatewaysRequest natGatewaysRequest = DescribeNatGatewaysRequest.builder()
                    .filter(Filter.builder().name("subnet-id").values(subnetId).build())
                    .build();
            DescribeNatGatewaysResponse natGatewaysResponse = ec2Client.describeNatGateways(natGatewaysRequest);

            for (NatGateway natGateway : natGatewaysResponse.natGateways()) {
                DeleteNatGatewayRequest deleteNatGatewayRequest = DeleteNatGatewayRequest.builder()
                        .natGatewayId(natGateway.natGatewayId())
                        .build();
                ec2Client.deleteNatGateway(deleteNatGatewayRequest);
            }
            DescribeRouteTablesRequest routeTablesRequest = DescribeRouteTablesRequest.builder()
                    .filters(Filter.builder().name("association.subnet-id").values(subnetId).build())
                    .build();
            DescribeRouteTablesResponse routeTablesResponse = ec2Client.describeRouteTables(routeTablesRequest);

            for (RouteTable routeTable : routeTablesResponse.routeTables()) {
                for (RouteTableAssociation association : routeTable.associations()) {
                    if (association.subnetId().equals(subnetId)) {
                        DisassociateRouteTableRequest disassociateRequest = DisassociateRouteTableRequest.builder()
                                .associationId(association.routeTableAssociationId())
                                .build();
                        ec2Client.disassociateRouteTable(disassociateRequest);
                    }
                }

                DeleteRouteTableRequest deleteRouteTableRequest = DeleteRouteTableRequest.builder()
                        .routeTableId(routeTable.routeTableId())
                        .build();
                ec2Client.deleteRouteTable(deleteRouteTableRequest);
            }

            DeleteSubnetRequest request = DeleteSubnetRequest.builder()
                    .subnetId(subnetId)
                    .build();
            ec2Client.deleteSubnet(request);
        } else {
            throw new IllegalArgumentException("The subnet does not belong to the VPC");
        }
    }

    public Subnet getSubnet(String subnetId) {
        DescribeSubnetsRequest request = DescribeSubnetsRequest.builder()
                .subnetIds(subnetId)
                .build();
        DescribeSubnetsResponse response = ec2Client.describeSubnets(request);
        return response.subnets().stream().filter(subnet -> subnet.subnetId().equals(subnetId)).findFirst().orElse(null);
    }

    public List<Subnet> listSubnets(String vpcId) {
        DescribeSubnetsResponse response = ec2Client.describeSubnets();
        List<Subnet> listSubnets =  response.subnets().stream()
                .filter(subnet -> subnet.vpcId().equals(vpcId))
                .flatMap(subnet -> response.subnets().stream())
                .toList();
        return listSubnets;
    }

    public String getSubnetArn(String subnetId) {
        DescribeSubnetsRequest request = DescribeSubnetsRequest.builder()
                .subnetIds(subnetId)
                .build();
        DescribeSubnetsResponse response = ec2Client.describeSubnets(request);
        return response.subnets().stream().filter(subnet -> subnet.subnetId().equals(subnetId)).findFirst().orElse(null).subnetArn();
    }

    public String getNatGatewayId(String subnetId) {
        DescribeNatGatewaysRequest request = DescribeNatGatewaysRequest.builder()
                .filter(Filter.builder().name("subnet-id").values(subnetId).build())
                .build();
        DescribeNatGatewaysResponse response = ec2Client.describeNatGateways(request);
        NatGateway natGateway = response.natGateways().stream()
                .filter(ng -> ng.subnetId().equals(subnetId))
                .findFirst().orElse(null);
        return natGateway != null ? natGateway.natGatewayId() : null;
    }

    public String getRouteTableId(String subnetId) {
        DescribeRouteTablesRequest request = DescribeRouteTablesRequest.builder()
                .filters(Filter.builder().name("association.subnet-id").values(subnetId).build())
                .build();
        DescribeRouteTablesResponse response = ec2Client.describeRouteTables(request);
        RouteTable routeTable = response.routeTables().stream()
                .filter(rt -> rt.associations().stream()
                        .anyMatch(association -> association.subnetId().equals(subnetId)))
                .findFirst().orElse(null);
        return routeTable != null ? routeTable.routeTableId() : null;
    }

    public String getGatewayId(String subnetId) {
        DescribeRouteTablesRequest request = DescribeRouteTablesRequest.builder()
                .filters(Filter.builder().name("association.subnet-id").values(subnetId).build())
                .build();
        DescribeRouteTablesResponse response = ec2Client.describeRouteTables(request);
        RouteTable routeTable = response.routeTables().stream()
                .filter(rt -> rt.associations().stream()
                        .anyMatch(association -> association.subnetId().equals(subnetId)))
                .findFirst().orElse(null);
        if (routeTable != null) {
            Route route = routeTable.routes().stream()
                    .filter(r -> r.destinationCidrBlock().equals("0.0.0.0/0"))
                    .findFirst().orElse(null);
            return route != null ? route.gatewayId() : null;
        }
        return null;
    }
}
