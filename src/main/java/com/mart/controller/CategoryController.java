package com.mart.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
import com.mart.dto.ProductResponseDto;
import com.mart.entity.Category;
import com.mart.entity.Product;
import com.mart.exception.ApplicationException;
import com.mart.repository.CategoryRepository;
import com.mart.service.CategoryService;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
	
	   @Autowired
	    private CategoryService categoryService;
	   
	   @Autowired
	   CategoryRepository categoryRepository;
	   
	   
		@PostMapping("/validateImage")
		public ResponseEntity<Object> validateImageFile(@RequestPart MultipartFile image) {
			return new ResponseEntity<>(categoryService.validateImageFile(image), HttpStatus.OK);
		}
				
	

		//update category
		@PostMapping("/updateCategory")
		public ResponseEntity<Object> updateCategory(
		        @ModelAttribute CategoryRequestDto categoryRequestDto,@RequestPart(required = false) MultipartFile categoryImage) throws Exception {		    
		    return new ResponseEntity<>(categoryService.updateCategory(categoryRequestDto,categoryImage), HttpStatus.OK);
		}
		
		@PostMapping("/saveOrUpdateCategory")
		public ResponseEntity<Object> saveOrUpdateCategory(@ModelAttribute CategoryRequestDto categoryRequestDto,
		        @RequestPart(required = false) MultipartFile categoryImage) throws Exception {
		    
		    return new ResponseEntity<>(categoryService.saveOrUpdateCategory(categoryRequestDto, categoryImage), HttpStatus.OK);
		}
		
				
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
		
		@GetMapping
		public ResponseEntity<Set<CategoryResponseDto.ProductResponseDto>> getProductsByCategoryId(@RequestParam Long id) {
		    Optional<Category> categoryOptional = categoryRepository.findById(id);
		    
		    if (categoryOptional.isPresent()) {
		        Set<CategoryResponseDto.ProductResponseDto> productResponseDtos = categoryService.convertProductsToDTOs(categoryOptional.get().getProducts());
		        return ResponseEntity.ok(productResponseDtos);
		    } else {
		        return ResponseEntity.notFound().build();
		    }
		}

		
		
		
        //get all products by cid or all
		@GetMapping("/getAllProductsByCategoryIdOrAll")
	    public ResponseEntity<Object> getAllProductsByCategoryIdOrAll(@RequestParam(required = false) Long categoryId) throws Exception {
	        return new ResponseEntity<>(categoryService.getAllProductsByCategoryIdOrAll(categoryId),HttpStatus.OK);
	    }
		
		
		@GetMapping("/getAllCategoriesWithProducts")
		public ResponseEntity<List<CategoryResponseDto>> getAllCategoriesWithProducts() {
		      List<CategoryResponseDto> categories = categoryService.getAllCategoriesWithProducts();
		        return new ResponseEntity<>(categories, HttpStatus.OK);
		 }

		@GetMapping("/getAllCategoryProductsAndAllProducts")
	    public ResponseEntity<Object> getAllCategoryproductsAndAllProducts(@RequestParam(required = false) Long categoryId) throws Exception {
	        return new ResponseEntity<>(categoryService.getAllCategoryProductsAndAllActiveProducts(categoryId),HttpStatus.OK);
	    }

		@PostMapping("/updateCategoryProducts")
		public ResponseEntity<Object> updateCategoryProducts(@RequestParam(required = false) Long categoryId, 
		                                                     @RequestBody List<CategoryResponseDto.ProductResponseDto> productDtos) throws Exception {
		    try {
		        Object response = categoryService.updateCategoryProducts(categoryId, productDtos); // Returns a response from the service
		        return new ResponseEntity<>(response, HttpStatus.OK);
		    } catch (ApplicationException e) {
		        return new ResponseEntity<>(e.getMessage(), e.getHttpStatus());
		    }
		}



		

		
		


}

//add category with 
/*@PostMapping("/addCategoryWithProducts")
public ResponseEntity<Object> addCategoryWithProducts(
        @ModelAttribute CategoryRequestDto categoryRequestDto,
        @RequestPart(required = false) MultipartFile categoryImage) throws Exception {
    
    return new ResponseEntity<>(categoryService.addCategoryWithProducts(categoryRequestDto, categoryImage), HttpStatus.OK);
}*/
			

//add category
/*@PostMapping("/addCategory")
public ResponseEntity<Object> addCategory(
        @ModelAttribute CategoryRequestDto categoryRequestDto,
        @RequestPart(required = false) MultipartFile categoryImage) throws Exception {
    
    return new ResponseEntity<>(categoryService.addCategory(categoryRequestDto, categoryImage), HttpStatus.OK);
}*/



/*@GetMapping("/{id}")
public ResponseEntity<CategoryResponseDto> getCategoryById(@PathVariable Long id) {
    Optional<Category> categry = categoryRepository.findById(id);
    if (categry.isPresent()) {
    	CategoryResponseDto categoryResponseDto = categoryService.convertToDTO(categry.get());
        return ResponseEntity.ok(categoryResponseDto);
    } else {
        return ResponseEntity.notFound().build();
    }
}*/

