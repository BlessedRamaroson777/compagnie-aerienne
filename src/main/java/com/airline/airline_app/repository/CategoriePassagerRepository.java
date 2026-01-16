package com.airline.airline_app.repository;

import com.airline.airline_app.entity.CategoriePassager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriePassagerRepository extends JpaRepository<CategoriePassager, Long> {
}
