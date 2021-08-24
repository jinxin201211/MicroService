package com.zuulserver.service;

import com.zuulserver.comm.Response;
import com.zuulserver.dto.Permissions;
import com.zuulserver.dto.Role;
import com.zuulserver.dto.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LoginServiceImpl implements LoginService {

    private static final Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);

    @Override
    public User getUserByName(String getMapByName) {
        return getMapByName(getMapByName);
    }

    /**
     * 模拟数据库查询
     *
     * @param userName 用户名
     * @return User
     */
    private User getMapByName(String userName) {
        Permissions permissions1 = new Permissions("1", "query");
        Permissions permissions2 = new Permissions("2", "add");
        Set<Permissions> permissionsSet = new HashSet<>();
        permissionsSet.add(permissions1);
        permissionsSet.add(permissions2);
        Role role = new Role("1", "admin", permissionsSet);
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(role);
        User user = new User("1", "wsl", "123456", roleSet);
        Map<String, User> map = new HashMap<>();
        map.put(user.getUserName(), user);

        Set<Permissions> permissionsSet1 = new HashSet<>();
        permissionsSet1.add(permissions1);
        Role role1 = new Role("2", "user", permissionsSet1);
        Set<Role> roleSet1 = new HashSet<>();
        roleSet1.add(role1);
        User user1 = new User("2", "zhangsan", "123456", roleSet1);
        map.put(user1.getUserName(), user1);
        return map.get(userName);
    }


    private static final String CACHE_NAME = "SysMenu";

    @Override
    @Cacheable(value = CACHE_NAME, key = "'user_'+#userid")
    public List<Permissions> getPermissions(String userid) {
        logger.info("根据用户ID[" + userid + "]查询用户权限，并缓存！");
        User user = getMapByName(userid);
        Set<Permissions> permissions = new HashSet<>();
        for (Role role : user.getRoles()) {
            permissions.addAll(role.getPermissions());
        }
        return new ArrayList<>(permissions);
    }

    @Override
    @CacheEvict(value = CACHE_NAME, key = "'user_'+#userid", beforeInvocation = true)
    public Response clearCache(String userid) {
        logger.info("根据用户ID[" + userid + "]清除权限缓存！");
        return new Response().success();
    }

    @Override
    public List<Permissions> getAllPermissions() {
        List<Permissions> permissions = new ArrayList<>();
        permissions.add(new Permissions("1", "query"));
        permissions.add(new Permissions("2", "add"));
        permissions.add(new Permissions("3", "/provider1/test/get"));
        permissions.add(new Permissions("3", "/provider2/test/get"));
        permissions.add(new Permissions("3", "/provider3/test/get"));
        return permissions;
    }
}
