package edu.elon.ajimenez4.EC2WebServices.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class IpDetailDTO {
    private String  privateIPAddress;
    private String  privateDNSName;
    private String  publicIPAddress;
    private String  publicDNSName;
}
