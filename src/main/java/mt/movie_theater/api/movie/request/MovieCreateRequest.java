package mt.movie_theater.api.movie.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mt.movie_theater.domain.genre.GenreType;
import mt.movie_theater.domain.movie.AgeRating;
import mt.movie_theater.domain.movie.ScreeningType;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor
@Setter
public class MovieCreateRequest {

    @NotBlank(message = "영화제목은 필수 입력값입니다.")
    private String title;

    private String subTitle;

    @NotBlank(message = "줄거리는 필수 입력값입니다.")
    private String description;

    @NotNull(message = "개봉일은 필수 입력값입니다.")
    @DateTimeFormat(pattern = "yyyy.MM.dd")
    private LocalDate releaseDate;

    @Positive(message = "상영시간은 양수여야합니다.")
    private int durationMinutes;

    @NotNull(message = "포스터 이미지는 필수 입력값입니다.")
    private MultipartFile posterImage;

    @NotNull(message = "영화 관람 등급은 필수 입력값입니다.")
    private AgeRating ageRating;

    private String director;

    @NotNull(message = "상영타입은 필수 입력값입니다.")
    private ScreeningType screeningType;

    @PositiveOrZero(message = "기본금액은 0 또는 양수여야 합니다.")
    private int standardPrice;

    @NotEmpty(message = "장르 리스트는 필수 입력값입니다.")
    private List<GenreType> genreTypes;

    private List<String> actors;

    @Builder
    public MovieCreateRequest(String title, String subTitle, String description, LocalDate releaseDate,
                              int durationMinutes,
                              MultipartFile posterImage, AgeRating ageRating, String director,
                              ScreeningType screeningType,
                              int standardPrice, List<GenreType> genreTypes, List<String> actors) {
        this.title = title;
        this.subTitle = subTitle;
        this.description = description;
        this.releaseDate = releaseDate;
        this.durationMinutes = durationMinutes;
        this.posterImage = posterImage;
        this.ageRating = ageRating;
        this.director = director;
        this.screeningType = screeningType;
        this.standardPrice = standardPrice;
        this.genreTypes = genreTypes;
        this.actors = actors;
    }
}
