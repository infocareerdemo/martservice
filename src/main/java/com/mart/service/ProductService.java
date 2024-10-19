package com.mart.service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.imageio.ImageIO;

import org.hibernate.Hibernate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import com.mart.dto.ProductRequestDto;
import com.mart.dto.ProductResponseDto;
import com.mart.entity.Category;
import com.mart.entity.Location;
import com.mart.entity.Product;
import com.mart.exception.ApplicationException;
import com.mart.repository.CategoryRepository;
import com.mart.repository.LocationRepository;
import com.mart.repository.ProductRepository;

@Service
public class ProductService {
	
	

	@Autowired
	ProductRepository productRepository;
	
	@Autowired
	LocationRepository locationRepository;
	
	@Autowired
     CategoryRepository categoryRepository;


	
	public String saveOrUpdateProduct(Product productReq, MultipartFile productImg) throws Exception{	
	Long locId =	productReq.getLocation().getLocationId();
		Optional<Location> location = locationRepository.findById(locId);
		if(!location.isPresent()) {
			throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "Location not found");
		}
		
		if(productReq.getProductId() == null) {
		 Product productExists =	productRepository.findByProductNameAndLocationLocationId(productReq.getProductName(),productReq.getLocation().getLocationId());
		 if (productExists != null) {
				throw new ApplicationException(HttpStatus.CONFLICT, 1001, LocalDateTime.now(),
						"Product already exists with same name");
			}
			
			Product product = new Product();
			product.setProductName(productReq.getProductName());
			product.setProductDescription(productReq.getProductDescription());
			product.setProductPrice(Double.parseDouble(String.format("%.2f", productReq.getProductPrice())));
            product.setProductActive(true);
			product.setLocation(location.get());
            if(productImg != null && !productImg.isEmpty()) {
            	product.setProductImage(productImg.getBytes());
            }
            product.setProductUpdatedBy(productReq.getProductUpdatedBy());
			product.setUpdatedDate(LocalDateTime.now());

            productRepository.save(product);
            return "Product saved";

		}else {
			 Optional<Product> productExists =	productRepository.findById(productReq.getProductId());
			 if(productExists != null) {

				Product product = productExists.get();
				byte[] img = productExists.get().getProductImage();
				product.setProductName(productReq.getProductName());
				product.setProductDescription(productReq.getProductDescription());
				product.setProductPrice(Double.parseDouble(String.format("%.2f", productReq.getProductPrice())));
				product.setProductActive(productReq.isProductActive());
				product.setLocation(location.get());
				product.setUpdatedDate(LocalDateTime.now());

	            if (productImg != null && !productImg.isEmpty()) {
					product.setProductImage(productImg.getBytes());
				} else {
					product.setProductImage(img);
				}

	            productRepository.save(product);

				return "Product updated";
			 }else {
					throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "No food item found");
			 }
			
		}  
	  
	}

	public List<Product> getAllProduct() throws ApplicationException{
	   List<Product> allProducts =	productRepository.findAll();
		return allProducts;
	}
	
	public List<Product> getAllProductsByLocation(Long id) throws ApplicationException {
		Optional<Location> location = locationRepository.findById(id);
		if (location.isPresent()) {
			List<Product> products = productRepository.findByLocationLocationId(id);
			return products;
		} else {
			throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "No location found");
		}
	}

	

	public Product getFoodItemsById(Long id) {
		Optional<Product> product = productRepository.findById(id);
		if (product.isPresent()) {
			return product.get();
		}
		return null;
	}
	
	
	public String activeOrInactiveProduct(Product products) throws ApplicationException {
		Optional<Product> product = productRepository.findById(products.getProductId());
		if (product.isPresent()) {
			if (products.isProductActive() == true) {
				product.get().setProductActive(true);
				product.get().setUpdatedDate(LocalDateTime.now());
				product.get().setProductUpdatedBy(products.getProductUpdatedBy());
				productRepository.save(product.get());

				return "Product is active";
			} else {
				product.get().setProductActive(false);
				product.get().setUpdatedDate(LocalDateTime.now());
				product.get().setProductUpdatedBy(products.getProductUpdatedBy());
				productRepository.save(product.get());

				return "Product is inactive";
			}
		} else {
			throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "Product not found");
		}
	}

	public List<Product> getActiveProducts(Long locationId) {
		return productRepository.findByLocationLocationIdAndProductActive(locationId, true);
	}

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
    
    
    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoriesCategoryId(categoryId);
    }

    public List<Category> getCategoriesByProductId(Long productId) {
        return categoryRepository.findByProductsProductId(productId);
    }

    
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

	 
		public String createProductWithCategory(ProductRequestDto productReq,MultipartFile productImg) throws Exception{
			Long locId =	productReq.getLocation().getLocationId();
			
			Optional<Location> location = locationRepository.findById(locId);
			if(!location.isPresent()) {
				throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "Location not found");
			}
				

			if(productReq.getProductId() == null) {
			 Product productExists =	productRepository.findByProductNameAndLocationLocationId(productReq.getProductName(),productReq.getLocation().getLocationId());
			 if (productExists != null) {
					throw new ApplicationException(HttpStatus.CONFLICT, 1001, LocalDateTime.now(),
							"Product already exists with same name");
				}
				
				Product product = new Product();
				product.setProductName(productReq.getProductName());
				product.setProductDescription(productReq.getProductDescription());
				product.setProductPrice(Double.parseDouble(String.format("%.2f", productReq.getProductPrice())));
	            product.setProductActive(true);
				product.setLocation(location.get());
	            if(productImg != null && !productImg.isEmpty()) {
	            	product.setProductImage(productImg.getBytes());
	            }
	            product.setProductUpdatedBy(productReq.getProductUpdatedBy());
				product.setUpdatedDate(LocalDateTime.now());
	            
	            
	            Set<Category> categories = new HashSet<>();
	            for( Long categoryId : productReq.getCategoryIds()) {
	            	categoryRepository.findById(categoryId).ifPresent(categories::add);
	            }
	            product.setCategories(categories);
	            
	            productRepository.save(product);
	            
	            
	            return "Product saved";

			}else {
				 Optional<Product> productExists =	productRepository.findById(productReq.getProductId());
				 if(productExists != null) {

					Product product = productExists.get();
					byte[] img = productExists.get().getProductImage();
					product.setProductName(productReq.getProductName());
					product.setProductDescription(productReq.getProductDescription());
					product.setProductPrice(Double.parseDouble(String.format("%.2f", productReq.getProductPrice())));
					product.setProductActive(productReq.isProductActive());
					product.setLocation(location.get());
					product.setUpdatedDate(LocalDateTime.now());

		            if (productImg != null && !productImg.isEmpty()) {
						product.setProductImage(productImg.getBytes());
					} else {
						product.setProductImage(img);
					}
		            
		            
		            Set<Category> categories = new HashSet<>();
		            for( Long categoryId : productReq.getCategoryIds()) {
		            	categoryRepository.findById(categoryId).ifPresent(categories::add);
		            }
		            product.setCategories(categories);
		            
		            productRepository.save(product);


					return "Product updated";
				 }else {
						throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "No food item found");
				 }				
			} 
									
		}
		
		
		public ProductResponseDto convertToDTO(Product product) {
		    ProductResponseDto productResponseDto = new ProductResponseDto();
		    productResponseDto.setProductId(product.getProductId());
		    productResponseDto.setProductName(product.getProductName());
		    productResponseDto.setProductDescription(product.getProductDescription());
		    productResponseDto.setProductPrice(Double.parseDouble(String.format("%.2f", product.getProductPrice())));
		    productResponseDto.setProductActive(product.isProductActive());
		    productResponseDto.setLocation(product.getLocation());
		    productResponseDto.setProductImage(product.getProductImage());
		    productResponseDto.setProductUpdatedBy(product.getProductUpdatedBy());

		    Set<ProductResponseDto.CategoryResponseDto> categoryResponseDTOs = new HashSet<>();
		    for (Category category : product.getCategories()) {
		        ProductResponseDto.CategoryResponseDto categoryDTO = new ProductResponseDto.CategoryResponseDto();
		        categoryDTO.setCategoryId(category.getCategoryId());
		        categoryDTO.setCategoryName(category.getCategoryName());
		        categoryResponseDTOs.add(categoryDTO);
		    }
		    productResponseDto.setCategories(categoryResponseDTOs);

		    return productResponseDto;
		}

    
}
