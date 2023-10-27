package com.my.application.demo.a.pplication.Otp_handling_and_cachemanagement;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

    @Service
    public class VerficationToken_Service {
        @Autowired
        private CacheManager cacheManager;

        public String Cache_Name = "verificationTokenCache";

        public void storeVerificationToken(String token, String phone)
        {
            Cache cache = cacheManager.getCache(Cache_Name);
            if (cache != null) {
                cache.put(new net.sf.ehcache.Element(token, phone));
            }
        }

        public String getPhoneForVerificationToken(String token)
        {
            Cache cache = cacheManager.getCache(Cache_Name);
            if (cache != null) {
                Element element = cache.get(token);
                if (element != null) {
                    return (String) element.getObjectValue();
                }
            }
            return null;
        }
    }

    /*    public void removeVerificationToken(String verificationToken) {
            Cache verificationTokenCache = cacheManager.getCache(VERIFICATION_TOKEN_CACHE_NAME);
            if (verificationTokenCache != null) {
                verificationTokenCache.remove(verificationToken);
            }
        }

        public boolean validateVerificationToken(String verificationToken) {
            Cache verificationTokenCache = cacheManager.getCache(VERIFICATION_TOKEN_CACHE_NAME);
            if (verificationTokenCache != null) {
                return verificationTokenCache.isKeyInCache(verificationToken);
            }
            return false;
        }
   }*/