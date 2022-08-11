package com.paloit.training.sp01.controller;
import com.paloit.training.sp01.model.Booking;
import com.paloit.training.sp01.model.Room;
import com.paloit.training.sp01.model.User;
import com.paloit.training.sp01.model.request.CreateBookingPayload;
import com.paloit.training.sp01.model.request.LoginUserPayload;
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
import org.springframework.test.context.jdbc.Sql;

import java.awt.print.Book;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.hamcrest.Matchers.*;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@Sql(value = {"/scripts/setup.sql"})

public class UserControllerTest {
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


    private User user;
    private Room room;
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
    public void getBookingUserById_UserExists_ReturnedSuccessfully_BookingList() {
        var response = given().log().all()
                .with()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/user/{userId}/booking", user.getId())
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].startedAt", equalTo(booking.getStartedAt().toString()))
                .body("[0].endedAt", equalTo(booking.getEndedAt().toString()))
                .body("[0].user.firstName", equalTo(booking.getUser().getFirstName()))
                .body("[0].user.lastName", equalTo(booking.getUser().getLastName()))
                .body("[0].user.email", equalTo(booking.getUser().getEmail()))
                .body("[0].room.id", equalTo(booking.getRoom().getId().toString()))
                .body("[0].room.size", equalTo(booking.getRoom().getSize()));
    }

    @Test
    public void getUserById_UserNotFound_ReturnedFailed_404() {
        given().log().all()
                .with()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/user/{userId}/booking", UUID.randomUUID())
                .then()
                .statusCode(404);
    }

    @Test
    public void createBookingByUserId_UserExistAndTheRoomAvailable_ReturnedSuccessfully_200() {
        var request = new CreateBookingPayload();
        request.setRoomId(room.getId());
        request.setStartedAt(Instant.parse("2022-08-04T18:00:00.982Z"));
        request.setEndedAt(Instant.parse("2022-08-04T19:00:00.982Z"));

        var response = given().log().all()
                .with()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(request)
                .post("/api/user/{userId}/booking", user.getId())
                .then()
                .statusCode(200)
                .body("startedAt", equalTo(request.getStartedAt().toString()))
                .body("endedAt", equalTo(request.getEndedAt().toString()))
                .body("user.firstName", equalTo(user.getFirstName()))
                .body("user.lastName", equalTo(user.getLastName()))
                .body("user.email", equalTo(user.getEmail()))
                .body("room.id", equalTo(room.getId().toString()))
                .body("room.size", equalTo(room.getSize()));
    }

    @Test
    public void createBookingByUserId_UserNotExist_ReturnedFailed_404() {
        var request = new CreateBookingPayload();
        request.setRoomId(room.getId());
        request.setStartedAt(Instant.parse("2022-08-04T18:00:00.982Z"));
        request.setEndedAt(Instant.parse("2022-08-04T19:00:00.982Z"));

        var response = given().log().all()
                .with()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(request)
                .post("/api/user/{userId}/booking", UUID.randomUUID())
                .then()
                .statusCode(404);
    }

    @Test
    public void createBookingByUserId_RoomNotExist_ReturnedFailed_404() {
        var request = new CreateBookingPayload();
        request.setRoomId(UUID.randomUUID());
        request.setStartedAt(Instant.parse("2022-08-04T18:00:00.982Z"));
        request.setEndedAt(Instant.parse("2022-08-04T19:00:00.982Z"));

        var response = given().log().all()
                .with()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(request)
                .post("/api/user/{userId}/booking", user.getId())
                .then()
                .statusCode(404);
    }

    @Test
    public void createBookingByUserId_RoomNotAvailableThatTime_ReturnedFailed_404() {
        var request = new CreateBookingPayload();
        request.setRoomId(room.getId());
        request.setStartedAt(Instant.parse("2022-08-04T16:00:00.982Z"));
        request.setEndedAt(Instant.parse("2022-08-04T17:00:00.982Z"));

        var response = given().log().all()
                .with()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(request)
                .post("/api/user/{userId}/booking", user.getId())
                .then()
                .statusCode(404);
    }

    @Test
    public void register_ReturnedSuccessfully_200() {
        var request = fileReader.getFile("request/userController/register_ReturnedSuccessfully_200.json");
        var response = given().log().all()
                .with()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(request)
                .post("/api/register")
                .then()
                .statusCode(200)
                .extract()
                .asString();
        var expectedResponse = fileReader.getFile(
                "response/userController/register_ReturnedSuccessfully_200.json"
        );
        assertThatJson(response).isEqualTo(expectedResponse);
    }

    @Test
    public void login_PasswordInvalid_ReturnedFailed_Unauthorized401() {
        LoginUserPayload request = new LoginUserPayload();
        request.setEmail("abc@def.com");
        request.setPassword("passwordEiEi");

        given().log().all()
            .with()
            .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .when()
            .body(request)
            .post("/api/login")
            .then()
            .statusCode(401);
    }

    @Test
    public void login_UserNotFound_ReturnedFailed_NotFound404() {
        LoginUserPayload request = new LoginUserPayload();
        request.setEmail("abc@defghij.com");
        request.setPassword("passwordEiEi");

        given().log().all()
                .with()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(request)
                .post("/api/login")
                .then()
                .statusCode(404);
    }

    @Test
    public void login_PasswordCorrect_ReturnedSuccessfully_200() {
        LoginUserPayload request = new LoginUserPayload();
        request.setEmail("abc@def.com");
        request.setPassword("password");

        given().log().all()
            .with()
            .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .when()
            .body(request)
            .post("/api/login")
            .then()
            .statusCode(200);
    }
}