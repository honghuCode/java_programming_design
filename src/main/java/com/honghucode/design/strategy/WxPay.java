package com.honghucode.design.strategy;

import org.springframework.stereotype.Service;

@Service
public class WxPay implements IPay {

    @Override
    public boolean support(String payStyle) {
        return "Weixin".equals(payStyle);
    }

    @Override
    public void pay() {
        System.out.println("这是微信支付");
    }
}
