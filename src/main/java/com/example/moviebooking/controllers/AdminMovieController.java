package com.example.moviebooking.controllers;

import com.example.moviebooking.dto.MovieDto;
import com.example.moviebooking.dto.ShowtimeDto;
import com.example.moviebooking.services.MovieService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/movies")
public class AdminMovieController {
    @Autowired
    private MovieService movieService;


    @PostMapping
    public ResponseEntity<MovieDto> createMovie(@Valid @RequestBody MovieDto movieDto) {
        MovieDto created = movieService.createMovie(movieDto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovieDto> updateMovie(@PathVariable Long id, @Valid @RequestBody MovieDto movieDto) {
        MovieDto updated = movieService.updateMovie(id, movieDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.ok("Movie deleted successfully");
    }

    @GetMapping
    public ResponseEntity<List<MovieDto>> getAllMovies() {
        List<MovieDto> movies = movieService.getAllMovies();
        return ResponseEntity.ok(movies);
    }

    @PostMapping("/{movieId}/showtimes")
    public ResponseEntity<ShowtimeDto> addShowtime(@PathVariable Long movieId, @Valid @RequestBody ShowtimeDto showtimeDto) {
        ShowtimeDto created = movieService.addShowtime(movieId, showtimeDto);
        return ResponseEntity.ok(created);
    }
}