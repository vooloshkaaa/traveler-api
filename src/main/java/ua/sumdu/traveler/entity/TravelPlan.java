package ua.sumdu.traveler.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "travel_plans")
@Getter
@Setter
@NoArgsConstructor
public class TravelPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 200) private String title;
    @Column(columnDefinition = "text")      private String description;
    @Column(name = "start_date")            private LocalDate startDate;
    @Column(name = "end_date")              private LocalDate endDate;
    @Column(precision = 10, scale = 2)      private BigDecimal budget;
    @Column(length = 3)                     private String currency = "USD";
    @Column(name = "is_public")             private boolean publicPlan;

    @Generated(event = EventType.INSERT)
    @Column(insertable = false, updatable = false)
    private Integer version;

    @Generated(event = EventType.INSERT)
    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;

    @Generated(event = EventType.INSERT)
    @Column(name = "updated_at", insertable = false, updatable = false)
    private Instant updatedAt;
    @OneToMany(mappedBy = "travelPlan")
    @OnDelete(action = OnDeleteAction.CASCADE)   // каскад робить сама БД
    @OrderBy("visitOrder ASC")
    private List<Location> locations = new ArrayList<>();
}