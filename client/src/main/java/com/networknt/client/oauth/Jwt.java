package com.networknt.client.oauth;

import com.networknt.config.Config;
import com.networknt.utility.StringUtils;

import java.util.*;

/**
 * a model class represents a JWT mostly for caching usage so that we don't need to decrypt jwt string to get info.
 * it will load config from client.yml/oauth/token
 */
public class Jwt {
    private String jwt;    // the cached jwt token for client credentials grant type
    private long expire;   // jwt expire time in millisecond so that we don't need to parse the jwt.
    private volatile boolean renewing = false;
    private volatile long expiredRetryTimeout;
    private volatile long earlyRetryTimeout;
    private Set<String> scopes = new HashSet<>();
    private Key key;

    private static long tokenRenewBeforeExpired;
    private static long expiredRefreshRetryDelay;
    private static long earlyRefreshRetryDelay;

    static final String OAUTH = "oauth";
    static final String TOKEN = "token";
    static final String TOKEN_RENEW_BEFORE_EXPIRED = "tokenRenewBeforeExpired";
    static final String EXPIRED_REFRESH_RETRY_DELAY = "expiredRefreshRetryDelay";
    static final String EARLY_REFRESH_RETRY_DELAY = "earlyRefreshRetryDelay";
    public static final String CLIENT_CONFIG_NAME = "client";

    public Jwt() {
        Map<String, Object> clientConfig = Config.getInstance().getJsonMapConfig(CLIENT_CONFIG_NAME);
        if(clientConfig != null) {
            Map<String, Object> oauthConfig = (Map<String, Object>)clientConfig.get(OAUTH);
            if(oauthConfig != null) {
                Map<String, Object> tokenConfig = (Map<String, Object>)oauthConfig.get(TOKEN);
                tokenRenewBeforeExpired = (Integer) tokenConfig.get(TOKEN_RENEW_BEFORE_EXPIRED);
                expiredRefreshRetryDelay = (Integer)tokenConfig.get(EXPIRED_REFRESH_RETRY_DELAY);
                earlyRefreshRetryDelay = (Integer)tokenConfig.get(EARLY_REFRESH_RETRY_DELAY);
            }
        }
    }

    public Jwt(Key key) {
        this();
        this.key = key;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    public boolean isRenewing() {
        return renewing;
    }

    public void setRenewing(boolean renewing) {
        this.renewing = renewing;
    }

    public long getExpiredRetryTimeout() {
        return expiredRetryTimeout;
    }

    public void setExpiredRetryTimeout(long expiredRetryTimeout) {
        this.expiredRetryTimeout = expiredRetryTimeout;
    }

    public long getEarlyRetryTimeout() {
        return earlyRetryTimeout;
    }

    public void setEarlyRetryTimeout(long earlyRetryTimeout) {
        this.earlyRetryTimeout = earlyRetryTimeout;
    }

    public static long getTokenRenewBeforeExpired() {
        return tokenRenewBeforeExpired;
    }

    public static void setTokenRenewBeforeExpired(long tokenRenewBeforeExpired) {
        Jwt.tokenRenewBeforeExpired = tokenRenewBeforeExpired;
    }

    public static long getExpiredRefreshRetryDelay() {
        return expiredRefreshRetryDelay;
    }

    public static void setExpiredRefreshRetryDelay(long expiredRefreshRetryDelay) {
        Jwt.expiredRefreshRetryDelay = expiredRefreshRetryDelay;
    }

    public static long getEarlyRefreshRetryDelay() {
        return earlyRefreshRetryDelay;
    }

    public static void setEarlyRefreshRetryDelay(long earlyRefreshRetryDelay) {
        Jwt.earlyRefreshRetryDelay = earlyRefreshRetryDelay;
    }

    public Set<String> getScopes() {
        return scopes;
    }

    public void setScopes(Set<String> scopes) {
        this.scopes = scopes;
    }

    public void setScopes(String scopesStr) {
        this.scopes = this.scopes == null ? new HashSet() : this.scopes;
        if(StringUtils.isNotBlank(scopesStr)) {
            scopes.addAll(Arrays.asList(scopesStr.split("(\\s)+")));
        }
    }

    public Key getKey() {
        return key;
    }

    public static class Key {
        private Set<String> scopes;
        private String serviceId;

        @Override
        public int hashCode() {
            return Objects.hash(scopes, serviceId);
        }

        @Override
        public boolean equals(Object obj) {
            return hashCode() == obj.hashCode();
        }

        public Key(Set<String> scopes) {
            this.scopes = scopes;
        }

        public Key(String serviceId) {
            this.serviceId = serviceId;
        }

        public Key() {
            this.scopes = new HashSet<>();
        }

        public Set<String> getScopes() {
            return scopes;
        }

        public String getServiceId() {
            return serviceId;
        }
    }
}
