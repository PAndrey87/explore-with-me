package ru.practicum.mainservice.request.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.user.model.User;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "requests", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"event_id", "requester_id"})
})
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor

@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(nullable = false)
    LocalDateTime created;
    @ManyToOne
    @JoinColumn(nullable = false)
    Event event;
    @ManyToOne
    @JoinColumn(nullable = false)
    User requester;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    RequestStatus status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParticipationRequest that = (ParticipationRequest) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}