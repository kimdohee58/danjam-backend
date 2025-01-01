package com.danjam.users.querydsl;

import com.danjam.users.Users;
import com.querydsl.jpa.impl.JPAQuery;

import java.util.List;

public interface UsersRepositoryCustom {
    List<UsersListDTO> findUsersList();
    Users findByEmail(String email);
    Users findByEmailAndPassword(String email, String password);
}
