package com.mmc.fifulec.service;

public interface CallBack<T> {
    void response(T t);
    void error();
}
