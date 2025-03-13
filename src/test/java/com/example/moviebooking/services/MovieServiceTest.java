package com.example.moviebooking.services;

import com.example.moviebooking.dto.MovieDto;
import com.example.moviebooking.dto.ShowtimeDto;
import com.example.moviebooking.exceptions.ResourceNotFoundException;
import com.example.moviebooking.models.Movie;
import com.example.moviebooking.models.Showtime;
import com.example.moviebooking.repositories.BookingRepository;
import com.example.moviebooking.repositories.MovieRepository;
import com.example.moviebooking.repositories.ShowtimeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private ShowtimeRepository showtimeRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private MovieService movieService;

    private Movie testMovie;
    private MovieDto testMovieDto;
    private Showtime testShowtime;
    private ShowtimeDto testShowtimeDto;
    private List<Movie> testMovieList;
    private List<Showtime> testShowtimeList;

    //This runs before each test and initializes the things required to perform the tests
    @BeforeEach
    void setUp() {
        //Test Movie
        testMovie = new Movie();
        testMovie.setId(1L);
        testMovie.setTitle("Test Movie");
        testMovie.setDescription("Test Description");
        testMovie.setGenre("Action");
        testMovie.setDurationMinutes(120);
        testMovie.setDirector("Test Director");
        testMovie.setReleaseDate("2025-01-01");
        testMovie.setPosterUrl("http://example.com/poster.jpg");

        //Test Movie Dto (Sent in the request body)
        testMovieDto = new MovieDto();
        testMovieDto.setId(1L);
        testMovieDto.setTitle("Test Movie");
        testMovieDto.setDescription("Test Description");
        testMovieDto.setGenre("Action");
        testMovieDto.setDurationMinutes(120);
        testMovieDto.setDirector("Test Director");
        testMovieDto.setReleaseDate("2025-01-01");
        testMovieDto.setPosterUrl("http://example.com/poster.jpg");

        //Test Showtime
        testShowtime = new Showtime();
        testShowtime.setId(1L);
        testShowtime.setMovie(testMovie);
        testShowtime.setStartTime(LocalDateTime.now().plusDays(1));
        testShowtime.setScreenNumber("Screen 1");
        testShowtime.setPrice(10.0);
        testShowtime.setTotalSeats(100);
        testShowtime.setAvailableSeats(100);

        //Test Showtime Dto (Sent in the request body)
        testShowtimeDto = new ShowtimeDto();
        testShowtimeDto.setId(1L);
        testShowtimeDto.setMovieId(1L);
        testShowtimeDto.setMovieTitle("Test Movie");
        testShowtimeDto.setStartTime(LocalDateTime.now().plusDays(1));
        testShowtimeDto.setScreenNumber("Screen 1");
        testShowtimeDto.setPrice(10.0);
        testShowtimeDto.setTotalSeats(100);
        testShowtimeDto.setAvailableSeats(100);

        // Add showtime to movie
        testMovie.getShowtimes().add(testShowtime);

        // Initialize test lists
        testMovieList = new ArrayList<>();
        testMovieList.add(testMovie);

        testShowtimeList = new ArrayList<>();
        testShowtimeList.add(testShowtime);
    }

    //Testing Create Movie Service
    @Test
    void createMovieTest(){
        when(movieRepository.save(any(Movie.class))).thenReturn(testMovie);

        MovieDto result = movieService.createMovie(testMovieDto);

        assertNotNull(result);
        assertEquals(testMovieDto.getTitle(), result.getTitle());
        //This ensures that the movie repository was called exactly once
        verify(movieRepository, times(1)).save(any(Movie.class));
    }

    //Testing Get All Movies Service
    @Test
    void getAllMoviesTest() {
        when(movieRepository.findAll()).thenReturn(testMovieList);

        List<MovieDto> result = movieService.getAllMovies();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testMovieDto.getTitle(), result.get(0).getTitle());
        verify(movieRepository, times(1)).findAll();
    }

    //Testing Get Movie By Id Service
    @Test
    void getMovieByIdWithValidIdTest() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(testMovie));

        MovieDto result = movieService.getMovieById(1L);

        assertNotNull(result);
        assertEquals(testMovieDto.getId(), result.getId());
        assertEquals(testMovieDto.getTitle(), result.getTitle());
        verify(movieRepository, times(1)).findById(1L);
    }

    //Testing Get Movie By Invalid Id Service
    @Test
    void getMovieByIdWithInvalidIdTest() {
        when(movieRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            movieService.getMovieById(999L);
        });
        verify(movieRepository, times(1)).findById(999L);
    }

    //Testing Update Movie With Id Service
    @Test
    void updateMovieWithValidIdTest(){
        when(movieRepository.findById(1L)).thenReturn(Optional.of(testMovie));
        when(movieRepository.save(any(Movie.class))).thenReturn(testMovie);

        testMovieDto.setTitle("Updated Movie Title");

        MovieDto result = movieService.updateMovie(1L, testMovieDto);

        assertNotNull(result);
        assertEquals("Updated Movie Title", result.getTitle());
        verify(movieRepository, times(1)).findById(1L);
        verify(movieRepository, times(1)).save(any(Movie.class));
    }

    //Testing Update Movie With Invalid Id Service
    @Test
    void updateMovieWithInvalidIdTest() {
        when(movieRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            movieService.updateMovie(999L, testMovieDto);
        });
        verify(movieRepository, times(1)).findById(999L);
        verify(movieRepository, never()).save(any(Movie.class));
    }

    //Testing Delete Movie With Valid Id Service
    @Test
    void deleteMovieWthValidIdTest() {
        when(movieRepository.existsById(1L)).thenReturn(true);
        when(showtimeRepository.findByMovieId(1L)).thenReturn(testShowtimeList);
        doNothing().when(bookingRepository).deleteAllByShowtimeIdIn(anyList());
        doNothing().when(showtimeRepository).deleteAllByMovieId(anyLong());
        doNothing().when(movieRepository).deleteById(anyLong());

        movieService.deleteMovie(1L);

        verify(movieRepository, times(1)).existsById(1L);
        verify(showtimeRepository, times(1)).findByMovieId(1L);
        verify(bookingRepository, times(1)).deleteAllByShowtimeIdIn(anyList());
        verify(showtimeRepository, times(1)).deleteAllByMovieId(1L);
        verify(movieRepository, times(1)).deleteById(1L);
    }

    //Testing Delete Movie With Invalid Id Service
    @Test
    void deleteMovieWithInvalidIdTest() {
        when(movieRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            movieService.deleteMovie(999L);
        });
        verify(movieRepository, times(1)).existsById(999L);
        verify(showtimeRepository, never()).findByMovieId(anyLong());
        verify(bookingRepository, never()).deleteAllByShowtimeIdIn(anyList());
        verify(showtimeRepository, never()).deleteAllByMovieId(anyLong());
        verify(movieRepository, never()).deleteById(anyLong());
    }

    //Testing Adding A New Showtime To A Movie With Id Service
    @Test
    void addShowtimeWithValidMovieIdTest(){
        when(movieRepository.findById(1L)).thenReturn(Optional.of(testMovie));
        when(showtimeRepository.save(any(Showtime.class))).thenReturn(testShowtime);

        ShowtimeDto result = movieService.addShowtime(1L, testShowtimeDto);

        assertNotNull(result);
        assertEquals(testShowtimeDto.getScreenNumber(), result.getScreenNumber());
        assertEquals(testShowtimeDto.getPrice(), result.getPrice());
        verify(movieRepository, times(1)).findById(1L);
        verify(showtimeRepository, times(1)).save(any(Showtime.class));
    }

    //Testing Adding A New Showtime To A Movie With Invalid Id Service
    @Test
    void addShowtimeWithInvalidMovieIdTest() {
        when(movieRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            movieService.addShowtime(999L, testShowtimeDto);
        });
        verify(movieRepository, times(1)).findById(999L);
        verify(showtimeRepository, never()).save(any(Showtime.class));
    }

    //Testing Getting All The Upcoming Showtime For A Movie With Id Service
    @Test
    void getUpcomingShowtimesWithValidMovieIdTest() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(testMovie));
        when(showtimeRepository.findByMovieAndStartTimeAfter(eq(testMovie), any(LocalDateTime.class)))
                .thenReturn(testShowtimeList);


        List<ShowtimeDto> result = movieService.getUpcomingShowtimes(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testShowtimeDto.getScreenNumber(), result.get(0).getScreenNumber());
        verify(movieRepository, times(1)).findById(1L);
        verify(showtimeRepository, times(1)).findByMovieAndStartTimeAfter(eq(testMovie), any(LocalDateTime.class));
    }

    //Testing Getting All The Upcoming Showtime For A Movie With Invalid Id Service
    @Test
    void getUpcomingShowtimesWthInvalidMovieIdTest() {
        when(movieRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            movieService.getUpcomingShowtimes(999L);
        });

        verify(movieRepository, times(1)).findById(999L);
        verify(showtimeRepository, never()).findByMovieAndStartTimeAfter(any(Movie.class), any(LocalDateTime.class));
    }
}