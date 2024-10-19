package com.mart.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

import com.mart.dto.CategoryResponseDto;
import com.mart.dto.ProductRequestDto;
import com.mart.dto.ProductResponseDto;
import com.mart.entity.Category;
import com.mart.entity.Product;
import com.mart.exception.ApplicationException;
import com.mart.repository.ProductRepository;
import com.mart.service.ProductService;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api/v1/product")
public class ProductController {

	@Autowired
	ProductService productService;
	
	@Autowired
	ProductRepository productRepository;

	@PostMapping("/saveOrUpdateProduct")
	public ResponseEntity<Object> saveOrUpdateProduct(@ModelAttribute Product productReq , @RequestPart(required = false) MultipartFile productImg) throws Exception{	
		return new ResponseEntity<Object> (productService.saveOrUpdateProduct(productReq,productImg),HttpStatus.OK);
		
	}
	
	@GetMapping("/getAllProduct")
	public ResponseEntity<Object> getAllProduct()throws Exception{		
		return new ResponseEntity<Object> (productService.getAllProduct(),HttpStatus.OK);
		
	}
	
	@GetMapping("/getAllProductsByLocation")
	public ResponseEntity<Object> getAllProductsByLocation(@RequestParam Long id) throws Exception{		
		return new ResponseEntity<Object>(productService.getAllProductsByLocation(id), HttpStatus.OK);
		
	}
	
	@GetMapping("/id")
	@Transactional
	public ResponseEntity<Object> getFoodItemsById(@RequestParam Long id) {
		return new ResponseEntity<Object>(productService.getFoodItemsById(id), HttpStatus.OK);
	}
	
	
	@PostMapping("/activeOrInactive")
	public ResponseEntity<Object> activeOrInactiveProduct(@ModelAttribute Product products) throws Exception {
		return new ResponseEntity<>(productService.activeOrInactiveProduct(products), HttpStatus.OK);
	}
	
	@GetMapping("/activeProducts")
	public ResponseEntity<List<Product>> getActiveProducts(@RequestParam Long locationId) throws Exception{
	List<Product> activeProducts = productService.getActiveProducts(locationId);
	        return new ResponseEntity<>(activeProducts, HttpStatus.OK);
	    }
	
	
	@PostMapping("/validateImage")
	public ResponseEntity<Object> validateImageFile(@RequestPart MultipartFile image) {
		return new ResponseEntity<>(productService.validateImageFile(image), HttpStatus.OK);
	}


	 @GetMapping("/getAllproductsByCategoryId")
	    public List<Product> getProductsByCategory(@RequestParam Long categoryId) {
	        return productService.getProductsByCategory(categoryId);
	    }
	 
	 
	 @GetMapping("/getAllCategoriesByProductId")
	    public List<Category> getCategoriesByProductId(@RequestParam Long productId) {
	        return productService.getCategoriesByProductId(productId);
	    }
	 
	
		
     @PostMapping("/createProductWithCategory")
	 public ResponseEntity<Object> createProductWithCategory(@ModelAttribute ProductRequestDto productReq,@RequestPart(required = false) MultipartFile productImg) throws Exception {
		return new ResponseEntity<Object>(productService.createProductWithCategory(productReq,productImg), HttpStatus.OK);
		}		
		
		
     @GetMapping("/{id}")
	 @Transactional
	 public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long id) {			
			Optional<Product> product = productRepository.findById(id);
				if(product.isPresent()) {
		            System.out.println("Categories: " + product.get().getCategories());

					ProductResponseDto productResponseDto = productService.convertToDTO(product.get());
					return ResponseEntity.ok(productResponseDto);
				}else {
			        return ResponseEntity.notFound().build();
	
				}
				
			}
		

			
}
