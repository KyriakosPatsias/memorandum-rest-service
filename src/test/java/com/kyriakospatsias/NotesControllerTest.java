package com.kyriakospatsias;

import com.kyriakospatsias.controller.NotesController;
import com.kyriakospatsias.model.Note;
import com.kyriakospatsias.repository.NotesRepository;
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
class NotesControllerTest {

    @Mock
    private NotesRepository notesRepository;

    @InjectMocks
    private NotesController notesController;

    private Note existingNote;

    @BeforeEach
    void setUp() {
        existingNote = new Note();
        existingNote.setId(1L);
        existingNote.setTitle("Original title");
        existingNote.setContent("Original content");
        existingNote.setCategory(null);
        existingNote.setFavorite(false);
        existingNote.setCreatedOn(LocalDateTime.of(2026, 1, 1, 0, 0));
        existingNote.setUpdatedOn(LocalDateTime.of(2026, 1, 1, 0, 0));
    }

    @Test
    void getAllNotes_returnsAllNotesFromRepository() {
        Note other = new Note();
        other.setId(2L);
        when(notesRepository.findAll()).thenReturn(List.of(existingNote, other));

        List<Note> notes = notesController.getAllNotes();

        assertThat(notes).containsExactly(existingNote, other);
    }

    @Test
    void getNoteById_whenFound_returnsOkWithNote() {
        when(notesRepository.findById(1L)).thenReturn(Optional.of(existingNote));

        ResponseEntity<Note> response = notesController.getNoteById(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(existingNote);
    }

    @Test
    void getNoteById_whenNotFound_returnsNotFound() {
        when(notesRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<Note> response = notesController.getNoteById(99L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void createNote_setsTimestampsAndSavesNote() {
        Note newNote = new Note();
        newNote.setTitle("New note");
        when(notesRepository.save(any(Note.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<Note> response = notesController.createNote(newNote);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getCreatedOn()).isNotNull();
        assertThat(response.getBody().getUpdatedOn()).isNotNull();

        ArgumentCaptor<Note> captor = ArgumentCaptor.forClass(Note.class);
        verify(notesRepository).save(captor.capture());
        assertThat(captor.getValue().getTitle()).isEqualTo("New note");
    }

    @Test
    void updateNote_whenNotFound_returnsNotFoundAndDoesNotSave() {
        when(notesRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<Note> response = notesController.updateNote(99L, new Note());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(notesRepository, never()).save(any(Note.class));
    }

    @Test
    void updateNote_whenFound_overwritesNonEmptyFieldsAndFavoriteAndRefreshesUpdatedOn() {
        when(notesRepository.findById(1L)).thenReturn(Optional.of(existingNote));
        when(notesRepository.save(any(Note.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Note update = new Note();
        update.setTitle("Updated title");
        update.setContent("Updated content");
        update.setCategory(null);
        update.setFavorite(true);

        ResponseEntity<Note> response = notesController.updateNote(1L, update);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Note saved = response.getBody();
        assertThat(saved.getTitle()).isEqualTo("Updated title");
        assertThat(saved.getContent()).isEqualTo("Updated content");
        assertThat(saved.getCategory()).isNull();
        assertThat(saved.isFavorite()).isTrue();
        assertThat(saved.getUpdatedOn()).isAfter(LocalDateTime.of(2026, 1, 1, 0, 0));
    }

    @Test
    void updateNote_whenFieldsAreNullOrEmpty_keepsExistingValues() {
        when(notesRepository.findById(1L)).thenReturn(Optional.of(existingNote));
        when(notesRepository.save(any(Note.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Note update = new Note();
        update.setTitle("");
        update.setContent(null);
        update.setCategory(null);
        update.setFavorite(false);

        ResponseEntity<Note> response = notesController.updateNote(1L, update);

        Note saved = response.getBody();
        assertThat(saved.getTitle()).isEqualTo("Original title");
        assertThat(saved.getContent()).isEqualTo("Original content");
        assertThat(saved.getCategory()).isNull();
    }

    @Test
    void deleteNote_whenExists_deletesAndReturnsNoContent() {
        when(notesRepository.existsById(1L)).thenReturn(true);

        ResponseEntity<Void> response = notesController.deleteNote(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(notesRepository).deleteById(1L);
    }

    @Test
    void deleteNote_whenNotExists_returnsNotFoundAndDoesNotDelete() {
        when(notesRepository.existsById(99L)).thenReturn(false);

        ResponseEntity<Void> response = notesController.deleteNote(99L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(notesRepository, never()).deleteById(anyLong());
    }
}
