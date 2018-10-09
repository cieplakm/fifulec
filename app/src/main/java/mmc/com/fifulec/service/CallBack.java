package mmc.com.fifulec.service;

public interface CallBack<T> {
    void response(T t);
    void error();
}
