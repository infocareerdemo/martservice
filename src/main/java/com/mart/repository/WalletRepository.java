package com.mart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mart.entity.UserDetail;
import com.mart.entity.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, Long>{


	Optional<Wallet> findByUserDetail(UserDetail userDetail);

    // Correct method to find by userId in UserDetail
    Wallet findByUserDetailUserId(Long userId);

}
