package com.project.shop;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CicdHealthCkeck {

    @GetMapping("/hello")
    public String hello() {
        return "PR TEST gogo! ";
    }
}
