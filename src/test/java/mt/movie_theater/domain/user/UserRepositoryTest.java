package mt.movie_theater.domain.user;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.transaction.Transactional;
import java.util.Optional;
import mt.movie_theater.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Transactional
class UserRepositoryTest extends IntegrationTestSupport {
    @Autowired
    private UserRepository userRepository;

    @DisplayName("로그인ID에 해당하는 유저를 조회한다. ")
    @Test
    void findByLoginId() {
        //given
        User user = createUser("test123", "1234");

        //when
        Optional<User> findUser = userRepository.findByLoginId(user.getLoginId());

        //then
        assertThat(findUser).isPresent();
        assertThat(findUser.get())
                .extracting("loginId", "password")
                .containsExactly("test123", "1234");
    }

    private User createUser(String loginId, String password) {
        User user = User.builder()
                .loginId(loginId)
                .password(password)
                .build();
        return userRepository.save(user);
    }

}