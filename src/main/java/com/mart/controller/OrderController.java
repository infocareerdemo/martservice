package com.mart.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mart.dto.OrderRequest;
import com.mart.dto.OrderSaveRequest;
import com.mart.dto.OrderStatusDto;
import com.mart.dto.OrderWithPaymentRequest;
import com.mart.dto.PaymentRequest;
import com.mart.dto.WalletRequest;
import com.mart.service.OrderService;

import jakarta.persistence.Entity;
import lombok.Data;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController {

	
	@Autowired
	OrderService orderService;
	
	
	
	
	@PostMapping("/save")
	public ResponseEntity<Object> saveOrderAndOrderDetails(@RequestBody List<OrderRequest> orderRequests,
			@RequestParam Long userId, @RequestParam Long locationId) throws Exception {
		return new ResponseEntity<Object>(orderService.saveOrderWithOrderDetails(orderRequests, userId, locationId),
				HttpStatus.OK);
	}

	
	 //Get Orders using by userId
	@GetMapping("/getOrdersByUserId")
	public ResponseEntity<Object> getOrdersByUserId(@RequestParam Long userId)throws Exception{		
		return new ResponseEntity<Object>(orderService.getOrdersByUserId(userId),HttpStatus.OK);
		
	}

	 //Get Orders and OrderDetails using order id
	@GetMapping("/getOrderAndOrderDetailsById")
	public ResponseEntity<Object> getOrderAndOrderDetailsById(@RequestParam Long id)throws Exception{	 	
		return new ResponseEntity<Object>(orderService.getOrderAndOrderDetailsById(id),HttpStatus.OK);
		
	}
	
	@GetMapping("/getItemQty")
	public ResponseEntity<Object> getOrderedItemsWithQuantityForToday(@RequestParam Long locationId) {
		return new ResponseEntity<Object>(orderService.getOrderedItemsWithQuantityForToday(locationId), HttpStatus.OK);
	}

	@GetMapping("/today")
	public ResponseEntity<Object> getTodayOrders(@RequestParam Long locationId) {
		return new ResponseEntity<Object>(orderService.getTodayOrders(locationId), HttpStatus.OK);
	}

	@GetMapping("/dashboard")
	public ResponseEntity<Object> getOrderItemCount(@RequestParam Long locationId) {
		return new ResponseEntity<Object>(orderService.getOrderItemCount(locationId), HttpStatus.OK);
	}

	@PostMapping("/report")
	public ResponseEntity<Object> generateOrderDetailsExcelReport(
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
			@RequestParam(required = false) Long locationId) throws IOException {
		byte[] in = orderService.generateOrderDetailsExcelReport(date, locationId);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		String filename = "Order_Report.xlsx";
		headers.setContentDispositionFormData(filename, filename);
		headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
		return new ResponseEntity<>(in, headers, HttpStatus.OK);
	}
	
	@GetMapping("/id")
	public ResponseEntity<Object> getOrderWithOrderDetailsById(@RequestParam Long id) throws Exception {
		return new ResponseEntity<Object>(orderService.getOrderWithOrderDetailsById(id), HttpStatus.OK);
	}
	
	
	@PostMapping("/getTotalQuantityOrderDetailsExcel")
	public ResponseEntity<Object> getTotalQuantityOrderDetailsExcel(
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
			@RequestParam(required = false) Long locationId) throws IOException {
		byte[] in = orderService.getTotalQuantityOrderDetailsExcel(date, locationId);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		String filename = "Total_Order_Report.xlsx";
		headers.setContentDispositionFormData(filename, filename);
		headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
		return new ResponseEntity<>(in, headers, HttpStatus.OK);
	}
	
	
	@PostMapping("/updateOrderStatus")
	public ResponseEntity<Object> updateOrderStatus(@RequestBody OrderStatusDto orderStatusDto) throws Exception{		
		return new ResponseEntity<Object>(orderService.updateOrderStatus(orderStatusDto), HttpStatus.OK);
		
	}
	
	@PostMapping("/updateDeliveredStatus")
	public ResponseEntity<Object> updateDeliveredStatus(@RequestBody OrderStatusDto orderStatusDto) throws Exception{		
		return new ResponseEntity<Object>(orderService.updateDeliveredStatus(orderStatusDto), HttpStatus.OK);
		
	}
}
	
	 //Save Order details
		/*@PostMapping("/save")
		public ResponseEntity<Object> saveOrderAndOrderDetails(@RequestBody List<OrderRequest> orderRequests,
				@RequestParam Long userId, @RequestParam Long locationId, @RequestBody PaymentRequest paymentRequest) throws Exception {
			return new ResponseEntity<Object>(orderService.saveOrderWithOrderDetails(orderRequests, userId, locationId, paymentRequest),
					HttpStatus.OK);
		}
		
		/*@PostMapping("/save")
	    public ResponseEntity<Object> saveOrderAndOrderDetails(@RequestBody OrderSaveRequest orderSaveRequest) throws Exception {
	        // Extract the properties from the DTO
	        List<OrderRequest> orderRequests = orderSaveRequest.getOrderRequests();
	        Long userId = orderSaveRequest.getUserId();
	        Long locationId = orderSaveRequest.getLocationId();
	        PaymentRequest paymentRequest = orderSaveRequest.getPaymentRequest();

	        // Call your service method to save order and order details
	        return new ResponseEntity<Object>(
	            orderService.saveOrderWithOrderDetails(orderRequests, userId, locationId, paymentRequest),
	            HttpStatus.OK
	        );
	    }*/
	
	
	/*@PostMapping("/save")
	public ResponseEntity<Object> saveOrderAndOrderDetails(@RequestBody List<OrderRequest> orderRequests, PaymentRequest paymentRequest,
			@RequestParam Long userId, @RequestParam Long locationId) throws Exception {
		return new ResponseEntity<Object>(orderService.saveOrderWithOrderDetails(orderRequests,paymentRequest, userId, locationId),
				HttpStatus.OK);
	}*/


/*@PostMapping("/save")
public ResponseEntity<Object> saveOrderAndOrderDetails(@RequestBody OrderWithPaymentRequest orderWithPaymentRequest,@RequestParam Long userId, @RequestParam Long locationId) throws Exception {
    return new ResponseEntity<>(
        orderService.saveOrderWithOrderDetails(
            orderWithPaymentRequest.getOrderRequests(),
            orderWithPaymentRequest.getPaymentRequest(), userId, locationId
        ),
        HttpStatus.OK
    );
}*/
