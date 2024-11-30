package mt.movie_theater.domain.theater;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Region {
    SEOUL("서울"),
    GYEONGGI("경기"),
    INCHEON("인천"),
    DAEJEON_CHUNGCHEONG_SEJONG("대전/충청/세종"),
    BUSAN_DAEGU_GYEONGSANG("부산/대구/경상"),
    GWANGJU_JEONLLA("광주/전라"),
    GANGWON("강원"),
    JEJU("제주");

    private final String text;
}
