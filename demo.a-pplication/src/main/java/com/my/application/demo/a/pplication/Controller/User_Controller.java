package com.my.application.demo.a.pplication.Controller;

import com.my.application.demo.a.pplication.Model.User_role;
import com.my.application.demo.a.pplication.Model.Users;
import com.my.application.demo.a.pplication.Repository.User_Repository;
import com.my.application.demo.a.pplication.Service.LoginResponse;
import com.my.application.demo.a.pplication.Service.User_Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/signup&login/")
public class User_Controller
{
    @Autowired
    public User_Service user_Service;
    @Autowired
    public User_Repository user_repository;

    @PostMapping("/users")
    public ResponseEntity<LoginResponse> sendOtp_VerifyOtp(
            @RequestParam String email, @RequestParam String phone,
            @RequestParam User_role role, @RequestBody Users user,
            @RequestParam String emailotp, @RequestParam String phoneotp,
            @RequestParam String action) {

        if ("sendotplogin".equalsIgnoreCase(action))
        {
            if (email != null && phone != null && role != null) {
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

                if (userExists) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(new LoginResponse("User already exists for sign-up"));
                } else {
                    Users newUser = new Users(email, phone, role);
                    return user_Service.sendOtp(newUser);
                }
            }
        } else if ("verifylogin".equalsIgnoreCase(action)) {
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
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new LoginResponse("Internal server error"));
    }
}

  /*  @PostMapping("/login/sendotp")
    public ResponseEntity<LoginResponse> sendOtp_For_Login(@RequestParam String email, @RequestParam String phone,
                                                           @RequestParam User_role role) {
        boolean valid = user_Service.User_Valid(email,phone,role);
        System.out.println("userExits :"+valid);

        if (valid)
        {
            Users user = new Users(email, phone, role);
            return user_Service.sendOtp(user);
        }
        else
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse("Invalid user"));
        }
    }
    @PostMapping("/login/verifyotp")
    public ResponseEntity<LoginResponse> verifyOtp_For_Login(@RequestBody Users user, @RequestParam String emailotp,
                                                             @RequestParam String phoneotp)
    {
        return user_Service.verify_And_Login(user,emailotp, phoneotp);
    }
    @PostMapping("/signup/sendotp")
public ResponseEntity<LoginResponse> sendOtp_For_SignUp(@RequestParam String email,@RequestParam String phone, @RequestParam User_role role) {
    boolean userExists = user_Service.User_Exist(email,phone,role);
        System.out.println("userExits  : "+userExists);
    if (userExists)
    {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new LoginResponse("User already exists"));
    }
    else
    {
        Users user = new Users(email, phone, role);
        return user_Service.sendOtp(user);
    }
}

    @PostMapping("/signup/verifyotp")
    public ResponseEntity<LoginResponse> verifyOtp_For_SignUp(@RequestParam String emailotp, @RequestParam String phoneotp,@RequestBody Users users) {

        Users exist = user_repository.findByEmailAndPhoneAndRole(users.getEmail(), users.getPhone(),users.getRole());
        System.out.println("users exit: "+exist);

        if (exist == null)
        {

            return user_Service.verify_And_SignUp(users,emailotp,phoneotp);
        }
        else
        {
            System.out.println("already exits users");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new LoginResponse("User with the same email, phone,and role already exists"));
        }
    }*/

    /*@PostMapping("/users")
      public ResponseEntity<LoginResponse> sendOtp_VerifyOtp(@RequestParam String email, @RequestParam String phone,
                                                             @RequestParam User_role role, @RequestBody Users user, @RequestParam  String emailotp,
                                                             @RequestParam String phoneotp, @RequestParam String action)
      {
          if (action.equals("sendotp"))
           {
              boolean user_Exists = user_Service.User_Exist(email,phone,role);

              if (user_Exists)
              {
                  return ResponseEntity.status(HttpStatus.CONFLICT).body(new LoginResponse("User already exists"));
              }
              else
              {
                  Users users = new Users(email, phone, role);
                  return user_Service.sendOtp(users);
              }
          }
         else if (action.equals("verifyotp"))
         {
              if (user != null)
              {
                  return user_Service.verify_And_Login(user,emailotp,phoneotp);
              }
              else
              {
                  return user_Service.verify_And_SignUp(new Users(email, phone, role), emailotp, phoneotp);
              }
          }
           else {
              return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new LoginResponse("Invalid action"));
          }
      }*/