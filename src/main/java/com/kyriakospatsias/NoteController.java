package com.kyriakospatsias;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class NoteController {

    Logger logger = LoggerFactory.getLogger(NoteController.class);
    NoteRepository noteRepository;

    @GetMapping(value = "/note")
    public List<Note> getAllNotes() {
        logger.debug("Listing all notes");
        List<Note> notes = new ArrayList<>();
        noteRepository.findAll().forEach(notes::add);
        return notes;
    }

    @GetMapping(value = "/note/{id}")
    public Optional<Note> getNoteById(@PathVariable String id) {
        return noteRepository.findById(id);
    }

    @GetMapping(value = "/note/body/{id}")
    public String getNoteBodyById(@PathVariable String id) {
        return noteRepository.findById(id).get().getBody();
    }

    @PostMapping(value = "/note")
    public Note addNote(@RequestBody Note note) {
        note.setCreatedOn(LocalDateTime.now());
        note.setUpdatedOn(LocalDateTime.now());
        return noteRepository.save(note);
    }

    @PutMapping(value = "/note")
    public Note updateNote(@RequestBody Note note) {
        note.setUpdatedOn(LocalDateTime.now());
        return noteRepository.save(note);
    }

    @DeleteMapping(value = "/note/{id}")
    public void deleteNote(@PathVariable String id) {
        noteRepository.deleteById(id);
    }
}
