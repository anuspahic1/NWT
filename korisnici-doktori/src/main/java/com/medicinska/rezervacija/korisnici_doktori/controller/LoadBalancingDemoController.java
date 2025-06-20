package com.medicinska.rezervacija.korisnici_doktori.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/lb-demo")
public class LoadBalancingDemoController {

    @Autowired
    private RestTemplate loadBalancedRestTemplate;

    @Value("${server.port}")
    private String serverPort;

    private static final String SERVICE_ID = "korisnici-doktori";

    @GetMapping("/call-self")
    public String callSelfViaLoadBalancer() {
        String url = "http://" + SERVICE_ID + "/api/doktori";
        try {
            loadBalancedRestTemplate.getForObject(url, String.class);
            return "Request from instance on port " + serverPort + " successfully routed via load balancer to " + SERVICE_ID + ".";
        } catch (Exception e) {
            return "Request from instance on port " + serverPort + " failed to route via load balancer to " + SERVICE_ID + ": " + e.getMessage();
        }
    }

    @GetMapping("/instance-info")
    public String getInstanceInfo() {
        return "Hello from KORISNICI-DOKTORI instance on port: " + serverPort;
    }
}
