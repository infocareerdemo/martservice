package com.mart.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mart.entity.Category;
import com.mart.entity.Product;
import com.mart.repository.CategoryRepository;
import com.mart.service.CategoryService;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
	
	   @Autowired
	    private CategoryService categoryService;
	   
	   

	   @GetMapping("/getAllCategories")
	   public Optional<Category> getCategory(@RequestParam Long id) {
	        return categoryService.getCategoryById(id);
	    }

}
