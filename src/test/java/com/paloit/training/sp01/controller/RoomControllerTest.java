package com.paloit.training.sp01.controller;
import com.paloit.training.sp01.model.Booking;
import com.paloit.training.sp01.model.Room;
import com.paloit.training.sp01.model.User;
import com.paloit.training.sp01.repository.BookingRepository;
import com.paloit.training.sp01.repository.RoomRepository;
import com.paloit.training.sp01.repository.UserRepository;
import com.paloit.training.sp01.utils.FileReader;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;

import java.time.Instant;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)

public class RoomControllerTest {
    @Autowired
    FileReader fileReader;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    BookingRepository bookingRepository;
    @LocalServerPort
    private int port;
    @BeforeAll
    public void beforeAll() {
        RestAssured.baseURI = "http://localhost:" + port;
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    private Room room;
    private User user;

    private Booking booking;

    @BeforeEach
    public void beforeEach() {
        User newUser = new User();
        newUser.setEmail("abc@def.com");
        newUser.setPassword("password");
        newUser.setFirstName("firstName");
        newUser.setLastName("lastName");

        user = userRepository.save(newUser);

        Room newRoom = new Room();
        newRoom.setSize(5);
        room = roomRepository.save(newRoom);

        Booking newBooking = new Booking();
        newBooking.setUser(user);
        newBooking.setRoom(room);
        newBooking.setStartedAt(Instant.parse("2022-08-04T16:00:00.982Z"));
        newBooking.setEndedAt(Instant.parse("2022-08-04T17:00:00.982Z"));
        booking = bookingRepository.save(newBooking);
    }

    @AfterEach
    public void afterEach() {
        bookingRepository.deleteAll();
        userRepository.deleteAll();
        roomRepository.deleteAll();
    }

    @Test
    public void createRoom_ReturnedNewRoomSuccessfully() {
        var request = fileReader.getFile("request/roomController/createRoom_ReturnedNewRoomSuccessfully.json");
        var response = given().log().all()
                .with()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(request)
                .post("/api/rooms")
                .then()
                .assertThat()
                .statusCode(200)
                .body("size", equalTo(5));
    }

    @Test
    public void getRooms_FilterWithStartedAtAndEndedAt_ReturnedRoomAvailableSuccessfully() {
        String startedAtFilter = "2022-08-04T18:00:00Z";
        String endedAtFilter = "2022-08-04T19:00:00Z";
        int sizeFilter = 2;

        given().log().all()
                .with()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .when()
                .param("startedAt", startedAtFilter)
                .param("endedAt", endedAtFilter)
                .param("roomSize", sizeFilter)
                .get("/api/rooms")
                .then()
                .assertThat()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].size", equalTo(room.getSize()))
                .body("[0].id", equalTo(room.getId().toString()));

    }

    @Test
    public void getRoomBookingByRoomId_RoomExisted_ReturnedBookingDataSuccessfully() {
        given().log().all()
                .with()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/api/rooms/{roomId}/bookings", room.getId())
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].startedAt", equalTo(booking.getStartedAt().toString()))
                .body("[0].endedAt", equalTo(booking.getEndedAt().toString()))
                .body("[0].user.firstName", equalTo(user.getFirstName()))
                .body("[0].user.lastName", equalTo(user.getLastName()))
                .body("[0].user.email", equalTo(user.getEmail()))
                .body("[0].room.id", equalTo(room.getId().toString()))
                .body("[0].room.size", equalTo(room.getSize()));

    }

    @Test
    public void getRoomBookingByRoomId_RoomNotExisted_ReturnedFailed_404() {
        given().log().all()
                .with()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/api/rooms/{roomId}/bookings", UUID.randomUUID())
                .then()
                .statusCode(404);

    }
}
