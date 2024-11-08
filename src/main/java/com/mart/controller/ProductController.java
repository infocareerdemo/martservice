package com.mart.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mart.dto.ProductRequestDto;
import com.mart.dto.ProductResponseDto;
import com.mart.entity.Category;
import com.mart.entity.Product;
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

	
	//Save or update product
	@PostMapping("/saveOrUpdateProduct")
	public ResponseEntity<Object> saveOrUpdateProduct(@ModelAttribute Product productReq , @RequestPart(required = false) MultipartFile productImg) throws Exception{	
		return new ResponseEntity<Object> (productService.saveOrUpdateProduct(productReq,productImg),HttpStatus.OK);
		
	}
	
	//get all products
	@GetMapping("/getAllProduct")
	public ResponseEntity<Object> getAllProduct()throws Exception{		
		return new ResponseEntity<Object> (productService.getAllProduct(),HttpStatus.OK);
		
	}
	
	//get all products by location
	@GetMapping("/getAllProductsByLocation")
	public ResponseEntity<Object> getAllProductsByLocation(@RequestParam Long id) throws Exception{		
		return new ResponseEntity<Object>(productService.getAllProductsByLocation(id), HttpStatus.OK);
		
	}
	
	//get product by id 
	@GetMapping("/id")
	@Transactional
	public ResponseEntity<Object> getProductsById(@RequestParam Long id) {
		return new ResponseEntity<Object>(productService.getProductsById(id), HttpStatus.OK);
	}
	
	
	//Product activate and deactivate
	@PostMapping("/activeOrInactive")
	public ResponseEntity<Object> activeOrInactiveProduct(@ModelAttribute Product products) throws Exception {
		return new ResponseEntity<>(productService.activeOrInactiveProduct(products), HttpStatus.OK);
	}
	
	
	//Get only active products
	@GetMapping("/activeProducts")
	public ResponseEntity<List<Product>> getActiveProducts(@RequestParam Long locationId) throws Exception{
	List<Product> activeProducts = productService.getActiveProducts(locationId);
	        return new ResponseEntity<>(activeProducts, HttpStatus.OK);
	    }
	
	
	//Validate the image
	@PostMapping("/validateImage")
	public ResponseEntity<Object> validateImageFile(@RequestPart MultipartFile image) {
		return new ResponseEntity<>(productService.validateImageFile(image), HttpStatus.OK);
	}


	//Get All Products by CategoryId
	@GetMapping("/getAllproductsByCategoryId")
	    public List<Product> getProductsByCategory(@RequestParam Long categoryId) {
	        return productService.getProductsByCategory(categoryId);
	    }
	 
	 
	//Get All Categories by productId
	 @GetMapping("/getAllCategoriesByProductId")
	    public List<Category> getCategoriesByProductId(@RequestParam Long productId) {
	        return productService.getCategoriesByProductId(productId);
	    }
	 
	
	// Create product with category
     @PostMapping("/createProductWithCategory")
	 public ResponseEntity<Object> createProductWithCategory(@ModelAttribute ProductRequestDto productReq,@RequestPart(required = false) MultipartFile productImg) throws Exception {
		return new ResponseEntity<Object>(productService.createProductWithCategory(productReq,productImg), HttpStatus.OK);
		}		
		
		
     //Get the  products with category response by productId
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
		
     //Get All Products With Categories
     @GetMapping("/getAllProductsWithCategories")
     public ResponseEntity<List<ProductResponseDto>> getAllProductsWithCategories() {
         List<ProductResponseDto> products = productService.getAllProductsWithCategories();
         return new ResponseEntity<>(products, HttpStatus.OK);
     }

			
}
