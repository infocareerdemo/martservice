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

import com.mart.config.GeneralConstant;
import com.mart.dto.WalletRequest;
import com.mart.dto.WalletResponse;
import com.mart.dto.WalletSummary;
import com.mart.entity.Cart;
import com.mart.entity.OrderDetails;
import com.mart.entity.Orders;
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
	                
	                double existingAmount = user.get().getWalletAmount();
	                double requestAmount = walletRequest.getWalletAmount();
	                
	                if (existingAmount >= requestAmount) {
	                	user.get().setWalletAmount(existingAmount - requestAmount);
	                	userDetailRepository.save(user.get());
	                	
	                    
	                    Optional<Orders> order = orderRepository.findById(walletRequest.getOrderId());
	                    if (order.isPresent()) {
	                        order.get().setPaymentStatus(GeneralConstant.PAY_SUCCESS.toString());
	                        order.get().setWalletAmount(requestAmount);
	                        order.get().setCashAmount(walletRequest.getCashAmount());
	                        String orderId = generateOrderId(order.get().getOrderedDateTime());
	                        order.get().setOrderId(orderId);
	                        orderRepository.save(order.get());
	                        
	                        
	                    } else {
	                        throw new ApplicationException(HttpStatus.NOT_FOUND, 1003, LocalDateTime.now(), "Order not found");
	                    }
	                    
	                    
	                    if (walletRequest.getProductIds() != null && !walletRequest.getProductIds().isEmpty()) {
	                        List<Long> productIds = walletRequest.getProductIds();
	                        
	                        List<Cart> carts = cartRepository.findByUserDetailUserIdAndProductProductIdIn(walletRequest.getUserId(), productIds);
	                        
	                        if (!carts.isEmpty()) {
	                            for (Cart cart : carts) {
	                                cart.setProductActive(false);
	                            }
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
	    walletSummary.setOrders(order.get());
	    walletSummary.setOrderDetails(orderDetails);

	    return walletSummary;
	}

	
}


