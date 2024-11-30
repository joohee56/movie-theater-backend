package mt.movie_theater.api.user.service;

import static mt.movie_theater.api.user.constants.SessionConstants.LOGIN_USER_ID;
import static org.assertj.core.api.Assertions.assertThat;

import mt.movie_theater.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

class SessionServiceTest extends IntegrationTestSupport {
    @Autowired
    private SessionService sessionService;

    @DisplayName("로그인 세션을 생성한다.")
    @Test
    void createSession() {
        //given
        MockHttpServletRequest request = new MockHttpServletRequest();
        Long userId = Long.valueOf(1);

        //when
        sessionService.createLoginSession(request, userId);

        //then
        assertThat(request.getSession().getAttribute(LOGIN_USER_ID)).isEqualTo(userId);
        assertThat(request.getSession().getMaxInactiveInterval()).isEqualTo(60*10);
    }

    @DisplayName("세션을 제거한다.")
    @Test
    void removeSession() {
        //given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpSession session = new MockHttpSession();
        request.setSession(session);

        session.setAttribute(LOGIN_USER_ID, Long.valueOf(1));

        //when
        sessionService.removeSession(request);

        //then
        assertThat(request.getSession(false)).isNull();
    }


}