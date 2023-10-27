package com.my.application.demo.a.pplication.Service;

import com.my.application.demo.a.pplication.Model.User_role;
import com.my.application.demo.a.pplication.Model.Users;
import com.my.application.demo.a.pplication.Otp_handling_and_cachemanagement.EmailOtpService;
import com.my.application.demo.a.pplication.Otp_handling_and_cachemanagement.PhoneOtpService;
import com.my.application.demo.a.pplication.Otp_handling_and_cachemanagement.VerficationToken_Service;
import com.my.application.demo.a.pplication.Repository.User_Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class User_Service {

    @Autowired
    private User_Repository user_repository;
    @Autowired
    private EmailOtpService emailOtpService;
    @Autowired
    private PhoneOtpService phoneOtpService;
    @Autowired
    public VerficationToken_Service verficationToken_service;

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
            return ResponseEntity.ok(new LoginResponse(" Otp's sent successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new LoginResponse("Failed to send otp"));
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse("Invalid otp,Signup failed"));
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse("Invalid OTPs.Try again"));
        }
    }
    //       forgetphone .............>>//;
        public ResponseEntity<LoginResponse> send_EmailOTP(String email) {
            Users user = user_repository.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse("Email not found"));
            }

            String emailOtpGenerated = emailOtpService.generateOtp();
            boolean emailSent = emailOtpService.sendEmail(email, emailOtpGenerated);

            if (!emailSent) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new LoginResponse("Failed to send otp"));
            }

            emailOtpService.cacheEmailOtp(email, emailOtpGenerated);

            return ResponseEntity.ok(new LoginResponse("otp sent success"));
        }
        public ResponseEntity<LoginResponse> verify_EmailOTP(String email, String emailotp) {
            boolean emailOtpValid = emailOtpService.validateEmailOtp(email, emailotp);

            if (!emailOtpValid)
            {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse("Invalid Email OTP"));
            }

            return ResponseEntity.ok(new LoginResponse("Email OTP verified successfully"));
        }

        public ResponseEntity<LoginResponse> send_NewPhoneOTP(String phone)
        {
            String phoneotp = phoneOtpService.generateOtp();
            boolean phoneSent = phoneOtpService.sendPhoneOtp(phone,phoneotp);

            if (!phoneSent)
            {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new LoginResponse("Failed to send Phone OTP"));
            }
            // Cache the phone otp for verification
            phoneOtpService.cachePhoneOtp(phone,phoneotp);

            return ResponseEntity.ok(new LoginResponse("Phone OTP sent to the new phone number"));
        }
    public ResponseEntity<LoginResponse> verify_NewPhoneOTP(String email, String phone,String phoneotp)
    {
        // Verify the new phone OTP
        boolean newPhoneOtpValid = phoneOtpService.validatePhoneOtp(phone, phoneotp);

        if (!newPhoneOtpValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse("Invalid New Phone OTP"));
        }
        Users user = user_repository.findByEmail(email);

        if (user != null) {
            // Update the user's phone number
            user.setPhone(phone);
            user_repository.save(user);

            return ResponseEntity.ok(new LoginResponse("New Phone OTP verified, and phone number updated successfully"));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new LoginResponse("Unable to update phone number"));
    }
    //forgetemail................>>>/;
        public ResponseEntity<?> send_PhoneOTP(String phone)
       {
            Users user = user_repository.findByPhone(phone);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse("Phone number not found"));
            }
            String phoneotp_generated = phoneOtpService.generateOtp();
            boolean phonesent = phoneOtpService.sendPhoneOtp(phone, phoneotp_generated);

            if (!phonesent)
            {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new LoginResponse("Failed to send phone OTP"));
            }
            String token = UUID.randomUUID().toString();
           System.out.println("tokennn---------->"+token);
            verficationToken_service.storeVerificationToken(token, phone);

            return ResponseEntity.ok(new LoginResponse(token));
       }
    public ResponseEntity<LoginResponse> verify_PhoneOTP(String phone, String phoneotp)
    {
        boolean phoneotp_Valid = phoneOtpService.validatePhoneOtp(phone, phoneotp);

        if (!phoneotp_Valid)
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse("Invalid phone OTP"));
        }

        return ResponseEntity.ok(new LoginResponse("phone OTP verified successfully"));
    }

    public ResponseEntity<LoginResponse> send_NewEmailOTP(String email)
    {
        String emailotp = emailOtpService.generateOtp();
        boolean emailsent = emailOtpService.sendEmail(email,emailotp);

        if (!emailsent)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new LoginResponse("Failed to send email OTP"));
        }
        // Cache the email otp for verification
        emailOtpService.cacheEmailOtp(email,emailotp);

        return ResponseEntity.ok(new LoginResponse("Email otp sent to the new Email"));
    }
    public ResponseEntity<LoginResponse> verify_NewEmailOTP(String token,String email,String emailotp)
    {
        String phone = verficationToken_service.getPhoneForVerificationToken(token);
        System.out.println("phonee----->"+phone);
        if (phone != null) {
            boolean newEmailOtpValid = emailOtpService.validateEmailOtp(email, emailotp);

            if (newEmailOtpValid)
            {
                Users user = user_repository.findByPhone(phone);

                if (user != null) {
                    user.setEmail(email);
                    user_repository.save(user);

                    verficationToken_service.removeVerificationToken(token);

                    return ResponseEntity.ok(new LoginResponse("New Email OTP verified, and email updated successfully"));
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new LoginResponse("Unable to update email"));
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse("Invalid New Email otp"));
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse("Invalid verification"));
        }
    }

}







