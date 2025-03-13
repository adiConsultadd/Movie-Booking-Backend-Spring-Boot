package com.example.moviebooking.controllers;

import com.example.moviebooking.dto.BookingDto;
import com.example.moviebooking.dto.BookingRequest;
import com.example.moviebooking.services.BookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserBookingController {
    @Autowired
    private BookingService bookingService;

    @PostMapping("/movies/book")
    public ResponseEntity<BookingDto> bookTickets(@Valid @RequestBody BookingRequest bookingRequest) {
        BookingDto booking = bookingService.bookTickets(bookingRequest);
        return ResponseEntity.ok(booking);
    }

    @DeleteMapping("/movies/{id}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable("id") Long bookingId) {
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.ok("Booking Deleted");
    }

    @GetMapping("/movies/history")
    public ResponseEntity<List<BookingDto>> getBookingHistory() {
        List<BookingDto> bookings = bookingService.getUserBookingHistory();
        return ResponseEntity.ok(bookings);
    }
}