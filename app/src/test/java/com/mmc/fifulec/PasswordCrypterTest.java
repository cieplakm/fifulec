package com.mmc.fifulec;

import com.mmc.fifulec.utils.PasswordCrypter;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class PasswordCrypterTest {

    @Test
    public void UU() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        PasswordCrypter passwordCrypter = new PasswordCrypter();
        passwordCrypter.crypt("ble");
    }
}