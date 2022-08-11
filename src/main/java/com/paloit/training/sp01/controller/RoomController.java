package com.paloit.training.sp01.controller;

import com.paloit.training.sp01.model.Booking;
import com.paloit.training.sp01.model.Room;
import com.paloit.training.sp01.repository.RoomRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("api")
public class RoomController {
    private final RoomRepository roomRepository;
    public RoomController(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @GetMapping("/rooms")
    public ResponseEntity<ArrayList<Room>> getRooms(@RequestParam Integer roomSize, @RequestParam(required = false) String startedAt, @RequestParam(required = false) String endedAt) {
        var response = new ArrayList<Room>();
        roomRepository.findAvailableRooms(roomSize, Instant.parse(startedAt), Instant.parse(endedAt)).forEach(room -> response.add(room));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/rooms")
    public ResponseEntity<Room> createRoom(@RequestBody Room room) {
        Room newRoom = roomRepository.save(room);
        return ResponseEntity.ok(newRoom);
    }

    @GetMapping("/rooms/{roomId}/bookings")
    public ResponseEntity<List<Booking>> getRoomBookingByRoomId(@PathVariable UUID roomId) {
        var result = new ArrayList<Booking>();
        Optional<Room> roomOpt = roomRepository.findById(roomId);
        if (roomOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        roomOpt.ifPresent(room -> {
            result.addAll(room.getBookings());
        });
        return ResponseEntity.ok(result);
    }
}
