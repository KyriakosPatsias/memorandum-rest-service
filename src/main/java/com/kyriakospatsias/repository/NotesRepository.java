package com.kyriakospatsias;

import com.kyriakospatsias.model.Note;
import org.springframework.data.repository.CrudRepository;

public interface NotesRepository extends CrudRepository<Note, Long> {

}
