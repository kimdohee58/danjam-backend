package com.danjam.booking;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final BookingService bookingService;

    @GetMapping("/{id}")
    public ResponseEntity<List<BookingResponse>> findAllById(@PathVariable Long id) {
        List<BookingResponse> bookings = bookingService.findByUsersId(id);

        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }
}
