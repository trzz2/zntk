package com.zntk.service;

import com.zntk.dto.LoginRequest;
import com.zntk.dto.LoginResponse;
import com.zntk.dto.RegisterRequest;
import com.zntk.entity.User;

public interface AuthService {

    Long register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    Boolean logout(String token);

    User currentUser();
}
