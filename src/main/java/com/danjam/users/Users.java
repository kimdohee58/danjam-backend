package com.danjam.users;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@ToString
@Table(name = "users")
@DynamicInsert // default
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "email", length = 30, nullable = false)
    private String email;

    @Setter
    @Column(name = "password", length = 100, nullable = false)
    private String password;

    @Column(name = "name", length = 45, nullable = false)
    private String name;

    @Setter
    @Column(name = "phone_number", length = 11, nullable = false)
    private int phoneNum;

    @Enumerated(EnumType.STRING) // enumtype.string 옵션 사용하면 enum 이름 그대로 db에 저장
    @Column(name = "role", nullable = true)
    private Role role;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updateAt;

    @ColumnDefault("'Y'")
    private String status;

    public Users(String email, String password) {
        this.email = email;
        this.password = password;
        this.role = getRole();
    }
}