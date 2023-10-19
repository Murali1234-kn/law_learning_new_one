package com.my.application.demo.a.pplication.Service;

import com.my.application.demo.a.pplication.Model.User_role;
import com.my.application.demo.a.pplication.Model.Users;
import com.my.application.demo.a.pplication.Otp_handling_and_cachemanagement.EmailOtpService;
import com.my.application.demo.a.pplication.Otp_handling_and_cachemanagement.PhoneOtpService;
import com.my.application.demo.a.pplication.Repository.User_Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class User_Service {

    @Autowired
    private User_Repository user_repository;
    @Autowired
    private EmailOtpService emailOtpService;
    @Autowired
    private PhoneOtpService phoneOtpService;

    public boolean User_Valid(String email, String phone, User_role role) {
        Users user = user_repository.findByEmailAndPhoneAndRole(email, phone, role);
        return user != null;
    }

    public boolean User_Exist(String email,String phone, User_role role)
    {
        Users users = user_repository.findByEmailAndPhoneAndRole(email, phone, role);
        return users != null;
    }

   /* public ResponseEntity<LoginResponse> sendOtp(Users users) {

        String emailotp = emailOtpService.generateOtp();
        String phoneotp = phoneOtpService.generateOtp();

        boolean emailSent = emailOtpService.sendEmail(users.getEmail(), emailotp);
        boolean phoneSent = phoneOtpService.sendPhoneOtp(users.getPhone(), phoneotp);

        emailOtpService.cacheEmailOtp(users.getEmail(), emailotp);
        phoneOtpService.cachePhoneOtp(users.getPhone(), phoneotp);

        System.out.println("email send otp status"+emailSent);
        System.out.println("phone send otp status"+phoneSent);

        if (emailSent && phoneSent) {
            System.out.println("otp's are sent successfull");
            return ResponseEntity.ok(new LoginResponse("Email and Phone otp's sent successfully"));
        }
        else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new LoginResponse("Failed to send OTPs"));
        }
    }*/
    public ResponseEntity<LoginResponse> sendOtp(Users users) {
        String emailotp = emailOtpService.generateOtp();
        String phoneotp=phoneOtpService.generateOtp();

        emailOtpService.cacheEmailOtp(users.getEmail(), emailotp);
        phoneOtpService.cachePhoneOtp(users.getPhone(), phoneotp);

        boolean emailSent = emailOtpService.sendEmail(users.getEmail(), emailotp);
        boolean phoneSent = phoneOtpService.sendPhoneOtp(users.getPhone(), phoneotp);



        System.out.println("email send otp status: " + emailSent);
        System.out.println("phone send otp status: " + phoneSent);

        if (emailSent && phoneSent)
        {
            System.out.println("OTP's are sent successfully");
            return ResponseEntity.ok(new LoginResponse("Email and Phone OTPs sent successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new LoginResponse("Failed to send OTPs"));
        }
    }

    public ResponseEntity<LoginResponse> verify_And_SignUp(Users users,String emailotp, String phoneotp)
    {
        boolean emailOtp_Valid = emailOtpService.validateEmailOtp(users.getEmail(), emailotp);
        boolean phoneOtp_Valid = phoneOtpService.validatePhoneOtp(users.getPhone(), phoneotp);

        System.out.println("email_otp :" + emailOtp_Valid);
        System.out.println("Phone_otp :" + phoneOtp_Valid);

        if (emailOtp_Valid && phoneOtp_Valid) {

                user_repository.save(users);
            System.out.println("user details saved to db and sign up successfull");
                return ResponseEntity.ok(new LoginResponse("Signup successful"));
            }
        else
        {
            System.out.println("sign up failedin valid otp");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse("Invalid OTPs. Signup failed"));
        }
    }

    public ResponseEntity<LoginResponse> verify_And_Login(Users users, String emailotp, String phoneotp)
    {
        boolean emailOtp_Valid = emailOtpService.validateEmailOtp(users.getEmail(), emailotp);
        boolean phoneOtp_Valid = phoneOtpService.validatePhoneOtp(users.getPhone(), phoneotp);

        System.out.println("email_otp :" + emailOtp_Valid);
        System.out.println("Phone_otp :" + phoneOtp_Valid);

        if (emailOtp_Valid && phoneOtp_Valid)
        {
            System.out.println("login Successfull");
            return ResponseEntity.ok(new LoginResponse("Login successfull"));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse("Invalid OTPs. Login failed"));
        }
    }
}
 /*   private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }*/
