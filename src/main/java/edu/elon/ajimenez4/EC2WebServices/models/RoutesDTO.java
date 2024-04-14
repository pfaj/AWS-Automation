package edu.elon.ajimenez4.EC2WebServices.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class RoutesDTO {
    private String destinationCidrBlock;
    private String gatewayId;
    private String natGatewayId;
}
