package mt.movie_theater.api.theater.response;

import lombok.Builder;
import lombok.Getter;
import mt.movie_theater.domain.theater.dto.RegionTheaterCountDto;

@Getter
public class RegionTheaterCountResponse {
    private String region;
    private String regionDisplay;
    private Long count;

    @Builder
    private RegionTheaterCountResponse(String region, String regionDisplay, Long count) {
        this.region = region;
        this.regionDisplay = regionDisplay;
        this.count = count;
    }

    public static RegionTheaterCountResponse create(RegionTheaterCountDto dto) {
        return RegionTheaterCountResponse.builder()
                .region(dto.getRegion().name())
                .regionDisplay(dto.getRegion().getText())
                .count(dto.getCount())
                .build();
    }
}
