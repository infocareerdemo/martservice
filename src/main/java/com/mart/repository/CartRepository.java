package com.mart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mart.entity.Cart;
import com.mart.entity.UserDetail;
import com.mart.service.CartService;

public interface CartRepository extends JpaRepository<Cart, Long>{
	
    List<Cart> findByUserDetail(Optional<UserDetail> userDetail);

	Optional<Cart> findByUserDetailUserIdAndProductProductId(Long userId, Long productId);

	List<Cart> findByUserDetailUserIdAndProductProductIdIn(Long userId, List<Long> productIds);


	
}
