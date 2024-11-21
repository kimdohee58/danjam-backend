package com.danjam.users.querydsl;

import com.danjam.users.Users;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UsersRepositoryCustom {
    List<UsersListDTO> findUsersList();
    Users findByEmail(String email);
    Users findByEmailAndPassword(String email, String password);
    long changePhone(Long id, int phone);
    long cancel(@Param("id") long id);
}
