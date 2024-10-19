package com.mart.service;

import com.mart.dto.CategoryRequestDto;
import com.mart.dto.CategoryResponseDto;
import com.mart.entity.Category;
import com.mart.entity.Product;
import com.mart.exception.ApplicationException;
import com.mart.repository.CategoryRepository;
import com.mart.repository.ProductRepository;

import jakarta.transaction.Transactional;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
	ProductRepository productRepository;


    @Transactional
    public String addCategoryWithProducts(CategoryRequestDto categoryRequestDto) throws ApplicationException {
        Optional<Category> existingCategory = categoryRepository.findByCategoryName(categoryRequestDto.getCategoryName());
        
        if (existingCategory.isPresent()) {
        	throw new ApplicationException(HttpStatus.CONFLICT, 1001, LocalDateTime.now(),
					"Category name already exists with same name");
        	}
        
        Category category = new Category();
        category.setCategoryName(categoryRequestDto.getCategoryName());

        Set<Product> products = new HashSet<>();
        
        for (Long productId : categoryRequestDto.getProductIds()) {
            productRepository.findById(productId).ifPresent(products::add);
        }
        category.setProducts(products);
        
        for (Product product : products) {
            product.getCategories().add(category);
        }
        
        categoryRepository.save(category);

        return "Category Saved With Products";
    }



	public CategoryResponseDto convertToDTO(Category category) {
		 
		CategoryResponseDto categoryResponseDto = new CategoryResponseDto();
		categoryResponseDto.setCategoryId(category.getCategoryId());
		categoryResponseDto.setCategoryName(category.getCategoryName());
		
		Set<CategoryResponseDto.ProductResponseDto> productResponseDTOs  = new HashSet<>();
		
		for(Product pro : category.getProducts()) {
			
			CategoryResponseDto.ProductResponseDto productDto = new CategoryResponseDto.ProductResponseDto();
			productDto.setProductId(pro.getProductId());
			productDto.setProductName(pro.getProductName());
			productDto.setProductDescription(pro.getProductDescription());
			productDto.setProductPrice(pro.getProductPrice());
			productDto.setProductUpdatedBy(pro.getProductUpdatedBy());
			productDto.setProductImage(pro.getProductImage());
			productResponseDTOs.add(productDto);
			
		}
		
		categoryResponseDto.setProducts(productResponseDTOs);
		
		return categoryResponseDto;
	}
	
	
	
}
