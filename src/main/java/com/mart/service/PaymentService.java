package com.mart.service;

import java.security.SignatureException;
import java.time.LocalDateTime;
import java.util.Optional;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.mart.dto.OrderSummary;
import com.mart.entity.Orders;
import com.mart.entity.RazorPayDetails;
import com.mart.entity.RazorpayPayment;
import com.mart.entity.UserDetail;
import com.mart.exception.ApplicationException;
import com.mart.repository.OrderRepository;
import com.mart.repository.RazorPayDetailsRepository;
import com.mart.repository.RazorpayPaymentRepository;
import com.mart.repository.UserDetailRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;



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
	
	
	private static final String SECRET_ID = "rzp_test_fvYUjpmIHqB5nx";

	private static final String SECRET_KEY = "j7d8lkf1RqAs3ni8Ngt4g8bC";
	
	

	public PaymentService() throws RazorpayException {
		this.client = new RazorpayClient(SECRET_ID, SECRET_KEY);
	}
		    
	    

	public RazorPayDetails createOrder(Long id, Long oid) throws ApplicationException {
		Order order = new Order(null);
		try {
			Optional<UserDetail> userDetails = userDetailRepository.findById(id);
			if (userDetails.isPresent()) {
				Optional<Orders> orders = orderRepository.findById(oid);
				order = createRazorPayOrder(String.valueOf(orders.get().getTotalAmount()));
				RazorPayDetails razorPay = getRazorPay((String) order.get("id"), userDetails.get(),
						orders.get().getTotalAmount(), orders.get());

				return razorPay;
			} else {
				throw new ApplicationException(HttpStatus.UNAUTHORIZED, 1001, LocalDateTime.now(), "Invalid user");
			}
		} catch (RazorpayException e) {
			e.printStackTrace();
		}
		if (order.get("id") == null || order.get("id").toString().isEmpty()) {
			return null;
		}
		return null;
	}
	
	

	private RazorPayDetails getRazorPay(String orderId, UserDetail userLogin, double amount, Orders orders) {
		RazorPayDetails razorPayDetails = new RazorPayDetails();
		razorPayDetails.setApplicationFee(convertRupeeToPaise(String.valueOf(amount)));
		razorPayDetails.setCustomerContact(userLogin.getPhone());
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
		options.put("payment_capture", 1); // You can enable this if you want to do Auto Capture.
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
			
					orderSummary.setOrders(order);
				
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
					orderSummary.setOrders(order);
				
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


}
