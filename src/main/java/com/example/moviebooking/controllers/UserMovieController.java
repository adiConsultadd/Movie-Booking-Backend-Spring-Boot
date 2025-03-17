package com.example.moviebooking.controllers;

import com.example.moviebooking.dto.MovieDto;
import com.example.moviebooking.dto.ShowtimeDto;
import com.example.moviebooking.services.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class UserMovieController {
    @Autowired
    private MovieService movieService;

    @GetMapping
    public ResponseEntity<List<MovieDto>> getAllMovies() {
        List<MovieDto> movies = movieService.getAllMovies();
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieDto> getMovieById(@PathVariable Long id) {
        MovieDto movie = movieService.getMovieById(id);
        return ResponseEntity.ok(movie);
    }

    @GetMapping("/{id}/showtimes")
    public ResponseEntity<List<ShowtimeDto>> getMovieShowtimes(@PathVariable Long id) {
        List<ShowtimeDto> showtimes = movieService.getUpcomingShowtimes(id);
        return ResponseEntity.ok(showtimes);
    }
}