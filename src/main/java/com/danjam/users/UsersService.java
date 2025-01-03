package com.danjam.users;

import com.danjam.users.querydsl.UsersListDTO;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UsersService {
    Users save(Users users);
    List<Users> findAll();
    Users findById(Long id);
    Users findByEmail(String email);
    void cancel(@Param("id") long id);

    /*public UsersService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public void save(Users users) {
        usersRepository.save(users);
    }

    public List<Users> findAll() {
        return usersRepository.findAll();
    }

    public Users findByEmail(String email) { return usersRepository.findByEmail(email); }

    public List<Users> findUsersList() { return usersRepository.findAll(); }

    public Users findById(long id) {
        return usersRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public void cancel(long id) {
        usersRepository.cancel(id);
    }*/
}
