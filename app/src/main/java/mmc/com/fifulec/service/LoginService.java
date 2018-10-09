package mmc.com.fifulec.service;

import javax.inject.Inject;

import mmc.com.fifulec.repository.SecurityRepository;
import mmc.com.fifulec.di.AppScope;

@AppScope
public class LoginService {

    private SecurityRepository securityRepository;

    @Inject
    public LoginService(SecurityRepository securityRepository) {
        this.securityRepository = securityRepository;
    }
}
