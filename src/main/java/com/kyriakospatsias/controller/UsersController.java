package com.kyriakospatsias.controller;

import com.kyriakospatsias.model.User;
import com.kyriakospatsias.repository.UsersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UsersController {

    Logger logger = LoggerFactory.getLogger(UsersController.class);

    private final UsersRepository usersRepository;

    public UsersController(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @GetMapping
    public List<User> getAllUsers(){
        logger.debug("Getting all users");
        List<User> users = new ArrayList<>();
        usersRepository.findAll().forEach(users::add);
        return users;
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<User> getUserById(@PathVariable long id){
        logger.debug("Getting user by id {}", id);
        return usersRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user){
        logger.debug("Creating user {}", user);
        user.setCreatedOn(LocalDateTime.now(ZoneId.of("UTC")));
        user.setUpdatedOn(LocalDateTime.now(ZoneId.of("UTC")));
        User savedUser = usersRepository.save(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<User> updateUser(@PathVariable long id, @RequestBody User user){
        logger.debug("Updating user {}", user);
        return usersRepository.findById(id)
                .map(existingUser -> {
                    if (user.getCognitoId() != null && !user.getCognitoId().isEmpty()) {
                        existingUser.setCognitoId(user.getCognitoId());
                    }
                    if (user.getUsername() != null && !user.getUsername().isEmpty()) {
                        existingUser.setUsername(user.getUsername());
                    }
                    if (user.getFirstName() != null && !user.getFirstName().isEmpty()) {
                        existingUser.setFirstName(user.getFirstName());
                    }
                    if (user.getLastName() != null && !user.getLastName().isEmpty()) {
                        existingUser.setLastName(user.getLastName());
                    }
                    if (user.getTimezone() != null && !user.getTimezone().isEmpty()) {
                        existingUser.setTimezone(user.getTimezone());
                    }
                    if(user.getStatus() != null && !user.getStatus().isEmpty()) {
                        existingUser.setStatus(user.getStatus());
                    }
                    existingUser.setUpdatedOn(LocalDateTime.now(ZoneId.of("UTC")));
                    return ResponseEntity.ok(usersRepository.save(existingUser));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable long id){
        logger.debug("Deleting user {}", id);
        if(usersRepository.existsById(id)){
            usersRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
