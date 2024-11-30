package mt.movie_theater.api.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import mt.movie_theater.IntegrationTestSupport;
import mt.movie_theater.api.user.request.UserCreateRequest;
import mt.movie_theater.api.user.response.UserResponse;
import mt.movie_theater.domain.user.User;
import mt.movie_theater.domain.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class UserServiceTest extends IntegrationTestSupport {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @DisplayName("새 회원을 등록한다.")
    @Test
    void joinTest() {
        //given
        UserCreateRequest request = UserCreateRequest.builder()
                .loginId("joohee123")
                .password("1234")
                .name("이주희")
                .email("test@test.com")
                .build();
        //when
        UserResponse response = userService.join(request);

        //then
        assertThat(response.getId()).isNotNull();
        assertThat(response)
                .extracting("loginId", "name")
                .contains("joohee123", "이주희");
    }

    @DisplayName("로그인을 검증한다.")
    @Test
    void authenticate() {
        //given
        String loginId = "test123";
        String password = "1234";
        join(loginId, password);

        //when
        User user = userService.authenticate(loginId, password);

        //then
        assertThat(user)
                .extracting("loginId", "password")
                .containsExactly(loginId, password);
    }

    @DisplayName("로그인 검증 시, 없는 로그인 ID일 경우 예외가 발생한다. ")
    @Test
    void authenticateWithNoLoginId() {
        //when, then
        assertThatThrownBy(() -> userService.authenticate("test1234", "1234"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("없는 아이디입니다. 아이디 정보를 다시 확인해 주세요.");
    }

    @DisplayName("로그인 검증 시, 비밀번호가 다를 경우 예외가 발생한다.")
    @Test
    void authenticateWithWrongPassword() {
        //given
        String loginId = "test123";
        join(loginId, "1234");

        //when, then
        assertThatThrownBy(() -> userService.authenticate(loginId, "000"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("아이디와 비밀번호가 일치하지 않습니다. 다시 확인해 주세요.");
    }

    private User join(String loginId, String password) {
        User user = User.builder()
                .loginId(loginId)
                .password(password)
                .build();
        return userRepository.save(user);
    }
}