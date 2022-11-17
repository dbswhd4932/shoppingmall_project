package com.project.shop.goods.controller;

import com.project.shop.goods.domain.Option;
import com.project.shop.goods.service.Impl.OptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OptionController {

    private final OptionService optionService;

    @PostMapping("/option")
    @ResponseStatus(HttpStatus.CREATED)
    public void createOption(@RequestBody @Valid Option option) {
        optionService.createOption(option);
    }
}
