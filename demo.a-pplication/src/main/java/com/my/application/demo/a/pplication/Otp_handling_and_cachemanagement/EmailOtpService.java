package com.my.application.demo.a.pplication.Otp_handling_and_cachemanagement;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class EmailOtpService
{

    @Autowired
    public CacheManager cacheManager;

    public boolean sendEmail(String email,String emailotp)
    {
        String fromEmail = "gundalokesh2000@gmail.com";
        String emailPassword = "xakg lrov gcod rpfp";

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        javax.mail.Session mailSession = javax.mail.Session.getInstance(properties, new javax.mail.Authenticator() {
            public javax.mail.PasswordAuthentication getPasswordAuthentication() {
                return new javax.mail.PasswordAuthentication(fromEmail, emailPassword);
            }
        });
        try {
            javax.mail.internet.MimeMessage message = new MimeMessage(mailSession);

            message.setFrom(new javax.mail.internet.InternetAddress(fromEmail));
            message.addRecipient(javax.mail.Message.RecipientType.TO, new javax.mail.internet.InternetAddress(email));
            message.setSubject("Your OTP Code");

            System.out.println("emailotp : "+emailotp);

            message.setText("Your OTP code is :" + emailotp);

            javax.mail.Transport.send(message);
            return true; // Email sent successfully
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }
    public String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
    public void cacheEmailOtp(String email, String emailotp)
    {
        Cache cache = cacheManager.getCache("emailOtpCache");
        Element element = new Element(email, emailotp);
        System.out.println("emailOtpCache" + element);
        element.setTimeToLive((int) TimeUnit.MINUTES.toSeconds(5000));
        System.out.println("cache------>"+cache);
        cache.put(element);
    }

    public boolean validateEmailOtp(String email, String emailotp) {
        String cachedValue = getCachedEmailOtp(email);
        System.out.println("Entered email: " + email);
        System.out.println("Entered email OTP: " + emailotp);
        System.out.println("Cached email OTP: " + cachedValue);
       System.out.println("cahced value email otp:--->"+cachedValue);

        if (cachedValue != null && cachedValue.equals(emailotp)) {
            System.out.println("Email OTP validation succeeded.");
            return true;
        } else {
            System.out.println("Email OTP validation failed.");
            return false;
        }
    }
    public String getCachedEmailOtp(String email) {
        Cache cache = cacheManager.getCache("emailOtpCache");
        Element cachedOtp = cache.get(email);
        System.out.println("Getting cached email OTP for email: " + email);
        System.out.println("Cache: " + cache);
        System.out.println("Cached OTP Element: " + cachedOtp);
        System.out.println("cachedotp in email"+cachedOtp);


        if (cachedOtp != null) {
            String cachedValue = (String) cachedOtp.getObjectValue();

            System.out.println("Cached email OTP value: " + cachedValue);
                    return (String) cachedOtp.getObjectValue();
        }

        System.out.println("Cached email OTP not found.");
        return null;
    }
}
