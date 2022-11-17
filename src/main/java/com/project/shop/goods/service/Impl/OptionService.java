package com.project.shop.goods.service.Impl;

import com.project.shop.goods.domain.Option;
import com.project.shop.goods.repository.OptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OptionService {

    private final OptionRepository optionRepository;

    public void createOption(Option option) {
        optionRepository.save(option);
    }
}
