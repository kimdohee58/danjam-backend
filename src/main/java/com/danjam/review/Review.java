package com.danjam.review;

import com.danjam.booking.Booking;
import com.danjam.users.Users;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@ToString
@Table(name = "review")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Lob
    private String content;
    private double rate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users users;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updateAt;

    @Builder
    public Review(String content, double rate, Users users, Booking booking, LocalDateTime createAt, LocalDateTime updateAt) {
        this.content = content;
        this.rate = rate;
        this.users = users;
        this.booking = booking;
        this.createAt = createAt;
        this.updateAt = updateAt;
    }
}
