package mt.movie_theater.api;

import org.springframework.web.bind.annotation.GetMapping;

public class HealthCheckController {

    @GetMapping("/health")
    public String healthCheck() {
        return "Success Health Check";
    }
}
