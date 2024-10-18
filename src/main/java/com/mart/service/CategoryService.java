package com.mart.service;

import com.mart.entity.Category;
import com.mart.entity.Product;
import com.mart.repository.CategoryRepository;
import com.mart.repository.ProductRepository;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

	 public Optional<Category> getCategoryById(Long id) {
	        return categoryRepository.findById(id);
	    }

	   
	
}
