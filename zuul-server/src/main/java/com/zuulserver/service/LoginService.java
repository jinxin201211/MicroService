package com.zuulserver.service;

import com.zuulserver.comm.Response;
import com.zuulserver.dto.Permissions;
import com.zuulserver.dto.User;

import java.util.List;

public interface LoginService {
    User getUserByName(String getMapByName);

    List<Permissions> getPermissions(String userid);

    Response clearCache(String userid);

    List<Permissions> getAllPermissions();
}
