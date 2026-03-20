package com.example.back.init;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class initSetup implements ApplicationRunner{
    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("Auto Start: ApplicationRunner");
        
    }

}
