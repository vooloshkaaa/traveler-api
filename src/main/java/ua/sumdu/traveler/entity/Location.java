package ua.sumdu.traveler.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;
import ua.sumdu.traveler.entity.TravelPlan;

import javax.swing.event.DocumentEvent;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "locations")
@Getter
@Setter
@NoArgsConstructor
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "travel_plan_id")
    private TravelPlan travelPlan;

    @Column(nullable = false, length = 200) private String name;
    private String address;
    @Column(precision = 10, scale = 6) private BigDecimal latitude;
    @Column(precision = 11, scale = 6) private BigDecimal longitude;
    @Column(name = "visit_order", nullable = false) private Integer visitOrder;
    @Column(name = "arrival_date")   private Instant arrivalDate;
    @Column(name = "departure_date") private Instant departureDate;
    @Column(precision = 10, scale = 2) private BigDecimal budget;
    @Column(columnDefinition = "text") private String notes;

    @Version
    @Column(nullable = false)
    private Integer version = 1;

    @Generated(event = EventType.INSERT)
    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;
}