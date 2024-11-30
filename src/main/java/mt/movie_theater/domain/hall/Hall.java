package mt.movie_theater.domain.hall;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mt.movie_theater.domain.BaseEntity;
import mt.movie_theater.domain.movie.ScreeningType;
import mt.movie_theater.domain.seat.Seat;
import mt.movie_theater.domain.theater.Theater;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Hall extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Theater theater;

    @Column(length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    private ScreeningType screeningType;

    private int hallTypeModifier;

    @OneToMany(mappedBy = "hall", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Seat> seats = new ArrayList<>();

    @Builder
    public Hall(Theater theater, String name, ScreeningType screeningType, int hallTypeModifier) {
        this.theater = theater;
        this.name = name;
        this.screeningType = screeningType;
        this.hallTypeModifier = hallTypeModifier;
    }
}
