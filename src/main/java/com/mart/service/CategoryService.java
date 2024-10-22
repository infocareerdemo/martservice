package com.mart.service;

import com.mart.dto.CategoryRequestDto;
import com.mart.dto.CategoryResponseDto;
import com.mart.dto.ProductResponseDto;
import com.mart.entity.Category;
import com.mart.entity.Product;
import com.mart.exception.ApplicationException;
import com.mart.repository.CategoryRepository;
import com.mart.repository.ProductRepository;

import jakarta.transaction.Transactional;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
	ProductRepository productRepository;

    public boolean validateImageFile(MultipartFile multipartFile) {
		BufferedImage image;
		try {
			image = ImageIO.read(multipartFile.getInputStream());
			if (image == null) {
				return false;
			} else {
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
    


	public CategoryResponseDto convertToDTO(Category category) {
		 
		CategoryResponseDto categoryResponseDto = new CategoryResponseDto();
		categoryResponseDto.setCategoryId(category.getCategoryId());
		categoryResponseDto.setCategoryName(category.getCategoryName());
		categoryResponseDto.setCategoryImage(category.getCategoryImage());
		Set<CategoryResponseDto.ProductResponseDto> productResponseDTOs  = new HashSet<>();
		
		for(Product pro : category.getProducts()) {
			
			CategoryResponseDto.ProductResponseDto productDto = new CategoryResponseDto.ProductResponseDto();
			productDto.setProductId(pro.getProductId());
			productDto.setProductName(pro.getProductName());
			productDto.setProductDescription(pro.getProductDescription());
			productDto.setProductPrice(pro.getProductPrice());
			productDto.setProductUpdatedBy(pro.getProductUpdatedBy());
			productDto.setProductImage(pro.getProductImage());
			productDto.setProductActive(pro.isProductActive());
			productResponseDTOs.add(productDto);
			
		}
		
		categoryResponseDto.setProducts(productResponseDTOs);
		
		return categoryResponseDto;
	}
	
	
	   @Transactional
	    public List<CategoryResponseDto> getAllCategoriesWithProducts() {
	        List<Category> categories = categoryRepository.findAll();
	        
	        return categories.stream()
	                .map(this::convertToDTO)  // Convert each Category entity to CategoryResponseDto
	                .collect(Collectors.toList());
	    }


	public String addCategory(CategoryRequestDto categoryRequestDto, MultipartFile categoryImage) throws Exception{
        Category existingCategory = categoryRepository.findByCategoryName(categoryRequestDto.getCategoryName());
		
        if (existingCategory !=null) {
            throw new ApplicationException(HttpStatus.CONFLICT, 1001, LocalDateTime.now(), 
                    "Category name already exists with the same name");
        }
        
        Category category = new Category();
        category.setCategoryName(categoryRequestDto.getCategoryName());

        if (categoryImage != null && !categoryImage.isEmpty()) {
            category.setCategoryImage(categoryImage.getBytes()); 
        }

        categoryRepository.save(category);

        return "Category Saved With Products and Image";
	}


	public String updateCategory(CategoryRequestDto categoryRequestDto, MultipartFile categoryImage) throws Exception{
		
		 Optional<Category> existingCategory	= categoryRepository.findById(categoryRequestDto.getCategoryId());
	        if (existingCategory.isPresent()) {
	        	
	        	 Category existinCategory = categoryRepository.findByCategoryName(categoryRequestDto.getCategoryName());
	     		
	             if (existinCategory !=null) {
	                 throw new ApplicationException(HttpStatus.CONFLICT, 1001, LocalDateTime.now(), 
	                         "Category name already exists with the same name");
	             }
	             
	        	existingCategory.get().setCategoryName(categoryRequestDto.getCategoryName());

		        if (categoryImage != null && !categoryImage.isEmpty()) {
		        	existingCategory.get().setCategoryImage(categoryImage.getBytes()); 
		        }
		        
	            categoryRepository.save(existingCategory.get());

		        
	        }else {
	        	 throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), 
		                    "data not found");
	        }
	
	        return "Category Updated";
	}

	
	

@Transactional
public List<Object> getAllProductsByCategoryIdOrAll(Long categoryId) throws Exception {
    if (categoryId != null) {
        Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isPresent()) {
        Set<Product> allproductss =	category.get().getProducts();
        List<CategoryResponseDto.ProductResponseDto> productDtos = allproductss.stream()
                .map(product -> {
                    CategoryResponseDto.ProductResponseDto productDto = new CategoryResponseDto.ProductResponseDto();
                    productDto.setProductId(product.getProductId());
                    productDto.setProductName(product.getProductName());
                    productDto.setProductDescription(product.getProductDescription());
                    productDto.setProductPrice(product.getProductPrice());
                    productDto.setProductUpdatedBy(product.getProductUpdatedBy());
                    productDto.setProductImage(product.getProductImage());
                    productDto.setProductActive(product.isProductActive());
                    return productDto;
                })
                .collect(Collectors.toList());

        return new ArrayList<>(productDtos);       
        
        } else {
            throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), 
                "Category not found for the provided ID: " + categoryId);
        }
    } else {
        List<Product> allProducts = productRepository.findByProductActiveTrue();
        if (!allProducts.isEmpty()) {
            List<CategoryResponseDto.ProductResponseDto> productDtos = allProducts.stream()
                    .map(product -> {
                        CategoryResponseDto.ProductResponseDto productDto = new CategoryResponseDto.ProductResponseDto();
                        productDto.setProductId(product.getProductId());
                        productDto.setProductName(product.getProductName());
                        productDto.setProductDescription(product.getProductDescription());
                        productDto.setProductPrice(product.getProductPrice());
                        productDto.setProductUpdatedBy(product.getProductUpdatedBy());
                        productDto.setProductImage(product.getProductImage());
                        productDto.setProductActive(product.isProductActive());
                        return productDto;
                    })
                    .collect(Collectors.toList());

            return new ArrayList<>(productDtos); 
        } else {
            return new ArrayList<>(); 
        }
    }

}
	





public Object saveOrUpdateCategory(CategoryRequestDto categoryRequestDto, MultipartFile categoryImage) throws Exception {
    if (categoryRequestDto.getCategoryId() != null) {
        
        Optional<Category> existingCategory = categoryRepository.findById(categoryRequestDto.getCategoryId());
        
        if (existingCategory.isPresent()) {
            Category category = existingCategory.get();
            
            Category categoryByName = categoryRepository.findByCategoryName(categoryRequestDto.getCategoryName());
            if (categoryByName != null && !categoryByName.getCategoryId().equals(category.getCategoryId())) {
                throw new ApplicationException(HttpStatus.CONFLICT, 1001, LocalDateTime.now(), 
                        "Category name already exists with the same name");
            }
            
            category.setCategoryName(categoryRequestDto.getCategoryName());
            if (categoryImage != null && !categoryImage.isEmpty()) {
                category.setCategoryImage(categoryImage.getBytes());
            }
            
            Set<Product> products = new HashSet<>();
            for (Long productId : categoryRequestDto.getProductIds()) {
                productRepository.findById(productId).ifPresent(products::add);
            }
            category.setProducts(products);
            
            for (Product product : products) {
                product.getCategories().add(category);
            }

            categoryRepository.save(category);
            
            return "Category Updated";
            
        } else {
            throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), 
                    "Category not found for the provided ID");
        }

    } else {
        
        Category existingCategoryByName = categoryRepository.findByCategoryName(categoryRequestDto.getCategoryName());
        if (existingCategoryByName != null) {
            throw new ApplicationException(HttpStatus.CONFLICT, 1001, LocalDateTime.now(), 
                    "Category name already exists with the same name");
        }

        Category newCategory = new Category();
        newCategory.setCategoryName(categoryRequestDto.getCategoryName());

        if (categoryImage != null && !categoryImage.isEmpty()) {
            newCategory.setCategoryImage(categoryImage.getBytes());
        }

        categoryRepository.save(newCategory);
        
        return "Category Saved";
    }
}



public Set<CategoryResponseDto.ProductResponseDto> convertProductsToDTOs(Set<Product> products) {
    Set<CategoryResponseDto.ProductResponseDto> productResponseDTOs = new HashSet<>();
    
    for (Product product : products) {
        CategoryResponseDto.ProductResponseDto productDto = new CategoryResponseDto.ProductResponseDto();
        productDto.setProductId(product.getProductId());
        productDto.setProductName(product.getProductName());
        productDto.setProductDescription(product.getProductDescription());
        productDto.setProductPrice(product.getProductPrice());
        productDto.setProductUpdatedBy(product.getProductUpdatedBy());
        productDto.setProductImage(product.getProductImage());
        productDto.setProductActive(product.isProductActive());
        
        productResponseDTOs.add(productDto);
    }
    
    return productResponseDTOs;
}

public Object getAllCategoryproductsAndAllProducts(Long categoryId) throws ApplicationException {

	Optional<Category> category = categoryRepository.findById(categoryId);
	if (category.isPresent()) {
		Set<Product> allproductss = category.get().getProducts();
		List<CategoryResponseDto.ProductResponseDto> productDtos = allproductss.stream().map(product -> {
			CategoryResponseDto.ProductResponseDto productDto = new CategoryResponseDto.ProductResponseDto();
			productDto.setProductId(product.getProductId());
			productDto.setProductName(product.getProductName());
			productDto.setProductDescription(product.getProductDescription());
			productDto.setProductPrice(product.getProductPrice());
			productDto.setProductUpdatedBy(product.getProductUpdatedBy());
			productDto.setProductImage(product.getProductImage());
			productDto.setProductActive(product.isProductActive());
			return productDto;
		}).collect(Collectors.toList());

		return new ArrayList<>(productDtos);

	} else {
		throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(),
				"Category not found for the provided ID: " + categoryId);
	}
}




@Transactional
public Object getAllCategoryProductsAndAllActiveProducts(Long categoryId) throws ApplicationException {
    
    List<CategoryResponseDto.ProductResponseDto> productDtos = new ArrayList<>();
    Set<Long> categoryProductIds = new HashSet<>();  

    if (categoryId != null) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        
        if (categoryOptional.isPresent()) {
            Category category = categoryOptional.get();
            String categoryName = category.getCategoryName();
            Long categoryID = category.getCategoryId();
            Set<Product> categoryProducts = category.getProducts();
            
            for (Product product : categoryProducts) {
                CategoryResponseDto.ProductResponseDto productDto = new CategoryResponseDto.ProductResponseDto();
                productDto.setProductId(product.getProductId());
                productDto.setProductName(product.getProductName());
                productDto.setProductDescription(product.getProductDescription());
                productDto.setProductPrice(product.getProductPrice());
                productDto.setProductUpdatedBy(product.getProductUpdatedBy());
                productDto.setProductImage(product.getProductImage());
                productDto.setProductActive(product.isProductActive());
                productDto.setProductCategory(true); 
                
                productDto.setCategoryId(categoryID);
                productDto.setCategoryName(categoryName);

                productDtos.add(productDto);
                categoryProductIds.add(product.getProductId()); 
            }
        } else {
            throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(),
                    "Category not found for the provided ID: " + categoryId);
        }
    }

    List<Product> allActiveProducts = productRepository.findByProductActiveTrue();
    
    for (Product product : allActiveProducts) {
        if (!categoryProductIds.contains(product.getProductId())) {
            CategoryResponseDto.ProductResponseDto productDto = new CategoryResponseDto.ProductResponseDto();
            productDto.setProductId(product.getProductId());
            productDto.setProductName(product.getProductName());
            productDto.setProductDescription(product.getProductDescription());
            productDto.setProductPrice(product.getProductPrice());
            productDto.setProductUpdatedBy(product.getProductUpdatedBy());
            productDto.setProductImage(product.getProductImage());
            productDto.setProductActive(product.isProductActive());
            productDto.setProductCategory(false); 
            
            productDto.setCategoryId(null);
            productDto.setCategoryName(null);

            productDtos.add(productDto);
        }
    }

    return new ArrayList<>(productDtos);
}

@Transactional
public String updateCategoryProducts(Long categoryId, List<CategoryResponseDto.ProductResponseDto> productDtos) throws ApplicationException {
    Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
    
    if (categoryOptional.isPresent()) {
        Category category = categoryOptional.get();
        Set<Product> categoryProducts = new HashSet<>(category.getProducts()); 

        for (CategoryResponseDto.ProductResponseDto productDto : productDtos) {
            Optional<Product> productOptional = productRepository.findById(productDto.getProductId());
            
            if (productOptional.isPresent()) {
                Product product = productOptional.get();

                if (productDto.isProductCategory()) {
                    categoryProducts.add(product); 
                    product.getCategories().add(category); 
                    
                } else {
                    categoryProducts.remove(product); 
                    product.getCategories().remove(category); 
                }
            } else {
                throw new ApplicationException(HttpStatus.NOT_FOUND, 1002, LocalDateTime.now(),
                        "Product not found for ID: " + productDto.getProductId());
            }
        }

        category.setProducts(categoryProducts);
        categoryRepository.save(category); 
        return "Category updated successfully.";
    } else {
        throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(),
                "Category not found for ID: " + categoryId);
    }
}




}


/*public Object saveOrUpdateCategoryy(CategoryRequestDto categoryRequestDto, MultipartFile categoryImage) throws Exception{
	if(categoryRequestDto.getCategoryId() !=null) {
		
		 Optional<Category> existingCategory	= categoryRepository.findById(categoryRequestDto.getCategoryId());
		   if (existingCategory.isPresent()) {
			   
				    existingCategory.get().setCategoryName(categoryRequestDto.getCategoryName());

			        if (categoryImage != null && !categoryImage.isEmpty()) {
			        	 existingCategory.get().setCategoryImage(categoryImage.getBytes()); 
			        }
		        	
			        Set<Product> products = new HashSet<>();
			        for (Long productId : categoryRequestDto.getProductIds()) {
			            productRepository.findById(productId).ifPresent(products::add);
			        }
			        existingCategory.get().setProducts(products);

			        for (Product product : products) {
			            product.getCategories().add(existingCategory.get());
			        }

			        categoryRepository.save(existingCategory.get());
				   
			
	        	
	        	
		        return "Category Updated";

	        }else {
	        	 throw new ApplicationException(HttpStatus.CONFLICT, 1001, LocalDateTime.now(), 
		                    "Category name already exists with the same name");
	        }
		
	}else {
		
		
		 Category  categorye =   categoryRepository.findByCategoryName(categoryRequestDto.getCategoryName());
		   if(categorye !=null) {
			   throw new ApplicationException(HttpStatus.CONFLICT, 1001, LocalDateTime.now(), 
	                    "Category name already exists with the same name");
		   }else {
			    Category category = new Category();
		        category.setCategoryName(categoryRequestDto.getCategoryName());

		        if (categoryImage != null && !categoryImage.isEmpty()) {
		            category.setCategoryImage(categoryImage.getBytes()); 
		        }

		        categoryRepository.save(category);
			   
		   }
		   
	        return "Category Saved";
	}
	
}*/