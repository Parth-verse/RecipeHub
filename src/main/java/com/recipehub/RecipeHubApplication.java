package com.recipehub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RecipeHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecipeHubApplication.class, args);
        System.out.println("\n=================================================");
        System.out.println("  RecipeHub Application is running successfully! ");
        System.out.println("  Server URL: http://localhost:8085               ");
        System.out.println("=================================================\n");
    }
}
