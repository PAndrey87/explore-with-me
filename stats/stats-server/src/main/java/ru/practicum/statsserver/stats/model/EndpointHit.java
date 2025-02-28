package ru.practicum.statsserver.stats.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "stats")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EndpointHit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     Integer id;
     String app;
     String uri;
     String ip;
     LocalDateTime timestamp;
}
