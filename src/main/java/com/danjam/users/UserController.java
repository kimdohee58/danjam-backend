package com.danjam.users;


import com.danjam.logInSecurity.UserDetail;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/users/")
@Tag(name = "Users", description = "Users Controller")
public class UserController {

    private final UsersService usersService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UsersService usersService, BCryptPasswordEncoder passwordEncoder) {
        this.usersService = usersService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("validate")
    public ResponseEntity<?> validate(@RequestBody String email) {
        HashMap<String, Object> resultMap = new HashMap<>();
        // 값 이상하게 넘어와서 replace 해줌
        email = email.replace("%40", "@");
        email = email.replace("=", "");

        if (usersService.findByEmail(email) == null) {
            resultMap.put("result", "success");
        } else {
            resultMap.put("result", "fail");
        }
        return ResponseEntity.ok(resultMap);
//        return ResponseEntity.ok(usersService.findByEmail(email));
    }

    @PostMapping("signUp")
    public ResponseEntity<Object> signUp(@RequestBody UsersDto usersDto) {
        HashMap<String, Object> resultMap = new HashMap<>();
        System.out.println("회원가입 접속");

        try{
            usersDto.setPassword(passwordEncoder.encode(usersDto.getPassword()));
            System.out.println("회원가입 성공");
            System.out.println("UserDto" + usersDto);
            usersService.save(usersDto.toEntity(usersDto));
            resultMap.put("result", "success");
        }catch (Exception e) {
            e.printStackTrace();
            resultMap.put("result", "fail");
        }

        return ResponseEntity.ok(resultMap);
//        return ResponseEntity.ok(usersService.save(usersDto));
    }

    @RequestMapping("authSuccess")
    public ResponseEntity<?> authSuccess(@AuthenticationPrincipal UserDetail userDetail) {
        HashMap<String, Object> response = new HashMap<>();
        Users user = userDetail.getUser();

        response.put("result", "success");
        response.put("id", user.getId());
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        response.put("phoneNum", user.getPhoneNum());
        response.put("role", user.getRole());

        return ResponseEntity.ok(response);
//        return ResponseEntity.ok(userDetail.getUser());
    }

    @RequestMapping("authFailure")
    public ResponseEntity<?> authFailure() {
        HashMap<String, Object> response = new HashMap<>();

        response.put("result", "fail");
        return ResponseEntity.ok(response);
//        return ResponseEntity.ok("fail");
    }

    @RequestMapping("logoutSuccess")
    public ResponseEntity<?> logoutSuccess() {
        HashMap<String, Object> response = new HashMap<>();

        response.put("result", "success");
        return ResponseEntity.ok(response);
//        return ResponseEntity.ok("success");
    }

    @GetMapping
    public ResponseEntity<List<?>> findAll() {
        List<UsersDto> users = usersService.findAll()
                .stream()
                .map(UsersDto::fromEntity)
                .toList();

        return new ResponseEntity<>(users, HttpStatus.OK);
//        return ResponseEntity.ok(usersService.findAll());
    }

    @GetMapping("{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        Users findByUser = usersService.findById(id);
        if (findByUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        UsersDto user = UsersDto.fromEntity(findByUser);

        return new ResponseEntity<>(user, HttpStatus.OK);
//        return ResponseEntity.ok(usersService.findById(id));
    }

    @Transactional
    @PatchMapping("{id}")
    public ResponseEntity<?> changePassword(@PathVariable Long id, @RequestBody Map<String, String> requestMap) {
        Users findByUser = usersService.findById(id);
        if (findByUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        findByUser.setPassword(passwordEncoder.encode(requestMap.get("password")));
        usersService.save(findByUser);
        UsersDto user = UsersDto.fromEntity(findByUser);

        return new ResponseEntity<>(user, HttpStatus.OK);
//        return ResponseEntity.ok(usersService.findById(id));
    }

    @Transactional
    @PatchMapping("{id}/phone")
    public ResponseEntity<Void> changePhone(@PathVariable Long id, @RequestBody Map<String, Integer> requestMap) {
        Users findByUser = usersService.findById(id);
        if (findByUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        findByUser.setPhoneNum(requestMap.get("phoneNum"));
        usersService.save(findByUser);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Transactional
    @PatchMapping("{id}/cancel")
    public ResponseEntity<Void> cancelMember(@PathVariable Long id) {
        Users findByUser = usersService.findById(id);
        if (findByUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        usersService.cancel(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("UsersList")
    public ResponseEntity<List<?>> findUsersList() {
        List<Users> userList = usersService.findAll();

        return new ResponseEntity<>(userList, HttpStatus.OK);
//        return ResponseEntity.ok(usersService.findAll());
    }
}
