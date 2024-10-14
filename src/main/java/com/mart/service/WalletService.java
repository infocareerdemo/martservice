package com.mart.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.mart.dto.WalletResponse;
import com.mart.entity.UserDetail;
import com.mart.entity.Wallet;
import com.mart.exception.ApplicationException;
import com.mart.repository.UserDetailRepository;
import com.mart.repository.WalletRepository;


@Service
public class WalletService {
	
	@Autowired
	WalletRepository walletRepository;
	
	@Autowired
	UserDetailRepository userDetailRepository;
	
	public Wallet getWalletDetailsByUserId(Long id) {
		Wallet wallet =	walletRepository.findByUserDetailUserId(id);
			return wallet;
		}

	public WalletResponse payByWallet(Long id, double amount) throws Exception {
	    if (id != null) {
	        Optional<UserDetail> user = userDetailRepository.findById(id);
	        if (user.isPresent()) {
	            Wallet wallet = walletRepository.findByUserDetailUserId(id);
	            if (wallet != null) {
	                double totalOrderAmount = amount;
	                double walletBalance = wallet.getWalletAmount();

	                WalletResponse response = new WalletResponse();

	                if (walletBalance >= totalOrderAmount) {
	                    // Full payment by wallet
	                    wallet.setWalletAmount(walletBalance - totalOrderAmount);
	                    wallet.setUpdatedDateTime(LocalDateTime.now());
	                    walletRepository.save(wallet);

	                    response.setAmount(totalOrderAmount);       // The amount paid using the wallet
	                    response.setRemainingAmount(wallet.getWalletAmount());  // Remaining wallet balance
	                    response.setPendingAmount(0.0);             // No pending amount
	                    response.setMessage("Payment successful! Full amount paid from the wallet.");
	                    response.setSuccess(true); // Set success to true
	                } else {
	                    // Partial payment: Deduct whatever is available in the wallet
	                    double remainingAmount = totalOrderAmount - walletBalance; // Amount still pending

	                    wallet.setWalletAmount(0.0); // Empty the wallet
	                    wallet.setUpdatedDateTime(LocalDateTime.now());
	                    walletRepository.save(wallet);

	                    response.setAmount(walletBalance);          // The amount paid using the wallet
	                    response.setRemainingAmount(0.0);           // Wallet is now empty
	                    response.setPendingAmount(remainingAmount);  // The remaining amount to be paid
	                    response.setMessage("Partial payment successful! Wallet exhausted, remaining amount to be paid: ₹" + remainingAmount);
	                    response.setSuccess(true); // Set success to true for partial payment as well
	                }

	                return response;  // Return the response object with the amount, remaining balance, and pending amount
	            } else {
	                throw new ApplicationException(HttpStatus.NOT_FOUND, 1002, LocalDateTime.now(), "Wallet not found");
	            }
	        } else {
	            throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "User not found");
	        }
	    } else {
	        throw new ApplicationException(HttpStatus.BAD_REQUEST, 1000, LocalDateTime.now(), "Invalid user ID");
	    }
	}




	
}
