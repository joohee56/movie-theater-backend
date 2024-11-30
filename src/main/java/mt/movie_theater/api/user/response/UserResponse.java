package mt.movie_theater.api.user.response;

import lombok.Builder;
import lombok.Getter;
import mt.movie_theater.domain.user.User;

@Getter
public class UserResponse {
    private Long id;
    private String loginId;
    private String name;

    @Builder
    public UserResponse(Long id, String loginId, String name) {
        this.id = id;
        this.loginId = loginId;
        this.name = name;
    }

    public static UserResponse create(User savedUser) {
        return UserResponse.builder()
                .id(savedUser.getId())
                .loginId(savedUser.getLoginId())
                .name(savedUser.getName())
                .build();
    }
}
