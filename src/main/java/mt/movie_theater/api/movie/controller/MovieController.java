package mt.movie_theater.api.movie.controller;

import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mt.movie_theater.api.apiResponse.ApiResponse;
import mt.movie_theater.api.movie.request.MovieCreateRequest;
import mt.movie_theater.api.movie.request.MovieWatchableRequest;
import mt.movie_theater.api.movie.response.MovieResponse;
import mt.movie_theater.api.movie.response.MovieWatchableResponse;
import mt.movie_theater.api.movie.service.MovieService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/movies")
public class MovieController {
    private final MovieService movieService;

    @PostMapping(value = "/new", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<MovieResponse> createMovie(@Valid @ModelAttribute MovieCreateRequest request) throws IOException {
        MovieResponse response = movieService.createMovie(request);
        return ApiResponse.ok(response);
    }

    @GetMapping
    public ApiResponse<List<MovieResponse>> getAllMovies() {
        return ApiResponse.ok(movieService.getAllMovies());
    }

    @GetMapping("/recent")
    public ApiResponse<List<MovieResponse>> getRecentMovies() {
        return ApiResponse.ok(movieService.getRecentMovies());
    }

    @GetMapping("/watchable")
    public ApiResponse<List<MovieWatchableResponse>> getMoviesWithIsWatchable(@Valid @ModelAttribute MovieWatchableRequest request) {
        List<MovieWatchableResponse> movieResponses = movieService.getMoviesWithIsWatchable(request.getDate(), request.getTheaterId());
        return ApiResponse.ok(movieResponses);
    }

}
