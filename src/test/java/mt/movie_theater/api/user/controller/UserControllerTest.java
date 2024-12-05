package mt.movie_theater.api.user.controller;

import static org.mockito.BDDMockito.when;
import static org.mockito.Mockito.anyString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import mt.movie_theater.api.user.constants.SessionConstants;
import mt.movie_theater.api.user.request.UserCreateRequest;
import mt.movie_theater.api.user.request.UserLoginRequest;
import mt.movie_theater.api.user.service.SessionService;
import mt.movie_theater.api.user.service.UserService;
import mt.movie_theater.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService userService;
    @MockBean
    private SessionService sessionService;

    @DisplayName("신규 회원을 등록한다.")
    @Test
    void join() throws Exception {
        //given
        UserCreateRequest request = UserCreateRequest.builder()
                .loginId("test")
                .password("1234")
                .name("홍길동")
                .email("test@test.com")
                .build();

        //when, then
        mockMvc.perform(MockMvcRequestBuilders.post(("/api/v1/users/user/join"))
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @DisplayName("신규 회원을 등록할 때, 로그인 ID는 필수값 입력값이다.")
    @Test
    void joinNoLoginId() throws Exception {
        //given
        UserCreateRequest request = UserCreateRequest.builder()
                .password("1234")
                .name("홍길동")
                .email("test@test.com")
                .build();

        //when, then
        mockMvc.perform(MockMvcRequestBuilders.post(("/api/v1/users/user/join"))
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("로그인 ID는 필수 입력값입니다."));
    }

    @DisplayName("로그인을 한다.")
    @Test
    void login() throws Exception {
        //given
        User user = User.builder().build();
        when(userService.authenticate(anyString(), anyString()))
                .thenReturn(user);

        UserLoginRequest request = UserLoginRequest.builder()
                .loginId("test")
                .password("1234")
                .build();

        //when, then
        mockMvc.perform(MockMvcRequestBuilders.post(("/api/v1/users/user/login"))
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @DisplayName("로그인할 때, 로그인 ID는 필수값 입력값이다.")
    @Test
    void loginLoginId() throws Exception {
        //given
        User user = User.builder().build();
        when(userService.authenticate(anyString(), anyString()))
                .thenReturn(user);

        UserLoginRequest request = UserLoginRequest.builder()
                .password("1234")
                .build();

        //when, then
        mockMvc.perform(MockMvcRequestBuilders.post(("/api/v1/users/user/login"))
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("로그인 ID는 필수 입력값입니다."));
    }

    @DisplayName("로그아웃한다.")
    @Test
    void logout() throws Exception {
        //given
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionConstants.LOGIN_USER_ID, 1L);

        //when, then
        mockMvc.perform(MockMvcRequestBuilders.get(("/api/v1/users/user/logout"))
                        .session(session)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

}