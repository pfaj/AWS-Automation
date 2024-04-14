package edu.elon.ajimenez4.EC2WebServices.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class EnvironmentDTO {
    private List<VPCDTO> networks;
    private List<EC2DTO> instances;
}
