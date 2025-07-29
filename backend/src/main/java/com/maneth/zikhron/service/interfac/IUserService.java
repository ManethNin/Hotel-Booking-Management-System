package com.maneth.zikhron.service.interfac;

import com.maneth.zikhron.dto.LoginRequest;
import com.maneth.zikhron.dto.Response;
import com.maneth.zikhron.entity.User;

public interface IUserService {
    Response register(User user);

    Response login(LoginRequest loginRequest);

    Response getAllUsers();

    Response getUserBookingHistory(String userId);

    Response deleteUser(String userId);

    Response getUserById(String userId);

    Response getMyInfo(String email);

}
