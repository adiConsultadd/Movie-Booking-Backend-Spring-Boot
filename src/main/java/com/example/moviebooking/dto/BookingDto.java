package com.example.moviebooking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private Long id;
    private Long showtimeId;
    private String movieTitle;
    private LocalDateTime showtimeStartTime;
    private String screenNumber;
    private Integer numberOfSeats;
    private Double totalPrice;
    private LocalDateTime bookingTime;
    private boolean active;
}