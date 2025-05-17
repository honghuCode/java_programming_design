package com.honghucode.design.strategy;


import org.springframework.stereotype.Service;

@Service
public class AliPay implements IPay {
    @Override
    public boolean support(String payStyle) {
        return "alipay".equals(payStyle);
    }

    @Override
    public void pay() {
        System.out.println("这是淘宝支付");
    }
}
