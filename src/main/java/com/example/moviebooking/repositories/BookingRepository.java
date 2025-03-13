package com.example.moviebooking.repositories;

import com.example.moviebooking.models.Booking;
import com.example.moviebooking.models.Showtime;
import com.example.moviebooking.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserOrderByBookingTimeDesc(User user);
    List<Booking> findAllByOrderByBookingTimeDesc();
    void deleteAllByShowtimeIdIn(List<Long> showtimeIds);
    Optional<Booking> findByUserAndShowtime(User user, Showtime showtime);
}