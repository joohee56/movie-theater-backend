package mt.movie_theater.api.user.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import mt.movie_theater.api.user.request.UserCreateRequest;
import mt.movie_theater.api.user.response.UserResponse;
import mt.movie_theater.domain.user.User;
import mt.movie_theater.domain.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public UserResponse join(UserCreateRequest request) {
        User savedUser = userRepository.save(request.toEntity());
        return UserResponse.create(savedUser);
    }

    public User authenticate(String loginId, String password) {
        Optional<User> findUser = userRepository.findByLoginId(loginId);
        if(findUser.isEmpty()) {
            throw new IllegalArgumentException("없는 아이디입니다. 아이디 정보를 다시 확인해 주세요.");
        }
        if (!findUser.get().getPassword().equals(password)) {
            throw new IllegalArgumentException("아이디와 비밀번호가 일치하지 않습니다. 다시 확인해 주세요.");
        }

        return findUser.get();
    }
}
