package com.example.moviebooking.repositories;

import com.example.moviebooking.models.Movie;
import com.example.moviebooking.models.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {
    List<Showtime> findByMovieAndStartTimeAfter(Movie movie, LocalDateTime startTime);
    List<Showtime> findByMovieId(Long movieId);
    void deleteAllByMovieId(Long movieId);
}