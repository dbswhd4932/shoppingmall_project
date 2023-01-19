package com.project.shop;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CicdHealthCkeck {

    @GetMapping("/hello")
    public String hello() {
        return "When SpringbootTest is fail, cicd is not effect and success ";
    }
}
