package com.mart.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import com.mart.config.GeneralConstant;
import com.mart.dto.WalletRequest;
import com.mart.dto.WalletResponse;
import com.mart.dto.WalletSummary;
import com.mart.entity.Cart;
import com.mart.entity.OrderDetails;
import com.mart.entity.Orders;
import com.mart.entity.Product;
import com.mart.entity.UserDetail;
import com.mart.entity.Wallet;
import com.mart.exception.ApplicationException;
import com.mart.repository.CartRepository;
import com.mart.repository.OrderDetailsRepository;
import com.mart.repository.OrderRepository;
import com.mart.repository.ProductRepository;
import com.mart.repository.UserDetailRepository;
import com.mart.repository.WalletRepository;


@Service
public class WalletService {
	
	@Autowired
	WalletRepository walletRepository;
	
	@Autowired
	UserDetailRepository userDetailRepository;
	
	@Autowired
	OrderRepository orderRepository;
	
	@Autowired
	OrderDetailsRepository orderDetailsRepository;
	
	@Autowired
	CartRepository cartRepository;
	

	@Autowired
	ProductRepository productRepository;
	
	
	public String generateOrderId(LocalDateTime orderDateTime) {
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMdd");
		LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
		LocalDateTime endOfDay = startOfDay.plusDays(1);
		List<Orders> orders = orderRepository.findByOrderedDateTimeBetween(startOfDay, endOfDay);
		String orderId = null;
		if (!CollectionUtils.isEmpty(orders)) {
			orders = orders.stream().filter(o -> (o.getOrderId() != null && !o.getOrderId().isEmpty()))
					.sorted(Comparator.comparing(Orders::getOrderId)).collect(Collectors.toList());
			if (!CollectionUtils.isEmpty(orders)) {
				orderId = orders.get(orders.size() - 1).getOrderId();
			}
		}
		String oid = null;
		if (orderId != null && !orderId.isEmpty()) {
			String[] id = orderId.split("-");
			int n = Integer.parseInt(id[1]) + 1;
			oid = "ORDER#" + orderDateTime.format(format) + "-" + String.format("%04d", n);
		} else {
			oid = "ORDER#" + orderDateTime.format(format) + "-" + String.format("%04d", 1);
		}
		return oid;
	}

	
	public Orders updatePaymentStatus(Orders orders, boolean status) {
		if (status == true) {
			// Generate order id
			String oid = generateOrderId(orders.getOrderedDateTime());
			orders.setOrderId(oid);
			orders.setPaymentStatus(GeneralConstant.PAY_SUCCESS.toString());
			orderRepository.save(orders);

			return orders;
		} else {
			// Generate order id
			String oid = generateOrderId(orders.getOrderedDateTime());
			orders.setOrderId(oid);
			orders.setPaymentStatus(GeneralConstant.PAY_FAILED.toString());
			orderRepository.save(orders);

			return orders;
		}
	}
	
	
	public Wallet getWalletDetailsByUserId(Long id) {
		Wallet wallet =	walletRepository.findByUserDetailUserId(id);
			return wallet;
		}

	

	
	public WalletResponse payByWallet(WalletRequest walletRequest) throws ApplicationException {
	    if (walletRequest != null) {
	        Optional<UserDetail> user = userDetailRepository.findById(walletRequest.getUserId());
	        if (user.isPresent()) {
	            Wallet wallet = walletRepository.findByUserDetailUserId(walletRequest.getUserId());
	            if (wallet != null) {
	                
	                double existingAmount = wallet.getWalletAmount();
	                double requestAmount = walletRequest.getWalletAmount();
	                
	                if (existingAmount >= requestAmount) {
	                    wallet.setWalletAmount(existingAmount - requestAmount);
	                    wallet.setUpdatedDateTime(LocalDateTime.now());
	                    walletRepository.save(wallet);
	                    
	                    Optional<Orders> order = orderRepository.findById(walletRequest.getOrderId());
	                    if (order.isPresent()) {
	                        order.get().setPaymentStatus(GeneralConstant.PAY_SUCCESS.toString());
	                        order.get().setWalletAmount(requestAmount);
	                        String orderId = generateOrderId(order.get().getOrderedDateTime());
	                        order.get().setOrderId(orderId);
	                        orderRepository.save(order.get());
	                        
	                        
	                    } else {
	                        throw new ApplicationException(HttpStatus.NOT_FOUND, 1003, LocalDateTime.now(), "Order not found");
	                    }
	                    
	                    
	                    // Check if product IDs exist and update productActive flag in the Cart
	                    if (walletRequest.getProductIds() != null && !walletRequest.getProductIds().isEmpty()) {
	                        List<Long> productIds = walletRequest.getProductIds();
	                        
	                        // Fetch carts with matching product IDs for the current user
	                        List<Cart> carts = cartRepository.findByUserDetailUserIdAndProductProductIdIn(walletRequest.getUserId(), productIds);
	                        
	                        if (!carts.isEmpty()) {
	                            // Set productActive to false for each cart entry with matching productId
	                            for (Cart cart : carts) {
	                                cart.setProductActive(false);
	                            }
	                            // Save updated cart entities
	                            cartRepository.saveAll(carts);
	                        }
	                    }
	                    
	                    WalletResponse walletResponse = new WalletResponse();
	                    walletResponse.setSuccess(true);
	                    return walletResponse;
	                } else {
	                    throw new ApplicationException(HttpStatus.BAD_REQUEST, 1004, LocalDateTime.now(), "Insufficient wallet balance");
	                }
	            } else {
	                throw new ApplicationException(HttpStatus.BAD_REQUEST, 1000, LocalDateTime.now(), "Wallet not found for this user");
	            }
	        } else {
	            throw new ApplicationException(HttpStatus.NOT_FOUND, 1002, LocalDateTime.now(), "User not found");
	        }
	    } else {
	        throw new ApplicationException(HttpStatus.BAD_REQUEST, 1001, LocalDateTime.now(), "Invalid wallet request data");
	    }
	    
	    
	}


	public WalletSummary getOrderDetails(Long userId, Long orderId) throws ApplicationException {
	    
	    Optional<UserDetail> userDetail = userDetailRepository.findById(userId);
	    if (!userDetail.isPresent()) {
	        throw new ApplicationException(HttpStatus.NOT_FOUND, 1002, LocalDateTime.now(), "User not found");
	    }
	    
	    Optional<Orders> order = orderRepository.findById(orderId);
	    if (!order.isPresent() || !order.get().getUserDetail().getUserId().equals(userId)) {
	        throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "Order not found for this user");
	    }

	    List<OrderDetails> orderDetails = orderDetailsRepository.findByOrdersId(orderId);
	    if (orderDetails == null || orderDetails.isEmpty()) {
	        throw new ApplicationException(HttpStatus.NOT_FOUND, 1003, LocalDateTime.now(), "Order details not found");
	    }

	    WalletSummary walletSummary = new WalletSummary();
	    walletSummary.setOrder(order.get());
	    walletSummary.setOrderDetails(orderDetails);

	    return walletSummary;
	}



	
}



/*public WalletResponse payByWallet(Long id, double amount) throws Exception {
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
}*/




/*public WalletResponse payByWallet(WalletRequest walletRequest) throws ApplicationException{
// TODO Auto-generated method stub
if(walletRequest !=null) {
    Optional<UserDetail> user = userDetailRepository.findById(walletRequest.getUserId());
       if(user !=null) {
             Wallet wallet = walletRepository.findByUserDetailUserId(walletRequest.getUserId());
            if(wallet != null) {
            	
            	
            	
            	double existingAmount = wallet.getWalletAmount();
            	double requestAmount = walletRequest.getWalletAmount();
            	
            	wallet.setWalletAmount(existingAmount - requestAmount);
                wallet.setUpdatedDateTime(LocalDateTime.now());
                walletRepository.save(wallet);

                Optional<Orders> order =   orderRepository.findById(walletRequest.getOrderId());
                 
                if(order !=null) {
                	order.get().setPaymentStatus(GeneralConstant.PAY_SUCCESS.toString());
                	order.get().setWalletAmount(requestAmount);
                    String orderId = generateOrderId(order.get().getOrderedDateTime());
                	order.get().setOrderId(orderId);
                	orderRepository.save(order.get());
                }
                
                WalletResponse walletResponse  = new WalletResponse();
                walletResponse.setSuccess(true);
                return walletResponse;
                
                
            	
            }else {
    	        throw new ApplicationException(HttpStatus.BAD_REQUEST, 1000, LocalDateTime.now(), "Invalid User");

            }
    	   
       }else {
            throw new ApplicationException(HttpStatus.NOT_FOUND, 1002, LocalDateTime.now(), "User not found");

       }
    
}else {
    throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "Data not found");

}
}*/
