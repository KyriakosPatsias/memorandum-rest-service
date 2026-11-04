package com.kyriakospatsias.repository;

import com.kyriakospatsias.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UsersRepository extends CrudRepository<User, Long> {
}
