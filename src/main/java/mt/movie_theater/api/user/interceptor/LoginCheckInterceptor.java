package mt.movie_theater.api.user.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import mt.movie_theater.api.exception.UnauthorizedException;
import mt.movie_theater.api.user.annotation.LoginCheck;
import mt.movie_theater.api.user.constants.SessionConstants;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        log.info("인증 체크 인터셉터 실행 {}", requestURI);

        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            if (!handlerMethod.getMethod().isAnnotationPresent(LoginCheck.class)) {
                return true;
            }

            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute(SessionConstants.LOGIN_USER_ID) == null) {
                log.info("미인증 사용자 오류");
                throw new UnauthorizedException("로그인이 필요한 서비스입니다.");
            }
        }

        return true;
    }
}
