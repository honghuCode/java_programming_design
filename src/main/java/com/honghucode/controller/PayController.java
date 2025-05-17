package com.honghucode.controller;

import com.honghucode.design.service.PayService;
import com.honghucode.design.strategy.IPay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PayController {

    @Autowired
    private PayService payService;

    @GetMapping("/pay")
    public String pay(@RequestParam("payStyle") String payStyle) {
        payService.pay(payStyle);
        return "success";
    }
}
