package mt.movie_theater.api.theater.response;

import lombok.Builder;
import lombok.Getter;
import mt.movie_theater.domain.theater.Region;

@Getter
public class RegionScreeningCountResponse {
    private String region;
    private String regionDisplay;
    private Long count;

    @Builder
    private RegionScreeningCountResponse(String region, String regionDisplay, Long count) {
        this.region = region;
        this.regionDisplay = regionDisplay;
        this.count = count;
    }

    public static RegionScreeningCountResponse create(Region region, Long count) {
        return RegionScreeningCountResponse.builder()
                .region(region.name())
                .regionDisplay(region.getText())
                .count(count)
                .build();
    }
}
