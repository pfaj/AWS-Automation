package edu.elon.ajimenez4.EC2WebServices.controllers;

import edu.elon.ajimenez4.EC2WebServices.models.EnvironmentDTO;
import edu.elon.ajimenez4.EC2WebServices.services.EnvironmentDTOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("ec2automation/environment")
public class EnvironmentController {
    @Autowired
    private EnvironmentDTOService environmentService;

    @GetMapping()
    public EnvironmentDTO getEnvironment(){
        return this.environmentService.getEnvironmentDTO();
    }

    @PostMapping()
    public EnvironmentDTO createEnvironment(@RequestParam String name, @RequestParam String instanceType){
        return this.environmentService.createEnvironmentDTO(name, instanceType);
    }

}
