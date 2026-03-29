package com.example.marinecrm.domain.user;

import com.example.marinecrm.domain.user.DTO.UserRequest;
import com.example.marinecrm.domain.user.DTO.UserResponse;
import com.example.marinecrm.domain.user.DTO.UserUpdateRequest;
import com.example.marinecrm.domain.user.Services.*;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController()
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class UserController {

    private final CreateUserService createUserService;
    private final DeleteUserService deleteUserService;
    private final GetAllUserService getAllUserService;
    private final GetUserByIdService getUserByIdService;
    private final UpdateUserService updateUserService;

    @PostMapping()
    public ResponseEntity<UserResponse> createUser(@Validated @RequestBody UserRequest request) {
        return ResponseEntity.ok(createUserService.execute(request));
    }

    @GetMapping()
    public ResponseEntity<Page<UserResponse>> getUsers(@ParameterObject @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(getAllUserService.execute(pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(deleteUserService.execute(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(getUserByIdService.execute(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable UUID id, @Validated @RequestBody UserRequest request) {
        return ResponseEntity.ok(updateUserService.execute(new UserUpdateRequest(id, request)));
    }
}
