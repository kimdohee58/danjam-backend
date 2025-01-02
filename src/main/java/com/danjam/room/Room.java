package com.danjam.room;

import com.danjam.booking.Booking;
import com.danjam.dorm.Dorm;
import com.danjam.roomImg.RoomImg;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@ToString
@Table(name = "room")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String name;
    @Lob
    private String description;
    private int person;
    private int price;
    @Column(length = 10)
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dorm_id")
    @JsonBackReference
    private Dorm dorm;

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Booking> bookings;

    @OneToMany(mappedBy = "room")
    private List<RoomImg> images;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @Builder
    public Room(String name, String description, int person, int price, String type, Dorm dorm) {
        this.name = name;
        this.description = description;
        this.person = person;
        this.price = price;
        this.type = type;
        this.dorm = dorm;
    }
}
