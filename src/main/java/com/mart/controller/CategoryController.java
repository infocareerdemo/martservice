package com.mart.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mart.dto.CategoryRequestDto;
import com.mart.dto.CategoryResponseDto;
import com.mart.dto.ProductRequestDto;
import com.mart.entity.Category;
import com.mart.entity.Product;
import com.mart.repository.CategoryRepository;
import com.mart.service.CategoryService;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
	
	   @Autowired
	    private CategoryService categoryService;
	   
	   @Autowired
	   CategoryRepository categoryRepository;
	   
	   
		@PostMapping("/addCategoryWithProducts")
		public ResponseEntity<Object> addCategoryWithProducts(@RequestBody CategoryRequestDto categoryRequestDto) throws Exception {
			return new ResponseEntity<Object>(categoryService.addCategoryWithProducts(categoryRequestDto), HttpStatus.OK);
		}
		
		@GetMapping("/{id}")
		public ResponseEntity<CategoryResponseDto> getCategoryById(@PathVariable Long id) {
		    Optional<Category> categry = categoryRepository.findById(id);
		    if (categry.isPresent()) {
		    	CategoryResponseDto categoryResponseDto = categoryService.convertToDTO(categry.get());
		        return ResponseEntity.ok(categoryResponseDto);
		    } else {
		        return ResponseEntity.notFound().build();
		    }
		}
		


}
