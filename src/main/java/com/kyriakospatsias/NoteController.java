package com.kyriakospatsias;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class NoteController {

    Logger logger = LoggerFactory.getLogger(NoteController.class);

    @Autowired
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
