package com.mart.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mart.entity.UserDetail;
import com.mart.entity.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, Long>{


	Optional<Wallet> findByUserDetail(UserDetail userDetail);

    Wallet findByUserDetailUserId(Long userId);

}
