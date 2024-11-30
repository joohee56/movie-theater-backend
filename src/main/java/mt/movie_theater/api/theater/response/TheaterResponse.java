package mt.movie_theater.api.theater.response;

import lombok.Builder;
import lombok.Getter;
import mt.movie_theater.domain.theater.Region;
import mt.movie_theater.domain.theater.Theater;

@Getter
public class TheaterResponse {
    private Long id;
    private String name;
    private String address;
    private String region;
    private Float latitude;
    private Float longitude;
    private String contactNumber;

    @Builder
    public TheaterResponse(Long id, String name, String address, Region region, Float latitude, Float longitude,
                           String contactNumber) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.region = region.getText();
        this.latitude = latitude;
        this.longitude = longitude;
        this.contactNumber = contactNumber;
    }

    public static TheaterResponse create(Theater theater) {
        return TheaterResponse.builder()
                .id(theater.getId())
                .name(theater.getName())
                .address(theater.getAddress())
                .region(theater.getRegion())
                .latitude(theater.getLatitude())
                .longitude(theater.getLongitude())
                .contactNumber(theater.getContactNumber())
                .build();
    }
}
