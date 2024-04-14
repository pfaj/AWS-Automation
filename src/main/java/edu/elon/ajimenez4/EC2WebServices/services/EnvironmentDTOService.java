package edu.elon.ajimenez4.EC2WebServices.services;

import edu.elon.ajimenez4.EC2WebServices.models.EC2DTO;
import edu.elon.ajimenez4.EC2WebServices.models.EnvironmentDTO;
import edu.elon.ajimenez4.EC2WebServices.models.VPCDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnvironmentDTOService {
    @Autowired
    private Ec2DTOService ec2DTOService;
    @Autowired
    private VPCDTOService vpcDTOService;
    @Autowired
    private SubnetDTOService subnetDTOService;

    public EnvironmentDTO getEnvironmentDTO(){
        List<VPCDTO> networks = vpcDTOService.listVPC();
        List<EC2DTO> instances = ec2DTOService.getAllInstances();
        return convertToDTO(networks, instances);
    }

    private EnvironmentDTO convertToDTO(List<VPCDTO> networks, List<EC2DTO> instances){
        EnvironmentDTO enviromentDTO = new EnvironmentDTO();
        enviromentDTO.setNetworks(networks);
        enviromentDTO.setInstances(instances);
        return enviromentDTO;
    }

    public EnvironmentDTO createEnvironmentDTO(String name, String instanceType){
        EC2DTO ec2 = ec2DTOService.createEc2Instance(name, instanceType);
        return convertToDTO(vpcDTOService.listVPC(), ec2DTOService.getAllInstances());
    }

}
