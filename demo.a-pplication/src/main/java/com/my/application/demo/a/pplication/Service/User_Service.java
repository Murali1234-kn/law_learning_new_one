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

    public boolean User_Exist(String email, String phone, User_role role) {
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
        String phoneotp = phoneOtpService.generateOtp();

        emailOtpService.cacheEmailOtp(users.getEmail(), emailotp);
        phoneOtpService.cachePhoneOtp(users.getPhone(), phoneotp);

        boolean emailSent = emailOtpService.sendEmail(users.getEmail(), emailotp);
        boolean phoneSent = phoneOtpService.sendPhoneOtp(users.getPhone(), phoneotp);

        System.out.println("email send otp status: " + emailSent);
        System.out.println("phone send otp status: " + phoneSent);

        if (emailSent && phoneSent) {
            System.out.println("OTP's are sent successfully");
            return ResponseEntity.ok(new LoginResponse("Email and Phone OTPs sent successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new LoginResponse("Failed to send OTPs"));
        }
    }

    public ResponseEntity<LoginResponse> verify_And_SignUp(Users users, String emailotp, String phoneotp) {
        boolean emailOtp_Valid = emailOtpService.validateEmailOtp(users.getEmail(), emailotp);
        boolean phoneOtp_Valid = phoneOtpService.validatePhoneOtp(users.getPhone(), phoneotp);

        System.out.println("email_otp :" + emailOtp_Valid);
        System.out.println("Phone_otp :" + phoneOtp_Valid);

        if (emailOtp_Valid && phoneOtp_Valid) {

            user_repository.save(users);
            System.out.println("user details saved to db and sign up successfull");
            return ResponseEntity.ok(new LoginResponse("Signup successful"));
        } else {
            System.out.println("sign up failedin valid otp");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse("Invalid OTPs. Signup failed"));
        }
    }

    public ResponseEntity<LoginResponse> verify_And_Login(Users users, String emailotp, String phoneotp) {
        boolean emailOtp_Valid = emailOtpService.validateEmailOtp(users.getEmail(), emailotp);
        boolean phoneOtp_Valid = phoneOtpService.validatePhoneOtp(users.getPhone(), phoneotp);

        System.out.println("email_otp :" + emailOtp_Valid);
        System.out.println("Phone_otp :" + phoneOtp_Valid);

        if (emailOtp_Valid && phoneOtp_Valid) {
            System.out.println("login Successfull");
            return ResponseEntity.ok(new LoginResponse("Login successfull"));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse("Invalid OTPs. Login failed"));
        }
    }
//forgetemail................>>>/;
    public ResponseEntity<LoginResponse> sendPhoneOTP(String phone, String phoneotp)
    {
        Users user = user_repository.findByPhone(phone);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse("Phone number not found"));
        }

        String phoneOtp = phoneOtpService.generateOtp();
        boolean phoneSent = phoneOtpService.sendPhoneOtp(phone, phoneotp);

        if (!phoneSent) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new LoginResponse("Failed to send Phone OTP"));
        }

        // Cache the phone OTP for verification
        phoneOtpService.cachePhoneOtp(phone, phoneotp);

        return ResponseEntity.ok(new LoginResponse("Phone OTP sent to the phone number"));
    }

    public ResponseEntity<LoginResponse> verifyPhoneOTP(String phone, String phoneotp) {
        boolean phoneOtpValid = phoneOtpService.validatePhoneOtp(phone, phoneotp);

        if (!phoneOtpValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse("Invalid Phone OTP"));
        }

        return ResponseEntity.ok(new LoginResponse("Phone OTP verified successfully"));
    }

    public ResponseEntity<LoginResponse> sendNewEmailOTP(String newEmail, String emailotp) {
        String emailOtp = emailOtpService.generateOtp();
        boolean emailSent = emailOtpService.sendEmail(newEmail, emailotp);

        if (!emailSent) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new LoginResponse("Failed to send Email OTP"));
        }
        emailOtpService.cacheEmailOtp(newEmail, emailotp);

        return ResponseEntity.ok(new LoginResponse("Email OTP sent to the new email address"));
    }
    public ResponseEntity<LoginResponse> verifyNewEmailOTP(String newEmail, String emailotp) {
            boolean emailOtpValid = emailOtpService.validateEmailOtp(newEmail, emailotp);

            if (!emailOtpValid)
            {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse("Invalid Email OTP"));
            }
            Users user = user_repository.findByEmail(newEmail); // Find the user by the new email
            if (user != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new LoginResponse("Email already exists"));
            }

            Users userByPhone = user_repository.findByPhone(user.getPhone());
            if (userByPhone != null) {
                userByPhone.setEmail(newEmail); // Update the email
                user_repository.save(userByPhone); // Save the changes
                return ResponseEntity.ok(new LoginResponse("Email OTP verified and email updated successfully"));
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new LoginResponse("Unable to update email"));
        }
        //       forgetphone .............>>//;
        public ResponseEntity<LoginResponse> sendEmailOTP(String email, String emailOtp) {
            Users user = user_repository.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse("Email not found"));
            }

            String emailOtpGenerated = emailOtpService.generateOtp();
            boolean emailSent = emailOtpService.sendEmail(email, emailOtpGenerated);

            if (!emailSent) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new LoginResponse("Failed to send Email OTP"));
            }

            emailOtpService.cacheEmailOtp(email, emailOtpGenerated);

            return ResponseEntity.ok(new LoginResponse("Email OTP sent to the email address"));
        }
        public ResponseEntity<LoginResponse> verifyEmailOTP(String email, String emailOtp) {
            boolean emailOtpValid = emailOtpService.validateEmailOtp(email, emailOtp);

            if (!emailOtpValid)
            {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse("Invalid Email OTP"));
            }

            return ResponseEntity.ok(new LoginResponse("Email OTP verified successfully"));
        }

        public ResponseEntity<LoginResponse> sendNewPhoneOTP(String newPhone)
        {
            String phoneOtp = phoneOtpService.generateOtp();
            boolean phoneSent = phoneOtpService.sendPhoneOtp(newPhone, phoneOtp);

            if (!phoneSent)
            {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new LoginResponse("Failed to send Phone OTP"));
            }
            // Cache the phone OTP for verification
            phoneOtpService.cachePhoneOtp(newPhone, phoneOtp);
            return ResponseEntity.ok(new LoginResponse("Phone OTP sent to the new phone number"));
        }

        public ResponseEntity<LoginResponse> verifyNewPhoneOTP(String newPhone, String phoneOtp)
        {
            boolean phoneOtpValid = phoneOtpService.validatePhoneOtp(newPhone, phoneOtp);

            if (!phoneOtpValid)
            {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse("Invalid Phone OTP"));
            }

            // Find the user by the new phone number
            Users user = user_repository.findByPhone(newPhone);

            if (user != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new LoginResponse("Phone number already exists"));
            }

            Users userByPhone = user_repository.findByPhone(user.getPhone());
            if (userByPhone != null) {
                userByPhone.setPhone(newPhone);
                user_repository.save(userByPhone); // Save the changes
                return ResponseEntity.ok(new LoginResponse("Phone OTP verified, and phone number updated successfully"));
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new LoginResponse("Unable to update phone number"));
        }
    }





