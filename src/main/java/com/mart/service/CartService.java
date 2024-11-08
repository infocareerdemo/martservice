package com.mart.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.mart.entity.Cart;
import com.mart.entity.Product;
import com.mart.entity.UserDetail;
import com.mart.exception.ApplicationException;
import com.mart.repository.CartRepository;
import com.mart.repository.ProductRepository;
import com.mart.repository.UserDetailRepository;


@Service
public class CartService {
	

	
	@Autowired
	CartRepository cartRepository;
	
	@Autowired
	UserDetailRepository userDetailRepository;

	@Autowired
	ProductRepository productRepository;
	

	
	public List<Cart> getAllProductsByUserId(Long userId) throws ApplicationException {
	    Optional<UserDetail> userDetail = userDetailRepository.findById(userId);
	    
	    if (userDetail.isPresent()) {
	        List<Cart> carts = cartRepository.findByUserDetailAndProductActive(userDetail.get(), true);
	        return carts;
	    } else {
	        throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "User Not found");
	    }
	}

	

	
	public Cart addProductByCart(Cart cartReq) throws ApplicationException {

	    if (cartReq == null) {
	        throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "Data Not Found");
	    }

	    UserDetail userDetail = userDetailRepository.findById(cartReq.getUserDetail().getUserId())
	        .orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "User Not Found"));

	    Product product = productRepository.findById(cartReq.getProduct().getProductId())
	        .orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "Product Not Found"));

	    Optional<Cart> existingCartItem = cartRepository.findByUserDetailUserIdAndProductProductId(
	        cartReq.getUserDetail().getUserId(), cartReq.getProduct().getProductId());

	    if (existingCartItem.isPresent()) {
	        Cart existingCart = existingCartItem.get();

	        if (existingCart.isProductActive()) {
	            throw new ApplicationException(HttpStatus.CONFLICT, 1002, LocalDateTime.now(), "Product already exists in the cart and is active");
	        } else {
	            existingCart.setProductActive(true);
	            existingCart.setQuantity(cartReq.getQuantity());  
	            existingCart.setTotalPrice(cartReq.getTotalPrice());  
	            existingCart.setUpdatedDateTime(LocalDateTime.now());  
	            return cartRepository.save(existingCart);  
	        }
	    }

	    Cart cart = new Cart();
	    cart.setUnitPrice(cartReq.getUnitPrice());
	    cart.setQuantity(cartReq.getQuantity());
	    cart.setTotalPrice(cartReq.getTotalPrice());
	    cart.setProductActive(true);  
	    cart.setUserDetail(userDetail);
	    cart.setProduct(product);
	    cart.setUpdatedDateTime(LocalDateTime.now());

	    return cartRepository.save(cart);  
	}



}
