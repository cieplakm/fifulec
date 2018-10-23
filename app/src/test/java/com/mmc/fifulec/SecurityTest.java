package com.mmc.fifulec;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class SecurityTest {

    @Test
    public void UU() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        Security security = new Security();
        security.secure("ble");
    }
}