package com.spring.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.api.entities.Category;

public interface CategoryRepository extends JpaRepository<Category, Long>{

	Category findById(int id);
}
