package com.paloit.training.sp01.controller;
import com.paloit.training.sp01.model.Booking;
import com.paloit.training.sp01.model.GetBookingResponse;
import com.paloit.training.sp01.model.User;
import com.paloit.training.sp01.model.request.CreateBookingPayload;
import com.paloit.training.sp01.model.request.CreateUserPayload;
import com.paloit.training.sp01.model.request.LoginUserPayload;
import com.paloit.training.sp01.repository.BookingRepository;
import com.paloit.training.sp01.repository.RoomRepository;
import com.paloit.training.sp01.repository.UserRepository;
import org.hibernate.mapping.Any;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("api")
public class UserController {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;

    public UserController(UserRepository userRepository, BookingRepository bookingRepository, RoomRepository roomRepository) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
    }

    @PostMapping(value = "/register", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<User> register(@RequestBody CreateUserPayload body) {
        var newUser = new User();
        newUser.setFirstName(body.getFirstname());
        newUser.setLastName(body.getLastname());
        newUser.setEmail(body.getEmail());
        newUser.setPassword(body.getPassword());
        var response = userRepository.save(newUser);

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/login", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<User> login(@RequestBody LoginUserPayload body) {
        var user = userRepository.findUserByEmail(body.getEmail());
        if(user.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "user not found"
            );
        }
        Boolean validateLogin = user.get().getPassword().equals(body.getPassword());

        if(!validateLogin) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Password invalid"
            );
        }

        return ResponseEntity.ok(user.get());
    }

    @GetMapping("/user/{userId}/booking")
    public ResponseEntity<List<Booking>> getBookings(@PathVariable UUID userId) {
        var response = new ArrayList<Booking>();
        var user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "user not found"
            );
        }
        user.ifPresent(userData -> {
            response.addAll(userData.getBookings());
        });
        return ResponseEntity.ok(response);
    }

    @PostMapping("/user/{userId}/booking")
    public ResponseEntity<GetBookingResponse> createNewBooking(@PathVariable UUID userId, @RequestBody CreateBookingPayload body) {
        System.out.println(body.toString());
        var user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "user not found"
            );
        }

        var room = roomRepository.findById(body.getRoomId());
        if (room.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "room not found"
            );
        }
        var booking = bookingRepository.findBooking(body.getRoomId(), body.getStartedAt(), body.getEndedAt());
        if(booking.size() > 0) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Cannot booking, the room is not available"
            );
        }
        var newBooking = new Booking();
        newBooking.setEndedAt(body.getEndedAt());
        newBooking.setStartedAt(body.getStartedAt());
        newBooking.setRoom(room.get());
        newBooking.setUser(user.get());
        bookingRepository.save(newBooking);
        var response = new GetBookingResponse();
        response.setUser(user.get());
        response.setRoom(room.get());
        response.setStartedAt(body.getStartedAt());
        response.setEndedAt(body.getEndedAt());

        return ResponseEntity.ok(response);
    }
}
