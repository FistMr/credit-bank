package com.puchkov.deal.controller;

import com.puchkov.deal.controller.dto.LoanStatementRequestDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/deal")
public class DealController {

    @PostMapping("/statement")
    public String getList(@RequestBody LoanStatementRequestDto requestDto){
        return "nice";
    }
}
