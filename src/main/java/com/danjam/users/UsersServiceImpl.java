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
    public Users findByEmail(String email) {
        return null;
    }

    @Override
    public Users findByEmailAndPassword(String email, String password) {
        return null;
    }

    @Override
    public long changePhone(Long id, int phone) {
        return 0;
    }

    @Override
    public long cancel(long id) {
        return 0;
    }

    /*@Modifying
    @Query("UPDATE Users u SET u.status = 'N' WHERE u.id = :id")
    public void cancel(@Param("id") long id){

    };*/
}
