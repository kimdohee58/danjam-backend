package com.danjam.users;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {
    private final UsersRepository usersRepository;

    @Override
    public Users save(UsersDto users) {
        return usersRepository.save(users.toEntity());
    }

    @Override
    public List<Users> findAll(){
        return usersRepository.findAll();
    };

    @Override
    public Users findById(Long id) {
        return usersRepository.findById(id).orElse(null);
    }

    @Override
    @Query("SELECT u.email FROM Users u WHERE u.email = :email")
    public Users findByEmail(String email){
        return usersRepository.findByEmail(email);
    };
    @Modifying
    @Query("UPDATE Users u SET u.status = 'N' WHERE u.id = :id")
    public void cancel(@Param("id") long id){

    };
}
