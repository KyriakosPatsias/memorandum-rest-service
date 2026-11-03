package com.kyriakospatsias;

import com.kyriakospatsias.controller.UsersController;
import com.kyriakospatsias.model.User;
import com.kyriakospatsias.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsersControllerTest {

    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private UsersController usersController;

    private User existingUser;

    @BeforeEach
    void setUp() {
        existingUser = new User();
        existingUser.setId(1L);
        existingUser.setCognitoId("123456");
        existingUser.setUsername("OriginalUsername");
        existingUser.setFirstName("OriginalFirstName");
        existingUser.setLastName("OriginalLastName");
        existingUser.setTimezone("CST");
        existingUser.setStatus("Active");
        existingUser.setCreatedOn(LocalDateTime.of(2026, 1, 1, 0, 0));
        existingUser.setUpdatedOn(LocalDateTime.of(2026, 1, 1, 0, 0));
    }

    @Test
    void getAllUsers_returnsAllUsersFromRepository() {
        User other = new User();
        other.setId(2L);
        when(usersRepository.findAll()).thenReturn(List.of(existingUser, other));

        List<User> users = usersController.getAllUsers();

        assertThat(users).containsExactly(existingUser, other);
    }

    @Test
    void getUserById_whenFound_returnsOkWithUser() {
        when(usersRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        ResponseEntity<User> response = usersController.getUserById(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(existingUser);
    }

    @Test
    void getUserById_whenNotFound_returnsNotFound() {
        when(usersRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<User> response = usersController.getUserById(99L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void createUser_setsTimestampsAndSavesUser() {
        User newUser = new User();
        newUser.setUsername("NewUsername");
        when(usersRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<User> response = usersController.createUser(newUser);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getCreatedOn()).isNotNull();
        assertThat(response.getBody().getUpdatedOn()).isNotNull();

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(usersRepository).save(captor.capture());
        assertThat(captor.getValue().getUsername()).isEqualTo("NewUsername");
    }

    @Test
    void updateUser_whenNotFound_returnsNotFoundAndDoesNotSave() {
        when(usersRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<User> response = usersController.updateUser(99L, new User());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(usersRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_whenFound_overwritesNonEmptyFieldsAndRefreshesUpdatedOn() {
        when(usersRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(usersRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User update = new User();
        update.setCognitoId("654321");
        update.setUsername("UpdatedUsername");
        update.setFirstName("UpdatedFirstName");
        update.setLastName("UpdatedLastName");
        update.setTimezone("PST");
        update.setStatus("Inactive");

        ResponseEntity<User> response = usersController.updateUser(1L, update);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        User saved = response.getBody();
        assertThat(saved.getCognitoId()).isEqualTo("654321");
        assertThat(saved.getUsername()).isEqualTo("UpdatedUsername");
        assertThat(saved.getFirstName()).isEqualTo("UpdatedFirstName");
        assertThat(saved.getLastName()).isEqualTo("UpdatedLastName");
        assertThat(saved.getTimezone()).isEqualTo("PST");
        assertThat(saved.getStatus()).isEqualTo("Inactive");
        assertThat(saved.getUpdatedOn()).isAfter(LocalDateTime.of(2026, 1, 1, 0, 0));
    }

    @Test
    void updateUser_whenFieldsAreNullOrEmpty_keepsExistingValues() {
        when(usersRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(usersRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User update = new User();
        update.setCognitoId("");
        update.setUsername(null);
        update.setFirstName("");
        update.setLastName(null);
        update.setTimezone("");
        update.setStatus(null);

        ResponseEntity<User> response = usersController.updateUser(1L, update);

        User saved = response.getBody();
        assertThat(saved.getCognitoId()).isEqualTo("123456");
        assertThat(saved.getUsername()).isEqualTo("OriginalUsername");
        assertThat(saved.getFirstName()).isEqualTo("OriginalFirstName");
        assertThat(saved.getLastName()).isEqualTo("OriginalLastName");
        assertThat(saved.getTimezone()).isEqualTo("CST");
        assertThat(saved.getStatus()).isEqualTo("Active");
    }

    @Test
    void deleteUser_whenExists_deletesAndReturnsNoContent() {
        when(usersRepository.existsById(1L)).thenReturn(true);

        ResponseEntity<User> response = usersController.deleteUser(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(usersRepository).deleteById(1L);
    }

    @Test
    void deleteUser_whenNotExists_returnsNotFoundAndDoesNotDelete() {
        when(usersRepository.existsById(99L)).thenReturn(false);

        ResponseEntity<User> response = usersController.deleteUser(99L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(usersRepository, never()).deleteById(anyLong());
    }
}
