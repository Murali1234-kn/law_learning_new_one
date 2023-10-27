package com.my.application.demo.a.pplication.Controller;

import com.my.application.demo.a.pplication.Model.User_role;
import com.my.application.demo.a.pplication.Model.Users;
import com.my.application.demo.a.pplication.Otp_handling_and_cachemanagement.VerficationToken_Service;
import com.my.application.demo.a.pplication.Repository.User_Repository;
import com.my.application.demo.a.pplication.Service.LoginResponse;
import com.my.application.demo.a.pplication.Service.User_Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/signuplogin")
public class User_Controller
{
    @Autowired
    public User_Service user_Service;
    @Autowired
    public User_Repository user_repository;
    @Autowired
    public VerficationToken_Service verficationToken_service;

    @PostMapping("/sendotp")
    public ResponseEntity<LoginResponse> sendOtp(
            @RequestParam String email, @RequestParam String phone,
            @RequestParam User_role role, @RequestBody Users user,
            @RequestParam String action)
    {

        if ("sendotplogin".equalsIgnoreCase(action))
        {
            if (email != null && phone != null && role != null)
            {
                boolean userExists = user_Service.User_Exist(email, phone, role);
                if (userExists) {
                    return user_Service.sendOtp(user);
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse("User not found for login"));
                }
            }
        } else if ("sendotpsignup".equalsIgnoreCase(action)) {
            if (email != null && phone != null && role != null) {
                boolean userExists = user_Service.User_Exist(email, phone, role);

                if (userExists)
                {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(new LoginResponse("User already exists for sign-up"));
                } else {
                    Users newUser = new Users(email, phone, role);
                    return user_Service.sendOtp(newUser);
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new LoginResponse("Missing required parameters"));
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new LoginResponse("Invalid action"));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new LoginResponse("Internal server error"));
    }

    @PostMapping("/verifyotp")
    public ResponseEntity<LoginResponse> verifyOtp(@RequestBody Users user, @RequestParam String emailotp,
            @RequestParam String phoneotp,
            @RequestParam String action)
    {
        if ("verifylogin".equalsIgnoreCase(action)) {
            if (user != null) {
                return user_Service.verify_And_Login(user, emailotp, phoneotp);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse("User not found for login"));
            }
        } else if ("verifysignup".equalsIgnoreCase(action)) {
            if (user != null) {
                return user_Service.verify_And_SignUp(user, emailotp, phoneotp);
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new LoginResponse("User not found for sign-up"));
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new LoginResponse("Invalid action"));
        }
    }
    @PostMapping("/forgetemail")
    public ResponseEntity<?> forgetEmail(
                @RequestParam String phone,
                @RequestParam String phoneotp,
                @RequestParam String action
        ) {
            if ("1".equalsIgnoreCase(action)) {
                return user_Service.send_PhoneOTP(phone);
            } else if ("2".equalsIgnoreCase(action)) {
                return user_Service.verify_PhoneOTP(phone, phoneotp);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new LoginResponse("Invalid action"));
            }
        }
        @PostMapping("/updateemail")
        public ResponseEntity<LoginResponse> updateEmail(
                @RequestParam String email,
                @RequestParam String emailotp,
                @RequestParam String action,
                @RequestParam String token
        ) {
            if ("3".equalsIgnoreCase(action)) {
                return user_Service.send_NewEmailOTP(email);
            } else if ("4".equalsIgnoreCase(action))
            {
                return user_Service.verify_NewEmailOTP(token,email,emailotp);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new LoginResponse("Invalid action"));
            }
        }
    ////////////////////////////////////////////////////////////////////////////////;
        @PostMapping("/forgetphone")
        public ResponseEntity<LoginResponse> forgetPhone(@RequestParam String email, @RequestParam String emailotp,
                @RequestParam String phone, @RequestParam String phoneotp,
                @RequestParam String action)
        {
            if ("sendemailotp".equalsIgnoreCase(action))
            {
                return user_Service.send_EmailOTP(email);
            }
            else if ("verifyemailotp".equalsIgnoreCase(action))
            {
                return user_Service.verify_EmailOTP(email, emailotp);
            }
            else if ("sendnewphoneotp".equalsIgnoreCase(action))
            {
                return user_Service.send_NewPhoneOTP(phone);
            }
            else if ("verifynewphoneotp".equalsIgnoreCase(action))
            {
                return user_Service.verify_NewPhoneOTP(email,phone,phoneotp);
            }
            else
            {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new LoginResponse("Invalid action"));
            }
        }
    }
