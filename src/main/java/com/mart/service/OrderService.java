package com.mart.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import com.mart.config.GeneralConstant;
import com.mart.dto.OrderRequest;
import com.mart.dto.OrderSummary;
import com.mart.dto.PaymentRequest;
import com.mart.dto.WalletRequest;
import com.mart.dto.WalletResponse;
import com.mart.entity.Location;
import com.mart.entity.Orders;
import com.mart.entity.OrderDetails;
import com.mart.entity.Product;
import com.mart.entity.UserDetail;
import com.mart.entity.Wallet;
import com.mart.exception.ApplicationException;
import com.mart.repository.LocationRepository;
import com.mart.repository.OrderDetailsRepository;
import com.mart.repository.OrderRepository;
import com.mart.repository.ProductRepository;
import com.mart.repository.UserDetailRepository;
import com.mart.repository.WalletRepository;


@Service
public class OrderService {

	@Autowired
	OrderRepository orderRepository;
	
	@Autowired
	UserDetailRepository userDetailRepository;
	
	@Autowired
	LocationRepository locationRepository;

	@Autowired
	ProductRepository productRepository;
	
	@Autowired
	OrderDetailsRepository orderDetailsRepository;

	@Autowired
	WalletRepository walletRepository;
	
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


	/*public OrderSummary saveOrderWithOrderDetails(List<OrderRequest> orderRequests, Long userId,
			Long locationId) throws Exception{
		OrderSummary orderSummary = new OrderSummary();
		
		if(!CollectionUtils.isEmpty(orderRequests)) {
			Orders order = new Orders();
		     Optional<UserDetail> user = userDetailRepository.findById(userId);
			  if(user.isPresent()) {
				  order.setUserDetail(user.get());
				  order.setAddress(user.get().getAddress());
				    Optional<Location> location =  locationRepository.findById(locationId);
				    order.setLocation(location.get());
				    order.setPaymentStatus(GeneralConstant.PAY_PENDING.toString());
				    order.setOrderedDateTime(LocalDateTime.now());
				    orderRepository.save(order);			  
				    
				    double totAmt =0.0;
				    List<OrderDetails>  orderDetailsRes = new  ArrayList<OrderDetails>();
				    for(OrderRequest orderRequest : orderRequests) {
				    	OrderDetails orderDetails = new OrderDetails();
				        Optional<Product> product =	productRepository.findById(orderRequest.getProductId());
				        if(product.isPresent()) {
				        orderDetails.setOrders(order);
				        orderDetails.setProducts(product.get());
				        orderDetails.setQuantity(orderRequest.getQuantity());
				        orderDetails.setUnitPrice(product.get().getProductPrice());
				        double total = product.get().getProductPrice() * orderRequest.getQuantity();
						orderDetails.setTotalPrice(Double.parseDouble(String.format("%.2f", total)));
						orderDetails.setOrderDateTime(LocalDateTime.now());
						orderDetailsRepository.save(orderDetails);
						totAmt += total;
						orderDetailsRes.add(orderDetails);
				        }
				     }
					order.setOrderAmount(Double.parseDouble(String.format("%.2f", totAmt)));
					orderSummary.setOrders(order);
					orderSummary.setOrderDetails(orderDetailsRes);
					return orderSummary;

			  }else {
					throw new ApplicationException(HttpStatus.UNAUTHORIZED, 1001, LocalDateTime.now(), "Invalid user");

			  }
		}else {
			throw new ApplicationException(HttpStatus.BAD_REQUEST, 1001, LocalDateTime.now(), "No data present");

		}
		
	}*/


	public List<Orders> getOrdersByUserId(Long userId)throws Exception {
		  Optional<UserDetail> user = userDetailRepository.findById(userId);
           if(user.isPresent()) {

        	List<Orders> orders =  orderRepository.findByUserDetailUserId(userId);
        	 if(!CollectionUtils.isEmpty(orders)) {
        		return orders;
        	  }
           }else {
			  throw new ApplicationException(HttpStatus.UNAUTHORIZED, 1001, LocalDateTime.now(), "Invalid user");
           }
		return null;
        	          	   
	}


	public OrderSummary getOrderAndOrderDetailsById(Long id) throws ApplicationException{
		OrderSummary  orderSummary = new OrderSummary();
		 Optional<Orders> order =  orderRepository.findById(id);
		  if(order.isPresent()) {
			List<OrderDetails> orderDetails =  orderDetailsRepository.findByOrdersId(id);
			if(!CollectionUtils.isEmpty(orderDetails)) {
				orderSummary.setOrderDetails(orderDetails);
				orderSummary.setOrders(order.get());				
			}
		  }else {
				throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "No order found");
		  }
		return orderSummary;
	}           
	
	
	public OrderSummary saveOrderWithOrderDetailss(List<OrderRequest> orderRequests, Long userId, Long locationId, PaymentRequest paymentRequest) throws Exception {
	    OrderSummary orderSummary = new OrderSummary();

	    // Validate order requests
	    if (!CollectionUtils.isEmpty(orderRequests)) {
	        Orders order = new Orders();
	        Optional<UserDetail> user = userDetailRepository.findById(userId);
	        
	        if (user.isPresent()) {
	            order.setUserDetail(user.get());
	            order.setAddress(user.get().getAddress());
	            Optional<Location> location = locationRepository.findById(locationId);
	            if (location.isPresent()) {
	                order.setLocation(location.get());
	            } else {
	                throw new ApplicationException(HttpStatus.BAD_REQUEST, 1002, LocalDateTime.now(), "Invalid location");
	            }
	            order.setPaymentStatus(GeneralConstant.PAY_PENDING.toString());
	            order.setOrderedDateTime(LocalDateTime.now());
	            orderRepository.save(order);
	            
	            double totalAmount = 0.0;
	            List<OrderDetails> orderDetailsList = new ArrayList<>();

	            for (OrderRequest orderRequest : orderRequests) {
	                OrderDetails orderDetails = new OrderDetails();
	                Optional<Product> product = productRepository.findById(orderRequest.getProductId());
	                
	                if (product.isPresent()) {
	                    orderDetails.setOrders(order);
	                    orderDetails.setProducts(product.get());
	                    orderDetails.setQuantity(orderRequest.getQuantity());
	                    orderDetails.setUnitPrice(product.get().getProductPrice());
	                    
	                    double total = product.get().getProductPrice() * orderRequest.getQuantity();
	                    orderDetails.setTotalPrice(Double.parseDouble(String.format("%.2f", total)));
	                    orderDetails.setOrderDateTime(LocalDateTime.now());
	                    orderDetailsRepository.save(orderDetails);
	                    totalAmount += total;
	                    orderDetailsList.add(orderDetails);
	                }
	            }

	            // Update order amount
	            order.setOrderAmount(Double.parseDouble(String.format("%.2f", totalAmount)));
	            orderSummary.setOrders(order);
	            orderSummary.setOrderDetails(orderDetailsList);

	            // Process payments using PaymentRequest
	            if (paymentRequest != null) {
	                // Implement your payment processing logic here
	                double walletAmount = paymentRequest.getWalletAmount();
	                double cashAmount = paymentRequest.getCashAmount();
	                double razorpayAmount = paymentRequest.getRazorpayAmount();

	                // Here you would deduct from the wallet and update the order status accordingly
	                // (similar to your previous implementation)
	                
	                // Example: Deduct from wallet and check if more payment is needed
	                if (walletAmount > 0) {
	                    // Check if wallet has enough balance and process payment...
	                }

	                // Handle cash and razorpay payments similarly...
	            }

	            return orderSummary;

	        } else {
	            throw new ApplicationException(HttpStatus.UNAUTHORIZED, 1001, LocalDateTime.now(), "Invalid user");
	        }
	    } else {
	        throw new ApplicationException(HttpStatus.BAD_REQUEST, 1001, LocalDateTime.now(), "No data present");
	    }
	}

	
	@Transactional
	public OrderSummary saveOrderWithOrderDetails(List<OrderRequest> orderRequests, Long userId, Long locationId, PaymentRequest paymentRequest) throws Exception {
	    OrderSummary orderSummary = new OrderSummary();

	    if (!CollectionUtils.isEmpty(orderRequests)) {
	        Orders order = new Orders();
	        Optional<UserDetail> user = userDetailRepository.findById(userId);
	        
	        if (user.isPresent()) {
	            order.setUserDetail(user.get());
	            order.setAddress(user.get().getAddress());
	            Optional<Location> location = locationRepository.findById(locationId);
	            order.setLocation(location.get());
	            order.setPaymentStatus(GeneralConstant.PAY_PENDING.toString());
	            order.setOrderedDateTime(LocalDateTime.now());

	            double totAmt = 0.0;
	            List<OrderDetails> orderDetailsRes = new ArrayList<OrderDetails>();

	            // Save the order first
	            orderRepository.save(order);

	            for (OrderRequest orderRequest : orderRequests) {
	                OrderDetails orderDetails = new OrderDetails();
	                Optional<Product> product = productRepository.findById(orderRequest.getProductId());

	                if (product.isPresent()) {
	                    orderDetails.setOrders(order);
	                    orderDetails.setProducts(product.get());
	                    orderDetails.setQuantity(orderRequest.getQuantity());
	                    orderDetails.setUnitPrice(product.get().getProductPrice());
	                    double total = product.get().getProductPrice() * orderRequest.getQuantity();
	                    orderDetails.setTotalPrice(Double.parseDouble(String.format("%.2f", total)));
	                    orderDetails.setOrderDateTime(LocalDateTime.now());
	                    orderDetailsRepository.save(orderDetails); // Save each order detail
	                    totAmt += total;
	                    orderDetailsRes.add(orderDetails);
	                }
	            }

	            order.setOrderAmount(Double.parseDouble(String.format("%.2f", totAmt)));
	            orderSummary.setOrders(order);
	            orderSummary.setOrderDetails(orderDetailsRes);

	            // Handle payment
	            if (paymentRequest.getWalletAmount() > 0) {
	                Wallet wallet = walletRepository.findByUserDetailUserId(userId);
	                if (wallet != null) {
	                    double currentWalletAmount = wallet.getWalletAmount();
	                    if (currentWalletAmount >= paymentRequest.getWalletAmount()) {
	                        wallet.setWalletAmount(currentWalletAmount - paymentRequest.getWalletAmount());
	                        wallet.setUpdatedDateTime(LocalDateTime.now());
	                        walletRepository.save(wallet);
	        	            order.setPaymentStatus(GeneralConstant.PAY_SUCCESS.toString());
	                        order.setWalletAmount(paymentRequest.getWalletAmount());
	                        String orderId = generateOrderId(order.getOrderedDateTime());
	                        order.setOrderId(orderId);
	                    } else {
	                        throw new ApplicationException(HttpStatus.BAD_REQUEST, 1002, LocalDateTime.now(), "Insufficient wallet balance");
	                    }
	                } else {
	                    throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "Wallet not found");
	                }
	            }

	            if (paymentRequest.getRazorpayAmount() > 0) {
	                order.setRazorpayAmount(paymentRequest.getRazorpayAmount());
	            }

	            if (paymentRequest.getCashAmount() > 0) {
	                order.setCashAmount(paymentRequest.getCashAmount());
	            }

	            orderRepository.save(order); // Save order again with payment information

	            return orderSummary;

	        } else {
	            throw new ApplicationException(HttpStatus.UNAUTHORIZED, 1001, LocalDateTime.now(), "Invalid user");
	        }

	    } else {
	        throw new ApplicationException(HttpStatus.BAD_REQUEST, 1001, LocalDateTime.now(), "No data present");
	    }
	}

	
}
