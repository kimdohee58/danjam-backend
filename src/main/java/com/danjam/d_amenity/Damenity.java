package com.danjam.d_amenity;

import com.danjam.amenity.Amenity;
import com.danjam.dorm.Dorm;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@ToString
@Table(name = "d_amenity")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Damenity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 댓글 고유

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "amenity_id")
    private Amenity amenity; // 카테고리

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dorm_id")
    private Dorm dorm; // 판매자

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;


    @Builder
    public Damenity(Long id , Amenity amenity, Dorm dorm){
        this.id = id;
        this.amenity = amenity;
        this.dorm = dorm;

    }
}
