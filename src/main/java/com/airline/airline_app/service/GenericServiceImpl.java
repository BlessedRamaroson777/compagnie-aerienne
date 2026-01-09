package com.airline.airline_app.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public abstract class GenericServiceImpl<T, ID> implements GenericService<T, ID> {
    
    protected abstract JpaRepository<T, ID> getRepository();
    protected abstract JpaSpecificationExecutor<T> getSpecificationExecutor();
    
    @Override
    public T save(T entity) {
        return getRepository().save(entity);
    }
    
    @Override
    public T update(ID id, T entity) {
        if (!getRepository().existsById(id)) {
            throw new RuntimeException("Entity not found with id: " + id);
        }
        return getRepository().save(entity);
    }
    
    @Override
    public void delete(ID id) {
        if (!getRepository().existsById(id)) {
            throw new RuntimeException("Entity not found with id: " + id);
        }
        getRepository().deleteById(id);
    }
    
    @Override
    public Optional<T> findById(ID id) {
        return getRepository().findById(id);
    }
    
    @Override
    public List<T> findAll() {
        return getRepository().findAll();
    }
    
    @Override
    public Page<T> findAll(Pageable pageable) {
        return getRepository().findAll(pageable);
    }
    
    @Override
    public List<T> findAll(Specification<T> specification) {
        return getSpecificationExecutor().findAll(specification);
    }
    
    @Override
    public Page<T> findAll(Specification<T> specification, Pageable pageable) {
        return getSpecificationExecutor().findAll(specification, pageable);
    }
}
