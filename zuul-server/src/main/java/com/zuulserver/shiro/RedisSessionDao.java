package com.zuulserver.shiro;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.ValidatingSession;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class RedisSessionDao extends AbstractSessionDAO {

    // Session超时时间，单位为毫秒 30分(min)=1800000毫秒(ms)
    private long expireTime = 6*60*60*1000;

    private String sessionPrefix = "stardon_session";

    @Autowired
    private RedisTemplate redisTemplate;

    public RedisSessionDao() {
        super();
    }

    public RedisSessionDao(long expireTime, String sessionPrefix) {
        super();
        if (expireTime > 0) {
            this.expireTime = expireTime;
        }
        if (sessionPrefix != null) {
            this.sessionPrefix = sessionPrefix;
        }
    }

    @Override // 更新session
    public void update(Session session) throws UnknownSessionException {
        if (session == null || session.getId() == null) {
            return;
        }
        //如果会话过期/停止 没必要再更新了
        if (session instanceof ValidatingSession && !((ValidatingSession) session).isValid()) {
            return;
        }
        session.setTimeout(expireTime);
        //System.out.println("===============update================" + session.getId());
        redisTemplate.opsForValue().set(sessionPrefix +"_alive:"+ session.getId(), session, expireTime, TimeUnit.MILLISECONDS);
    }

    @Override // 删除session
    public void delete(Session session) {
        //System.out.println("===============delete================" + session.getId());
        if (null == session) {
            return;
        }
        redisTemplate.opsForValue().getOperations().delete(sessionPrefix +"_alive:"+ session.getId());
    }

    public  void setKickoutSession(Session session)
    {
        Serializable sessionId = this.generateSessionId(session);
        //System.out.println("===============setKickoutSession================" + sessionId);
        redisTemplate.opsForValue().set(sessionPrefix +"_kickout:"+ session.getId(), session, expireTime, TimeUnit.MILLISECONDS);
    }

    public  Session getKickoutSession(Serializable sessionId)
    {
        //System.out.println("===============getKickoutSession================" + sessionId);
        return (Session) redisTemplate.opsForValue().get(sessionPrefix +"_kickout:"+ sessionId);
    }

    public  void deleteKickoutSession(Session session)
    {
        Serializable sessionId = this.generateSessionId(session);
        //System.out.println("===============deleteKickoutSession================" + sessionId);
        redisTemplate.opsForValue().getOperations().delete(sessionPrefix +"_kickout:"+ session.getId());
    }

    @Override
    // 获取活跃的session，可以用来统计在线人数，如果要实现这个功能，可以在将session加入redis时指定一个session前缀，统计的时候则使用keys("session-prefix*")的方式来模糊查找redis中所有的session集合
    public Collection<Session> getActiveSessions() {
       // System.out.println("==============getActiveSessions=================");
        Collection<String> sessions =redisTemplate.keys( sessionPrefix + "_alive:*");
        List<Session> list = new ArrayList<>();
        for (String session : sessions) {
            list.add( (Session) redisTemplate.opsForValue().get(session));
        }
        return list;
    }

    public Integer getActiveSessionsCount() {
       // System.out.println("==============getActiveSessionsCount=================");
        Collection<String> sessions =redisTemplate.keys( sessionPrefix + "_alive:*");
        return sessions.size();
    }

    @Override// 加入session
    protected Serializable doCreate(Session session) {

        Serializable sessionId = this.generateSessionId(session);
        this.assignSessionId(session, sessionId);
       // System.out.println("===============doCreate================" + sessionId);
        redisTemplate.opsForValue().set(sessionPrefix +"_alive:"+ session.getId(), session, expireTime, TimeUnit.MILLISECONDS);
        return sessionId;
    }

    @Override// 读取session
    protected Session doReadSession(Serializable sessionId) {
       // System.out.println("==============doReadSession=================" + sessionId);
        if (sessionId == null) {
            return null;
        }
        return (Session) redisTemplate.opsForValue().get(sessionPrefix +"_alive:"+ sessionId);
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public String getSessionPrefix() {
        return sessionPrefix;
    }

    public void setSessionPrefix(String sessionPrefix) {
        this.sessionPrefix = sessionPrefix;
    }

    public RedisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    @Autowired(required = false)
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        RedisSerializer stringSerializer = new StringRedisSerializer();
        RedisSerializer defaultSerializer = new JdkSerializationRedisSerializer();
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(defaultSerializer);
        this.redisTemplate = redisTemplate;
    }


}

