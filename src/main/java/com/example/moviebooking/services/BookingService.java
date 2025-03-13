package com.example.moviebooking.services;

import com.example.moviebooking.dto.BookingDto;
import com.example.moviebooking.dto.BookingRequest;
import com.example.moviebooking.exceptions.ResourceNotFoundException;
import com.example.moviebooking.models.Booking;
import com.example.moviebooking.models.Showtime;
import com.example.moviebooking.models.User;
import com.example.moviebooking.repositories.BookingRepository;
import com.example.moviebooking.repositories.ShowtimeRepository;
import com.example.moviebooking.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private SecurityService securityService;

    //This method gets the current user details
    protected User getCurrentUser() {
        String username = securityService.getCurrentUsername();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private BookingDto convertToDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setShowtimeId(booking.getShowtime().getId());
        dto.setMovieTitle(booking.getShowtime().getMovie().getTitle());
        dto.setShowtimeStartTime(booking.getShowtime().getStartTime());
        dto.setScreenNumber(booking.getShowtime().getScreenNumber());
        dto.setNumberOfSeats(booking.getNumberOfSeats());
        dto.setTotalPrice(booking.getTotalPrice());
        dto.setBookingTime(booking.getBookingTime());
        dto.setActive(booking.isActive());
        return dto;
    }

    @Transactional
    public BookingDto bookTickets(BookingRequest bookingRequest){
        User user = getCurrentUser();

        Showtime showtime = showtimeRepository.findById(bookingRequest.getShowtimeId())
                .orElseThrow(() -> new ResourceNotFoundException("Showtime not found"));

        if (showtime.getStartTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Cannot book tickets for a showtime that has already ended");
        }

        Optional<Booking> existingBooking = bookingRepository.findByUserAndShowtime(user, showtime).stream().findFirst();
        if (existingBooking.isPresent()) {
            throw new RuntimeException("You already have a booking for this showtime");
        }

        if (showtime.getAvailableSeats() < bookingRequest.getNumberOfSeats()) {
            throw new RuntimeException("Not enough seats available for this showtime");
        }

        double totalPrice = showtime.getPrice() * bookingRequest.getNumberOfSeats();

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setShowtime(showtime);
        booking.setNumberOfSeats(bookingRequest.getNumberOfSeats());
        booking.setTotalPrice(totalPrice);
        booking.setBookingTime(LocalDateTime.now());
        booking.setActive(true);

        showtime.setAvailableSeats(showtime.getAvailableSeats() - bookingRequest.getNumberOfSeats());
        showtimeRepository.save(showtime);

        Booking savedBooking = bookingRepository.save(booking);

        return convertToDto(savedBooking);
    }

    @Transactional
    public void cancelBooking(Long bookingId) {
        User user = getCurrentUser();

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only cancel your own bookings");
        }

        Showtime showtime = booking.getShowtime();
        showtime.setAvailableSeats(showtime.getAvailableSeats() + booking.getNumberOfSeats());
        showtimeRepository.save(showtime);

        bookingRepository.delete(booking);
    }

    public List<BookingDto> getUserBookingHistory() {
        User user = getCurrentUser();
        List<Booking> bookings = bookingRepository.findByUserOrderByBookingTimeDesc(user);
        return bookings.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<BookingDto> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAllByOrderByBookingTimeDesc();
        return bookings.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}