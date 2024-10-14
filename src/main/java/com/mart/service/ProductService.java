package com.mart.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mart.entity.Location;
import com.mart.entity.Product;
import com.mart.exception.ApplicationException;
import com.mart.repository.LocationRepository;
import com.mart.repository.ProductRepository;

@Service
public class ProductService {
	
	

	@Autowired
	ProductRepository productRepository;
	
	@Autowired
	LocationRepository locationRepository;

	public String saveOrUpdateProduct(Product productReq, MultipartFile productImg) throws Exception{		
		Optional<Location> location = locationRepository.findById(productReq.getLocation().getLocationId());
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



}
