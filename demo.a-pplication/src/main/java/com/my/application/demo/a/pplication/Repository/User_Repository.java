package com.my.application.demo.a.pplication.Repository;

import com.my.application.demo.a.pplication.Model.User_role;
import com.my.application.demo.a.pplication.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface User_Repository  extends JpaRepository<Users,Integer>
{
    Users findByEmailAndPhoneAndRole(String email, String phone, User_role role);//user_valid

    Users findByPhone(String phone);

    Users findByEmail(String email);
    }



  //  User findById(Long id);
  //  User save(User user);
   // void delete(User user);

  //  findAll(),
    //    findById() ,
      //  save(),
        //delete(),
        //findByEmailAndPhone()