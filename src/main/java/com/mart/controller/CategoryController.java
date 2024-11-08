package com.mart.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mart.dto.CategoryRequestDto;
import com.mart.dto.CategoryResponseDto;
import com.mart.entity.Category;
import com.mart.repository.CategoryRepository;
import com.mart.service.CategoryService;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
	
	    @Autowired
	    private CategoryService categoryService;
	   
	    @Autowired
	    CategoryRepository categoryRepository;
	   
	   
	    // Validate the image file
		@PostMapping("/validateImage")
		public ResponseEntity<Object> validateImageFile(@RequestPart MultipartFile image) {
			return new ResponseEntity<>(categoryService.validateImageFile(image), HttpStatus.OK);
		}
				
	

		//Update Only category
		@PostMapping("/updateCategory")
		public ResponseEntity<Object> updateCategory(
		        @ModelAttribute CategoryRequestDto categoryRequestDto,@RequestPart(required = false) MultipartFile categoryImage) throws Exception {		    
		    return new ResponseEntity<>(categoryService.updateCategory(categoryRequestDto,categoryImage), HttpStatus.OK);
		}
		
		
		//save or Update Only category
		@PostMapping("/saveOrUpdateCategory")
		public ResponseEntity<Object> saveOrUpdateCategory(@ModelAttribute CategoryRequestDto categoryRequestDto,
		        @RequestPart(required = false) MultipartFile categoryImage) throws Exception {
		    
		    return new ResponseEntity<>(categoryService.saveOrUpdateCategory(categoryRequestDto, categoryImage), HttpStatus.OK);
		}				
		
		
        //get all products by category id	
		@GetMapping("/getAllProductsByCategoryId")
		public ResponseEntity<Object> getAllProductsByCategoryId(@RequestParam Long categoryId) throws Exception {
		    Optional<Category> categry = categoryRepository.findById(categoryId);
             if(categry.isPresent()) {
 		    	CategoryResponseDto categoryResponseDto = categoryService.convertToDTO(categry.get());
		        return ResponseEntity.ok(categoryResponseDto);

             }else {
 		        return ResponseEntity.notFound().build();

             }
			
		}
	
		
        //get all products by cid or all
		@GetMapping("/getAllProductsByCategoryIdOrAll")
	    public ResponseEntity<Object> getAllProductsByCategoryIdOrAll(@RequestParam(required = false) Long categoryId) throws Exception {
	        return new ResponseEntity<>(categoryService.getAllProductsByCategoryIdOrAll(categoryId),HttpStatus.OK);
	    }
				
		
		//get all  catgoeies with products
		@GetMapping("/getAllCategoriesWithProducts")
		public ResponseEntity<List<CategoryResponseDto>> getAllCategoriesWithProducts() {
		      List<CategoryResponseDto> categories = categoryService.getAllCategoriesWithProducts();
		        return new ResponseEntity<>(categories, HttpStatus.OK);
		 }

		// get all category products and all all products
		@GetMapping("/getAllCategoryProductsAndAllProducts")
	    public ResponseEntity<Object> getAllCategoryproductsAndAllProducts(@RequestParam(required = false) Long categoryId) throws Exception {
	        return new ResponseEntity<>(categoryService.getAllCategoryProductsAndAllActiveProducts(categoryId),HttpStatus.OK);
	    }		


}

