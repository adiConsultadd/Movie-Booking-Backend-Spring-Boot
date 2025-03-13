package com.example.moviebooking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShowtimeDto {
    private Long id;
    private Long movieId;
    private String movieTitle;
    private LocalDateTime startTime;
    private String screenNumber;
    private Double price;
    private Integer availableSeats;
    private Integer totalSeats;
}