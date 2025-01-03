package com.danjam.users.querydsl;

import com.danjam.users.QUsers;
import com.danjam.users.Users;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UsersRepositoryCustomImpl implements  UsersRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    private final QUsers users = QUsers.users;

    public UsersRepositoryCustomImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<UsersListDTO> findUsersList() {
        return queryFactory
                .select(Projections.constructor(
                        UsersListDTO.class,
                        users.id,
                        users.email,
                        users.name,
                        Expressions.stringTemplate("CONCAT('0', CAST({0} AS CHAR))", users.phoneNum).as("phoneNum"),
                        Expressions.stringTemplate(
                                "CASE WHEN {0} = 'ROLE_ADMIN' THEN '관리자' " +
                                        "WHEN {0} = 'ROLE_SELLER' THEN '판매자' " +
                                        "ELSE '일반고객' END",
                                users.role).as("role"),
                        Expressions.stringTemplate(
                                "CASE WHEN {0} = 'Y' THEN '활동중' " +
                                        "ELSE '휴면중' END",
                                users.status).as("status"),
                        users.createAt,
                        users.updateAt
                ))
                .from(users)
                .fetch();
    }

    @Override
    public Users findByEmail(String email) {
        return queryFactory
                .select(users)
                .from(users)
                .where(users.email.eq(email))
                .fetchFirst();
    }

    @Override
    public Users findByEmailAndPassword(String email, String password) {
        return queryFactory
                .select(users)
                .from(users)
                .where(users.email.eq(email).and(users.email.eq(password)))
                .fetchFirst();
    }
}
