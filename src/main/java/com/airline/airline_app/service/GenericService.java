package com.airline.airline_app.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

public interface GenericService<T, ID> {
    T save(T entity);
    T update(ID id, T entity);
    void delete(ID id);
    Optional<T> findById(ID id);
    List<T> findAll();
    Page<T> findAll(Pageable pageable);
    List<T> findAll(Specification<T> specification);
    Page<T> findAll(Specification<T> specification, Pageable pageable);
}
