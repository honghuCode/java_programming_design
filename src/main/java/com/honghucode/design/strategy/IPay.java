package com.honghucode.design.strategy;

public interface IPay {

    boolean support(String payStyle);

    void pay();

}
