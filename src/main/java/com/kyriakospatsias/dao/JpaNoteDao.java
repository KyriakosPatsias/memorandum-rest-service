package com.kyriakospatsias.dao;

import com.kyriakospatsias.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Optional;

public class JpaNoteDao implements Dao<Note>{

    @Autowired
    JdbcTemplate jdbcTemplate;


    @Override
    public Optional<Note> get(long id) {
        return Optional.empty();
    }

    @Override
    public List<Note> getAll() {
        return null;
    }

    @Override
    public void save(Note note) {

    }

    @Override
    public void update(Note note, String[] params) {

    }

    @Override
    public void delete(Note note) {

    }
}
