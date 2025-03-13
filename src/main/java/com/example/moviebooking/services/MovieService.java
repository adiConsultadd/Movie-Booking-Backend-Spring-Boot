package com.example.moviebooking.services;

import com.example.moviebooking.dto.MovieDto;
import com.example.moviebooking.dto.ShowtimeDto;
import com.example.moviebooking.exceptions.ResourceNotFoundException;
import com.example.moviebooking.models.Movie;
import com.example.moviebooking.models.Showtime;
import com.example.moviebooking.repositories.BookingRepository;
import com.example.moviebooking.repositories.MovieRepository;
import com.example.moviebooking.repositories.ShowtimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieService {
    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private MovieDto convertToDto(Movie movie) {
        MovieDto dto = new MovieDto();
        dto.setId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setDescription(movie.getDescription());
        dto.setGenre(movie.getGenre());
        dto.setDurationMinutes(movie.getDurationMinutes());
        dto.setDirector(movie.getDirector());
        dto.setReleaseDate(movie.getReleaseDate());
        dto.setPosterUrl(movie.getPosterUrl());

        List<ShowtimeDto> showtimeDtos = movie.getShowtimes().stream()
                .map(this::convertToShowtimeDto)
                .collect(Collectors.toList());
        return dto;
    }

    private ShowtimeDto convertToShowtimeDto(Showtime showtime) {
        ShowtimeDto dto = new ShowtimeDto();
        dto.setId(showtime.getId());
        dto.setMovieId(showtime.getMovie().getId());
        dto.setMovieTitle(showtime.getMovie().getTitle());
        dto.setStartTime(showtime.getStartTime());
        dto.setScreenNumber(showtime.getScreenNumber());
        dto.setPrice(showtime.getPrice());
        dto.setAvailableSeats(showtime.getAvailableSeats());
        dto.setTotalSeats(showtime.getTotalSeats());
        return dto;
    }

    @Transactional
    public MovieDto createMovie(MovieDto movieDto){
        Movie movie = new Movie();
        movie.setTitle(movieDto.getTitle());
        movie.setDescription(movieDto.getDescription());
        movie.setGenre(movieDto.getGenre());
        movie.setDurationMinutes(movieDto.getDurationMinutes());
        movie.setDirector(movieDto.getDirector());
        movie.setReleaseDate(movieDto.getReleaseDate());
        movie.setPosterUrl(movieDto.getPosterUrl());

        Movie savedMovie = movieRepository.save(movie);

        return convertToDto(savedMovie);
    }

    public List<MovieDto> getAllMovies() {
        return movieRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public MovieDto getMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));
        return convertToDto(movie);
    }

    @Transactional
    public MovieDto updateMovie(Long id, MovieDto movieDto) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));

        movie.setTitle(movieDto.getTitle());
        movie.setDescription(movieDto.getDescription());
        movie.setGenre(movieDto.getGenre());
        movie.setDurationMinutes(movieDto.getDurationMinutes());
        movie.setDirector(movieDto.getDirector());
        movie.setReleaseDate(movieDto.getReleaseDate());
        movie.setPosterUrl(movieDto.getPosterUrl());

        Movie updatedMovie = movieRepository.save(movie);

        return convertToDto(updatedMovie);
    }

    @Transactional
    public void deleteMovie(Long movieId) {
        if (!movieRepository.existsById(movieId)) {
            throw new ResourceNotFoundException("Movie not found with id: " + movieId);
        }

        List<Long> showtimeIds = showtimeRepository.findByMovieId(movieId)
                .stream()
                .map(Showtime::getId)
                .collect(Collectors.toList());

        if (!showtimeIds.isEmpty()) {
            bookingRepository.deleteAllByShowtimeIdIn(showtimeIds);
        }

        showtimeRepository.deleteAllByMovieId(movieId);

        movieRepository.deleteById(movieId);
    }

    @Transactional
    public ShowtimeDto addShowtime(Long movieId, ShowtimeDto showtimeDto) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + movieId));

        Showtime showtime = new Showtime();
        showtime.setMovie(movie);
        showtime.setStartTime(showtimeDto.getStartTime());
        showtime.setScreenNumber(showtimeDto.getScreenNumber());
        showtime.setPrice(showtimeDto.getPrice());
        showtime.setTotalSeats(showtimeDto.getTotalSeats());
        showtime.setAvailableSeats(showtimeDto.getTotalSeats());

        Showtime savedShowtime = showtimeRepository.save(showtime);

        return convertToShowtimeDto(savedShowtime);
    }

    public List<ShowtimeDto> getUpcomingShowtimes(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + movieId));

        List<Showtime> showtimes = showtimeRepository.findByMovieAndStartTimeAfter(movie, LocalDateTime.now());

        return showtimes.stream()
                .map(this::convertToShowtimeDto)
                .collect(Collectors.toList());
    }
}