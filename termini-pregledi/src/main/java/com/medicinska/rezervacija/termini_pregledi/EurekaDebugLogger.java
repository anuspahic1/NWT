package com.medicinska.rezervacija.termini_pregledi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

@Component
public class EurekaDebugLogger {

    @Autowired
    private DiscoveryClient discoveryClient;

    @PostConstruct
    public void logInstances() {
        System.out.println("➡ Registrirane instance za TERMINI-PREGLEDI:");
        discoveryClient.getInstances("TERMINI-PREGLEDI")
                .forEach(instance -> System.out.println("   URI: " + instance.getUri()));
    }
}
