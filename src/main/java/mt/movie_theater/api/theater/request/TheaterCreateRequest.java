package mt.movie_theater.api.theater.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mt.movie_theater.domain.theater.Region;
import mt.movie_theater.domain.theater.Theater;

@Getter
@NoArgsConstructor
public class TheaterCreateRequest {

    @NotBlank(message = "영화관 이름은 필수 입력값입니다.")
    private String name;

    @NotBlank(message = "영화관 주소는 필수 입력값입니다.")
    private String address;

    @NotNull(message = "영화관 지역은 필수 입력값입니다.")
    private Region region;

    @NotNull(message = "영화관 위도는 필수 입력값입니다.")
    private Float latitude;

    @NotNull(message = "영화관 경도는 필수 입력값입니다.")
    private Float longitude;

    private String contactNumber;

    @Builder
    public TheaterCreateRequest(String name, String address, Region region, Float latitude, Float longitude,
                                String contactNumber) {
        this.name = name;
        this.address = address;
        this.region = region;
        this.latitude = latitude;
        this.longitude = longitude;
        this.contactNumber = contactNumber;
    }

    public Theater toEntity() {
        return Theater.builder()
                .name(this.name)
                .address(this.address)
                .region(this.region)
                .latitude(this.latitude)
                .longitude(this.longitude)
                .contactNumber(this.contactNumber)
                .build();
    }
}
