package com.kyriakospatsias;

import com.kyriakospatsias.model.Note;
import org.springframework.data.repository.CrudRepository;

public interface NoteRepository extends CrudRepository<Note, Long> {

}
