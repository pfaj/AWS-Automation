package edu.elon.ajimenez4.EC2WebServices.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class SubnetDTO {
    private String subnetId;
    private String subnetArn;
    private String availabilityZone;
    private String cidrBlock;
    private String visibility;
    private RouteTableDTO routeTable;
}
