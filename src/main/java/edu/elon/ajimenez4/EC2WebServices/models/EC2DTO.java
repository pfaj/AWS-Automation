package edu.elon.ajimenez4.EC2WebServices.models;

import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class EC2DTO {
    private String name;
    private String instanceId;
    private String vpcId;
    private String subnetId;
    private String state;
    private IpDetailDTO ipDetail;
    private String platform;
    private String instanceType;
    private String launchTime;
}
