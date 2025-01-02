package com.danjam.wish;

import com.danjam.dorm.Dorm;
import com.danjam.users.Users;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@ToString
@Table(name = "wish")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Wish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users users;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dorm_id")
    private Dorm dorm;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Builder
    public Wish(long id, Users users, Dorm dorm, LocalDateTime createdAt) {
        this.id = id;
        this.users = users;
        this.dorm = dorm;
        this.createdAt = createdAt;
    }
}
