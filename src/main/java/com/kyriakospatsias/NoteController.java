package com.kyriakospatsias;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class NoteController {
    @Autowired
    NoteRepository noteRepository;

    @GetMapping(value = "/note")
    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();
        noteRepository.findAll().forEach(notes::add);
        return notes;
    }

    @GetMapping(value = "/note/{id}")
    public Optional<Note> getNoteById(@PathVariable String id) {
        return noteRepository.findById(id);
    }

    @PostMapping(value = "/note")
    public Note addNote(@RequestBody Note note) {
        return noteRepository.save(note);
    }

    @PutMapping(value = "/note")
    public Note updateNote(@RequestBody Note note) {
        return noteRepository.save(note);
    }

    @DeleteMapping(value = "/note/{id}")
    public void deleteNote(@PathVariable String id) {
        noteRepository.deleteById(id);
    }
}
