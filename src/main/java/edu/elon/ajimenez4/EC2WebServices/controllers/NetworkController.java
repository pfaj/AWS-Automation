package edu.elon.ajimenez4.EC2WebServices.controllers;

import edu.elon.ajimenez4.EC2WebServices.models.SubnetDTO;
import edu.elon.ajimenez4.EC2WebServices.models.VPCDTO;
import edu.elon.ajimenez4.EC2WebServices.services.SubnetDTOService;
import edu.elon.ajimenez4.EC2WebServices.services.VPCDTOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("ec2automation/network")
public class NetworkController {
    @Autowired
    private VPCDTOService vpcService;
    @Autowired
    private SubnetDTOService subnetService;

    @GetMapping("/list")
    public List<VPCDTO> getVpcList(){
        return this.vpcService.listVPC();
    }

    @GetMapping("/{vpcId}")
    public VPCDTO getVpc(@PathVariable String vpcId){
        return this.vpcService.getVPC(vpcId);
    }

    @PostMapping()
    public VPCDTO createVpc(@RequestParam String cidrBlock) {
        return this.vpcService.createVpc(cidrBlock);
    }

    @PostMapping("/{vpcId}/subnet")
    public SubnetDTO createSubnet(@PathVariable String vpcId, @RequestParam String cidrBlock, @RequestParam boolean isPublic) {
        return this.subnetService.createSubnet(vpcId, cidrBlock, isPublic);
    }

    @GetMapping("/{vpcId}/subnet/list")
    public List<SubnetDTO> getSubnets(@PathVariable String vpcId) {
        return this.subnetService.listSubnets(vpcId);
    }

    @GetMapping("/{vpcId}/subnet/{subnetId}")
    public SubnetDTO getSubnet(@PathVariable String vpcId, @PathVariable String subnetId) {
        return this.subnetService.getSubnet(subnetId);
    }

    @DeleteMapping("/{vpcId}/subnet/{subnetId}")
    public void deleteSubnet(@PathVariable String vpcId, @PathVariable String subnetId) {
        this.subnetService.deleteSubnet(vpcId, subnetId);
    }
}