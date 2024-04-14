package edu.elon.ajimenez4.EC2WebServices.controllers;

import edu.elon.ajimenez4.EC2WebServices.models.EC2DTO;
import edu.elon.ajimenez4.EC2WebServices.services.Ec2DTOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("ec2automation/instance")
public class Ec2Controller {

    @Autowired
    private Ec2DTOService ec2Service;

   @GetMapping()
    public EC2DTO createEc2Instance(@RequestParam String name, @RequestParam String instanceType) {
    return this.ec2Service.createEc2Instance(name, instanceType);
    }

    @GetMapping("/list")
    public List<EC2DTO> getEc2List(){
        return this.ec2Service.getAllInstances();
    }

    @GetMapping("/{instanceId}")
    public EC2DTO getEc2Instance(@PathVariable String instanceId){
        return this.ec2Service.getInstance(instanceId);
    }

    @GetMapping("/{instanceId}/start")
    public EC2DTO startEc2Instance(@PathVariable String instanceId){
        return this.ec2Service.startInstance(instanceId);
    }

    @GetMapping("/{instanceId}/stop")
    public EC2DTO stopEc2Instance(@PathVariable String instanceId){
        return this.ec2Service.stopInstance(instanceId);
    }

    @GetMapping("/{instanceId}/terminate")
    public EC2DTO terminateEc2Instance(@PathVariable String instanceId){
        return this.ec2Service.terminateInstance(instanceId);
    }

}
