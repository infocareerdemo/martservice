package com.mart.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.mart.entity.Cart;
import com.mart.entity.Location;
import com.mart.entity.Product;
import com.mart.entity.UserDetail;
import com.mart.exception.ApplicationException;
import com.mart.repository.CartRepository;
import com.mart.repository.ProductRepository;
import com.mart.repository.UserDetailRepository;

import jakarta.transaction.Transactional;

@Service
public class CartService {
	

	
	@Autowired
	CartRepository cartRepository;
	
	@Autowired
	UserDetailRepository userDetailRepository;

	@Autowired
	ProductRepository productRepository;
	


	public List<Cart> getAllProductsByUserId(Long userId) throws ApplicationException {
	 Optional<UserDetail> userDetail =	userDetailRepository.findById(userId);
	   if(userDetail.isPresent()) {
		   List<Cart> carts =   cartRepository.findByUserDetail(userDetail);
	        return carts;

	   }else {
			throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "User Not found");

	   }
	    
	}
	
	
	
	public Cart addProductByCart(Cart cartReq) throws ApplicationException {

		Cart cart = new Cart();
	    if (cartReq != null) {

	        UserDetail userDetail = userDetailRepository.findById(cartReq.getUserDetail().getUserId())
	            .orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "User Not Found"));

	        Product product = productRepository.findById(cartReq.getProduct().getProductId())
	            .orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "Product Not Found"));

	        Optional<Cart> existingCartItem = cartRepository.findByUserDetailUserIdAndProductProductId(
	            cartReq.getUserDetail().getUserId(), cartReq.getProduct().getProductId());

	        if (existingCartItem.isPresent()) {
	            throw new ApplicationException(HttpStatus.CONFLICT, 1002, LocalDateTime.now(), "Product already exists in the cart");
	        }

	        
	        cart.setUnitPrice(cartReq.getUnitPrice());
	        cart.setQuantity(cartReq.getQuantity());
	        cart.setTotalPrice(cartReq.getTotalPrice());
	        cart.setProductActive(cartReq.isProductActive());
	        cart.setUserDetail(userDetail);
	        cart.setProduct(product);
	        cart.setUpdatedDateTime(LocalDateTime.now());

	        return cartRepository.save(cart);

	    } else {
	        throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "Data Not Found");
	    }
	}



}