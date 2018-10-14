package com.mmc.fifulec.service;

import javax.inject.Inject;

import com.mmc.fifulec.repository.SecurityRepository;
import com.mmc.fifulec.di.AppScope;

@AppScope
public class LoginService {

    private SecurityRepository securityRepository;

    @Inject
    public LoginService(SecurityRepository securityRepository) {
        this.securityRepository = securityRepository;
    }
}
