package com.honghucode.design.service;

import com.honghucode.design.strategy.IPay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PayService {

    @Autowired
    private List<IPay> payList;

    public void pay(String payStyle) {
        for (IPay iPay : payList) {
            if(iPay.support(payStyle)) {
                iPay.pay();
            }
        }
    }
}
