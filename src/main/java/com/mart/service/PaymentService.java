package com.mart.service;

import java.security.SignatureException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import com.mart.config.GeneralConstant;
import com.mart.dto.OrderSummary;
import com.mart.dto.PaymentRequest;
import com.mart.dto.PaymentResponse;
import com.mart.dto.WalletRequest;
import com.mart.dto.WalletResponse;
import com.mart.entity.Cart;
import com.mart.entity.OrderDetails;
import com.mart.entity.Orders;
import com.mart.entity.RazorPayDetails;
import com.mart.entity.RazorpayPayment;
import com.mart.entity.UserDetail;
import com.mart.exception.ApplicationException;
import com.mart.repository.OrderDetailsRepository;
import com.mart.repository.OrderRepository;
import com.mart.repository.RazorPayDetailsRepository;
import com.mart.repository.RazorpayPaymentRepository;
import com.mart.repository.UserDetailRepository;
import com.mart.repository.WalletRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.mart.entity.Wallet;



@Service
public class PaymentService {
	
	private RazorpayClient client;
	
	@Autowired
	RazorpayPaymentRepository razorpayPaymentRepository;

	@Autowired
	RazorPayDetailsRepository razorPayDetailsRepository;
	
	@Autowired
	OrderRepository orderRepository;
	
	@Autowired
	UserDetailRepository userDetailRepository;
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	WalletRepository walletRepository;
	
	

	@Autowired
	OrderDetailsRepository orderDetailsRepository;
	
	
	//private static final String SECRET_ID = "rzp_test_fvYUjpmIHqB5nx";

	//private static final String SECRET_KEY = "j7d8lkf1RqAs3ni8Ngt4g8bC";
	
	private static final String SECRET_ID = "rzp_test_8xlK20ea4017KA";
    private static final String SECRET_KEY = "ErQStpcyY98zxGMGrTClEsfy";

	public PaymentService() throws RazorpayException {
		this.client = new RazorpayClient(SECRET_ID, SECRET_KEY);
	}
		    
	
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
	
	    

	public RazorPayDetails createOrder(Long id, Long oid, double razorpayAmount) throws ApplicationException {


	    Order razorPayOrder = new Order(null);
	    try {
	        Optional<UserDetail> userDetails = userDetailRepository.findById(id);
	        if (userDetails.isPresent()) {
	            Optional<Orders> orders = orderRepository.findById(oid);
	            if (orders.isPresent()) {
	            	orders.get().setRazorpayAmount(razorpayAmount);
	            	
	            	orderRepository.save(orders.get());
	              //  razorPayOrder = createRazorPayOrder(String.valueOf(orders.get().getTotalAmount()));
	            	 razorPayOrder = createRazorPayOrder(String.valueOf(razorpayAmount));
	       
	                RazorPayDetails razorPayDetails = getRazorPay((String) razorPayOrder.get("id"), 
	                                                              userDetails.get(), 
	                                                              razorpayAmount,
	                                                             // orders.get().getRazorpayAmount(), 
	                                                              orders.get());
	                return razorPayDetails;
	            } else {
	                throw new ApplicationException(HttpStatus.NOT_FOUND, 1002, LocalDateTime.now(), "Order not found");
	            }
	        } else {
	            throw new ApplicationException(HttpStatus.UNAUTHORIZED, 1001, LocalDateTime.now(), "Invalid user");
	        }
	    } catch (RazorpayException e) {
	        e.printStackTrace();
	        throw new ApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, 1003, LocalDateTime.now(), 
	                                       "Error while creating RazorPay order");
	    }
	}
	
	
	private RazorPayDetails getRazorPay(String orderId, UserDetail userLogin, double amount, Orders orders) {
		RazorPayDetails razorPayDetails = new RazorPayDetails();
		razorPayDetails.setApplicationFee(convertRupeeToPaise(String.valueOf(amount)));
		//razorPayDetails.setCustomerName(userLogin.getUserName());
		//razorPayDetails.setCustomerEmail(userLogin.getEmailId());
		razorPayDetails.setMerchantName("Test");
		razorPayDetails.setPurchaseDescription("TEST PURCHASES");
		razorPayDetails.setRazorpayOrderId(orderId);
		razorPayDetails.setSecretId(SECRET_ID);
		razorPayDetails.setTheme("#F37254");
		razorPayDetails.setNotes("notes" + orderId);
		razorPayDetails.setCustomerContact(userLogin.getPhone());
		razorPayDetails.setOrders(orders);
		razorPayDetails.setRazorpayDateTime(LocalDateTime.now());

		razorPayDetailsRepository.save(razorPayDetails);
		return razorPayDetails;
	}

	private Order createRazorPayOrder(String amount) throws RazorpayException {
		JSONObject options = new JSONObject();
		options.put("amount", convertRupeeToPaise(amount));
		options.put("currency", "INR");
		options.put("receipt", "txn_123456");
		options.put("payment_capture", 1); 
		return client.orders.create(options);
	}

	private String convertRupeeToPaise(String paise) {
		double rupeesDouble = Double.parseDouble(paise);
		int amt = (int) Math.round(rupeesDouble * 100);
		return Integer.toString(amt);
	}

	public OrderSummary isPaymentSuccess(RazorpayPayment razorpayPayment) {
		try {
			Optional<Orders> orders = orderRepository.findById(razorpayPayment.getOrders().getId());
			RazorPayDetails razorPayDetails = razorPayDetailsRepository
					.findByRazorpayOrderId(razorpayPayment.getRazorPayDetails().getRazorpayOrderId());
			String generatedSignature = hmacSha256(
					razorpayPayment.getRazorPayDetails().getRazorpayOrderId() + "|" + razorpayPayment.getPaymentId(),
					SECRET_KEY);
			if (generatedSignature.equals(razorpayPayment.getSignature())) {
				// update payment status and generate order Id
				Orders order = orderService.updatePaymentStatus(orders.get(), true);

				razorPayDetails.setOrders(order);

				razorPayDetailsRepository.save(razorPayDetails);

				razorpayPayment.setOrders(order);
				razorpayPayment.setSuccess(true);
				razorpayPayment.setRazorPayDetails(razorPayDetails);
				razorpayPayment.setPaymentDateTime(LocalDateTime.now());

				razorpayPaymentRepository.save(razorpayPayment);

				OrderSummary orderSummary = new OrderSummary();
				List<OrderDetails> orderDetails = orderDetailsRepository.findByOrdersId(order.getId());
				if (!CollectionUtils.isEmpty(orderDetails)) {
					orderSummary.setOrders(order);
					orderSummary.setOrderDetails(orderDetails);
				}
				return orderSummary;
			} else {
				// update payment status and generate order Id
				Orders order = orderService.updatePaymentStatus(orders.get(), false);

				razorPayDetails.setOrders(order);

				razorPayDetailsRepository.save(razorPayDetails);

				razorpayPayment.setOrders(order);
				razorpayPayment.setSuccess(false);
				razorpayPayment.setRazorPayDetails(razorPayDetails);
				razorpayPayment.setPaymentDateTime(LocalDateTime.now());

				razorpayPaymentRepository.save(razorpayPayment);
				OrderSummary orderSummary = new OrderSummary();
				List<OrderDetails> orderDetails = orderDetailsRepository.findByOrdersId(order.getId());
				if (!CollectionUtils.isEmpty(orderDetails)) {
					orderSummary.setOrders(order);
					orderSummary.setOrderDetails(orderDetails);
				}
				return orderSummary;
			}
		} catch (SignatureException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String hmacSha256(String data, String secret) throws java.security.SignatureException {
		String result;
		try {
			SecretKeySpec signingKey = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(signingKey);
			byte[] rawHmac = mac.doFinal(data.getBytes());
			result = DatatypeConverter.printHexBinary(rawHmac).toLowerCase();
		} catch (Exception e) {
			throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
		}
		return result;
	}

	
	//new paywallet and cash method
	public PaymentResponse payByWallet(WalletRequest walletRequest) throws ApplicationException {
	    PaymentResponse paymentResponse = new PaymentResponse();
	    double reqWalletAmount = walletRequest.getCashAmount();
	    double reqCashAmount = walletRequest.getWalletAmount();

	    
	        Optional<UserDetail> userDetail = userDetailRepository.findById(walletRequest.getUserId());
	        if (userDetail.isEmpty()) {
	            throw new ApplicationException(HttpStatus.NOT_FOUND, 1002, LocalDateTime.now(), "User not found");
	        }
	        
	        Optional<Orders> orders = orderRepository.findById(walletRequest.getOrderId());
	        if (orders.isEmpty()) {
	            throw new ApplicationException(HttpStatus.NOT_FOUND, 1002, LocalDateTime.now(), "Order not found");
	        }
	        
	        List<OrderDetails> orderDetails = orderDetailsRepository.findByOrdersId(walletRequest.getOrderId());
	        if (orderDetails.isEmpty()) {
	            throw new ApplicationException(HttpStatus.NOT_FOUND, 1002, LocalDateTime.now(), "Order Details not found");
	        }			 
	    	
	        double existingWalletAmount = userDetail.get().getWalletAmount();

	        // Wallet only	    	
	        if (reqWalletAmount > 0) {
	            if (existingWalletAmount >= reqWalletAmount) {
	                userDetail.get().setWalletAmount(existingWalletAmount - reqWalletAmount);
	                userDetailRepository.save(userDetail.get());
	                
	                Orders order = orders.get();
	                order.setPaymentStatus(GeneralConstant.PAY_SUCCESS.toString());
	                order.setWalletAmount(reqWalletAmount);
	                String orderId = generateOrderId(order.getOrderedDateTime());
	                order.setOrderId(orderId);
	                orderRepository.save(order);
	                
	                paymentResponse.setWalletAmount(reqWalletAmount);
	                paymentResponse.setOrderDetails(orderDetails);
	                paymentResponse.setOrders(order);
	                paymentResponse.setSuccessMsg("Wallet only Paid!!");
	            } else {
	                throw new ApplicationException(HttpStatus.BAD_REQUEST, 1004, LocalDateTime.now(), "Insufficient wallet balance");
	            }
	        }
	        // Cash only	
	        else if (reqCashAmount > 0) {
	            Orders order = orders.get();
	            order.setCashAmount(reqCashAmount);
	            order.setPaymentStatus(GeneralConstant.PAY_SUCCESS.toString());
	            String orderId = generateOrderId(order.getOrderedDateTime());
	            order.setOrderId(orderId);
	            orderRepository.save(order);
	            
	            paymentResponse.setCashAmount(reqCashAmount);
	            paymentResponse.setOrderDetails(orderDetails);
	            paymentResponse.setOrders(order);
	            paymentResponse.setSuccessMsg("Cash only Paid!!");
	        }
	        // Wallet + cash only	    	

	        else if (reqWalletAmount > 0 && reqCashAmount > 0) {
	            if (existingWalletAmount >= reqWalletAmount) {
	                userDetail.get().setWalletAmount(existingWalletAmount - reqWalletAmount);
	                userDetailRepository.save(userDetail.get());
	                
	                Orders order = orders.get();
	                order.setWalletAmount(reqWalletAmount);
	                order.setCashAmount(reqCashAmount);
	                order.setPaymentStatus(GeneralConstant.PAY_SUCCESS.toString());
	                String orderId = generateOrderId(order.getOrderedDateTime());
	                order.setOrderId(orderId);
	                orderRepository.save(order);
	                
	                paymentResponse.setCashAmount(reqCashAmount);
	                paymentResponse.setWalletAmount(reqWalletAmount);
	                paymentResponse.setOrderDetails(orderDetails);
	                paymentResponse.setOrders(order);
	                paymentResponse.setSuccessMsg("Wallet and cash Paid!!");
	            } else{
	                throw new ApplicationException(HttpStatus.BAD_REQUEST, 1004, LocalDateTime.now(), "Insufficient wallet balance");
	            }
	        }else if(reqWalletAmount > 0 &&  walletRequest.getRazorPaymentStatus() == "SUCCESS") {
	        	   if (existingWalletAmount >= reqWalletAmount) {
		                userDetail.get().setWalletAmount(existingWalletAmount - reqWalletAmount);
		                userDetailRepository.save(userDetail.get());
		                
		                Orders order = orders.get();
		                order.setWalletAmount(reqWalletAmount);

		                paymentResponse.setRazorpayAmount(order.getRazorpayAmount());
		                paymentResponse.setWalletAmount(reqWalletAmount);
		                paymentResponse.setOrderDetails(orderDetails);
		                paymentResponse.setOrders(order);
		                paymentResponse.setSuccessMsg("Razorpay Success and Wallet Paid!!");
		                
	        	   }else {
		                throw new ApplicationException(HttpStatus.BAD_REQUEST, 1004, LocalDateTime.now(), "Insufficient wallet balance");

	        	   }
	        	
	        }
	        
	        
	        else {
	            throw new ApplicationException(HttpStatus.NOT_FOUND, 1002, LocalDateTime.now(), "Wallet not found");
	        }
	    

	    return paymentResponse;
	}

}





 /*public PaymentResponse payAmount(Long userId, Long orderId, PaymentRequest paymentRequest) throws ApplicationException {
	PaymentResponse paymentResponse = new PaymentResponse();
	if (paymentRequest != null) {
        double reqWalletAmount = paymentRequest.getWalletAmount();
        double reqCashAmount = paymentRequest.getCashAmount();
        double reqRazorpayAmount = paymentRequest.getRazorpayAmount();
        
        Optional<Orders> orders = orderRepository.findById(orderId);
		List<OrderDetails> orderDetails = orderDetailsRepository.findByOrdersId(orderId);

          
        //Wallet
       if(reqWalletAmount > 0 &&  reqCashAmount == 0 &&  reqRazorpayAmount == 0) {
            Wallet wallet = walletRepository.findByUserDetailUserId(userId);
              if(wallet !=null) {
            	  if(wallet.getWalletAmount() >= reqWalletAmount) {
            		  
            		  wallet.setWalletAmount(wallet.getWalletAmount() - reqWalletAmount);
	                  wallet.setUpdatedDateTime(LocalDateTime.now());
	                  
	      			
		                    orders.get().setPaymentStatus(GeneralConstant.PAY_SUCCESS.toString());
	                        orders.get().setWalletAmount(reqWalletAmount);

	                        String orderIdd = generateOrderId(orders.get().getOrderedDateTime());
	                        orders.get().setOrderId(orderIdd);
	                        
			                walletRepository.save(wallet);		                    		           
	                        orderRepository.save(orders.get());
	                        
		                    
	                        paymentResponse.setWalletAmount(reqWalletAmount);
		                    paymentResponse.setOrderDetails(orderDetails);
		                    paymentResponse.setOrders(orders.get());
		                    paymentResponse.setSuccessMsg("Wallet only  Paid!!");
                       //  return paymentResponse;  
            	  }else {
	      	            throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "Insuffienct Amount ");

            	  }
            	  
              }else {
                  throw new ApplicationException(HttpStatus.NOT_FOUND, 1002, LocalDateTime.now(), "Wallet not found");

              }
    	   
       }
       
       
       
        //Cash
       else if(reqWalletAmount == 0 &&  reqCashAmount > 0 &&  reqRazorpayAmount == 0) {
    	   
    	    orders.get().setPaymentStatus(GeneralConstant.PAY_SUCCESS.toString());
            orders.get().setCashAmount(reqCashAmount);

            String orderIdd = generateOrderId(orders.get().getOrderedDateTime());
            orders.get().setOrderId(orderIdd);
            
            orderRepository.save(orders.get());
            
            
            paymentResponse.setCashAmount(reqCashAmount);
            paymentResponse.setOrderDetails(orderDetails);
            paymentResponse.setOrders(orders.get());
            paymentResponse.setSuccessMsg("Cash only  Paid!!");
           // return paymentResponse; 
       }
       
       
       
        //Razorpay
       else if(reqWalletAmount == 0 &&  reqCashAmount == 0 &&  reqRazorpayAmount > 0) {
    	   orders.get().setRazorpayAmount(reqRazorpayAmount);
           orderRepository.save(orders.get());
           
    	     RazorPayDetails razorPayDetails = createOrderr(userId, orderId);
                if(razorPayDetails != null) {
                	Orders ordr =  razorPayDetails.getOrders();
                	   paymentResponse.setRazorpayAmount(reqRazorpayAmount);
   	                   paymentResponse.setOrderDetails(orderDetails);
   	                   paymentResponse.setOrders(orders.get());
   	                   paymentResponse.setRazorPayDetails(razorPayDetails);
   	                   paymentResponse.setSuccessMsg("Razorpay only  Paid!!");
                	if(ordr.getPaymentStatus().equals("PAY_SUCCESS")){
                    		                
                	
   	                   //return 	paymentResponse;
                                          
                    	
                    }else {
	                    throw new ApplicationException(HttpStatus.NOT_FOUND, 1002, LocalDateTime.now(), "Razorpay not paid");

                    }
                }else {
                    throw new ApplicationException(HttpStatus.NOT_FOUND, 1002, LocalDateTime.now(), "Razorpay details not found");

                }
                 
    	   
       }
       
       
       //Wallet + cash
       else if(reqWalletAmount > 0 &&  reqCashAmount > 0 &&  reqRazorpayAmount == 0) {
    	   
           Wallet wallet = walletRepository.findByUserDetailUserId(userId);
           if(wallet !=null) {
         	  if(wallet.getWalletAmount() >= reqWalletAmount) {
         		  
         		        wallet.setWalletAmount(wallet.getWalletAmount() - reqWalletAmount);
	                    wallet.setUpdatedDateTime(LocalDateTime.now());                  
	      			
	                    orders.get().setPaymentStatus(GeneralConstant.PAY_SUCCESS.toString());
                        orders.get().setWalletAmount(reqWalletAmount);
                        orders.get().setCashAmount(reqCashAmount);

                        String orderIdd = generateOrderId(orders.get().getOrderedDateTime());
                        orders.get().setOrderId(orderIdd);
                        
		                walletRepository.save(wallet);		                    		           
                        orderRepository.save(orders.get());
                        
	                    
                        paymentResponse.setWalletAmount(reqWalletAmount);
                        paymentResponse.setCashAmount(reqCashAmount);
	                    paymentResponse.setOrderDetails(orderDetails);
	                    paymentResponse.setOrders(orders.get());
	                    paymentResponse.setSuccessMsg("Wallet Paid!! and cash will pay");
                     // return paymentResponse;  
         	  }else {
      	            throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "Insuffienct Amount ");

         	  }
         	  
           }else {
               throw new ApplicationException(HttpStatus.NOT_FOUND, 1002, LocalDateTime.now(), "Wallet not found");

           }
    	   
       }
       
       
       
       
       
       //Wallet + razorpay
       else if(reqWalletAmount > 0 &&  reqCashAmount == 0 &&  reqRazorpayAmount > 0) {
    	   
    	   orders.get().setRazorpayAmount(reqRazorpayAmount);
           orders.get().setWalletAmount(reqWalletAmount);

           orderRepository.save(orders.get());
           
    	     RazorPayDetails razorPayDetails = createOrderr(userId, orderId);
                if(razorPayDetails != null) {
                	Orders ordr =  razorPayDetails.getOrders();
                	if(ordr.getPaymentStatus().equals("PAY_SUCCESS")){
                		
         	           Wallet wallet = walletRepository.findByUserDetailUserId(userId);
                           
         	          if(wallet !=null) {
                     	  if(wallet.getWalletAmount() >= reqWalletAmount) {
                     		  
                     		        wallet.setWalletAmount(wallet.getWalletAmount() - reqWalletAmount);
        		                    wallet.setUpdatedDateTime(LocalDateTime.now());                  	        		      			
        	                        
        			                walletRepository.save(wallet);		                    		           
   		                    

                     	  }else {
        	      	            throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "Insuffienct Amount ");

                     	  }
                     	  
                       }
         	          
                       paymentResponse.setWalletAmount(reqWalletAmount);                
                	   paymentResponse.setRazorpayAmount(reqRazorpayAmount);
   	                   paymentResponse.setOrderDetails(orderDetails);
   	                   paymentResponse.setOrders(orders.get());
   	                   paymentResponse.setSuccessMsg("Razorpay and wallet Paid!!");
   	                 //  return 	paymentResponse;
                                          
                    	
                    }else {
	                    throw new ApplicationException(HttpStatus.NOT_FOUND, 1002, LocalDateTime.now(), "Razorpay not paid");

                    }
                }else {
                    throw new ApplicationException(HttpStatus.NOT_FOUND, 1002, LocalDateTime.now(), "Razorpay details not found");

                }
       }else {
            throw new ApplicationException(HttpStatus.UNAUTHORIZED, 1001, LocalDateTime.now(), "Invalid user");

         }
	   }else {
           throw new ApplicationException(HttpStatus.NOT_FOUND, 1002, LocalDateTime.now(), "Order not found");

	   }
	return paymentResponse;
	}*/
