package com.mmc.fifulec;

import com.google.android.gms.common.util.Hex;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Security {

    public String secure(String p) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");

        byte[] pBytes = p.getBytes("UTF-8");

        byte[] digest = md5.digest(pBytes);

        return  Hex.bytesToStringUppercase(digest);
    }
}
