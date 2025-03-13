package com.example.moviebooking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDto {
    private Long id;
    private String title;
    private String description;
    private String genre;
    private int durationMinutes;
    private String director;
    private String releaseDate;
    private String posterUrl;
//    private List<ShowtimeDto> showtimes;
}