package com.kyriakospatsias;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class NoteController {
    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private ModelMapper modelMapper;


    @GetMapping(value = "/note")
    public List<NoteDTO> getAllNotes() {
        List<Note> notes = new ArrayList<>();
        noteRepository.findAll().forEach(notes::add);
        return notes.stream().map(n -> modelMapper.map(n, NoteDTO.class)).collect(Collectors.toList());

    }

    @GetMapping(value = "/note/{id}")
    public Optional<NoteDTO> getNoteById(@PathVariable long id) {
        return Optional.of(modelMapper.map(noteRepository.findById(id), NoteDTO.class));
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping(value = "/note")
    public NoteDTO addNote(@RequestBody NoteDTO noteDto) {
        Note createdNote =  noteRepository.save(modelMapper.map(noteDto, Note.class));
        return modelMapper.map(createdNote, NoteDTO.class);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PutMapping(value = "/note")
    public NoteDTO updateNote(@RequestBody NoteDTO noteDto) {
        Note updatedNote = noteRepository.save(modelMapper.map(noteDto, Note.class));
        return modelMapper.map(updatedNote, NoteDTO.class);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @DeleteMapping(value = "/note/{id}")
    public void deleteNote(@PathVariable long id) {
        noteRepository.deleteById(id);
    }
}
