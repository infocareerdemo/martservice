package com.mart.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.mart.config.GeneralConstant;
import com.mart.dto.ItemCount;
import com.mart.dto.OrderDashboard;
import com.mart.dto.OrderRequest;
import com.mart.dto.OrderStatusDto;
import com.mart.dto.OrderSummary;
import com.mart.entity.Location;
import com.mart.entity.Orders;
import com.mart.entity.OrderDetails;
import com.mart.entity.Product;
import com.mart.entity.UserDetail;
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
	


	public List<Orders> getOrdersByUserId(Long userId) throws Exception {
	    Optional<UserDetail> user = userDetailRepository.findById(userId);
	    
	    if (user.isPresent()) {
	        // Sort by orderDate in descending order
	        List<Orders> orders = orderRepository.findByUserDetailUserId(userId, Sort.by(Sort.Direction.DESC, "orderedDateTime"));
	        
	        if (!CollectionUtils.isEmpty(orders)) {
	            return orders;
	        }
	    } else {
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
	
	public Object saveOrderWithOrderDetails(List<OrderRequest> orderRequests,Long userId, Long locationId) throws ApplicationException {
				    
		OrderSummary orderSummary = new OrderSummary();	
	    	    
		if (!CollectionUtils.isEmpty(orderRequests)) {
			Orders orders = new Orders();
			Optional<UserDetail> userLogin = userDetailRepository.findById(userId);
			if (userLogin.isPresent()) {
	            orders.setUserDetail(userLogin.get());
				orders.setAddress(userLogin.get().getAddress());
				Optional<Location> location = locationRepository.findById(locationId);
				orders.setLocation(location.get());
				//orders.setGst(5.0);
				orders.setPaymentStatus(GeneralConstant.PAY_PENDING.toString());
				orders.setOrderedDateTime(LocalDateTime.now());
				
	            
				orderRepository.save(orders);
				double totAmt = 0.0;
				List<OrderDetails> orderDetailsRes = new ArrayList<>();
				for (OrderRequest orderRequest : orderRequests) {
					OrderDetails orderDetails = new OrderDetails();
					Optional<Product> products = productRepository.findById(orderRequest.getProductId());
					if (products.isPresent()) {
						orderDetails.setProducts(products.get());
						orderDetails.setOrders(orders);
						orderDetails.setQuantity(orderRequest.getQuantity());
						orderDetails.setUnitPrice(products.get().getProductPrice());

						double total = orderRequest.getQuantity() * products.get().getProductPrice();

						orderDetails.setTotalPrice(Double.parseDouble(String.format("%.2f", total)));
						orderDetails.setOrderDateTime(LocalDateTime.now());

						orderDetailsRepository.save(orderDetails);
						totAmt += total;
						orderDetailsRes.add(orderDetails);
					}
				}

				orders.setOrderAmount(Double.parseDouble(String.format("%.2f", totAmt)));
                Double gstPercentage = 0.0;
	            double gst = (gstPercentage / 100.0) * totAmt;
				orders.setGstAmount(Double.parseDouble(String.format("%.2f", gst)));
				double totalAmountIncludingGst = totAmt + gst;
				
				orders.setTotalAmount(Double.parseDouble(String.format("%.2f", totalAmountIncludingGst)));
				
				orderRepository.save(orders);
				orderSummary.setOrderDetails(orderDetailsRes);
				orderSummary.setOrders(orders);
				return orderSummary;
			} else {
				throw new ApplicationException(HttpStatus.UNAUTHORIZED, 1001, LocalDateTime.now(), "Invalid user");
			}
		} else {
			throw new ApplicationException(HttpStatus.BAD_REQUEST, 1001, LocalDateTime.now(), "No data present");
		}
	}
	
	
	
	public Map<String, Object> getOrderedItemsWithQuantityForToday(Long locationId) {
		Map<String, Object> itemWithQty = new HashMap<>();
		LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
		LocalDateTime endOfDay = startOfDay.plusDays(1);
		List<Orders> orders = orderRepository.findByOrderedDateTimeBetweenAndLocationLocationId(startOfDay, endOfDay,
				locationId);
		if (!CollectionUtils.isEmpty(orders)) {
			orders = orders.stream().filter(o -> o.getPaymentStatus().equals(GeneralConstant.PAY_SUCCESS.toString()))
					.collect(Collectors.toList());
			if (!CollectionUtils.isEmpty(orders)) {
				for (Orders order : orders) {
					List<OrderDetails> orderDetails = orderDetailsRepository.findByOrdersId(order.getId());
					if (!CollectionUtils.isEmpty(orderDetails)) {
						for (OrderDetails orderDetail : orderDetails) {
							if (itemWithQty.get(orderDetail.getProducts().getProductName()) != null) {
								Long qty = (Long) itemWithQty.get(orderDetail.getProducts().getProductName());
								qty += orderDetail.getQuantity();

								itemWithQty.put(orderDetail.getProducts().getProductName(), qty);
							} else {
								itemWithQty.put(orderDetail.getProducts().getProductName(), orderDetail.getQuantity());
							}
						}
					}
				}
			}
		}
		return itemWithQty;
	}
	
	

	public List<Orders> getTodayOrders(Long locationId) {
		LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
		LocalDateTime endOfDay = startOfDay.plusDays(1);
		List<Orders> orders = orderRepository.findByOrderedDateTimeBetweenAndLocationLocationId(startOfDay, endOfDay,
				locationId);
		if (!CollectionUtils.isEmpty(orders)) {
			orders = orders.stream().filter(o -> o.getPaymentStatus().equals(GeneralConstant.PAY_SUCCESS.toString()))
					.collect(Collectors.toList());
			if (!CollectionUtils.isEmpty(orders)) {
				return orders;
			}
		}
		return null;
	}
	
	
	public OrderDashboard getOrderItemCount(Long locationId) {
		OrderDashboard orderDashboard = new OrderDashboard();
		List<Orders> locationOrders = null;
		if (locationId != null) {
			locationOrders = getTodayOrders(locationId);
		} else {
			locationOrders = getTodayOrders(locationRepository.findAll().get(0).getLocationId());
		}

		LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
		LocalDateTime endOfDay = startOfDay.plusDays(1);
		List<Orders> todayOrders = orderRepository.findByOrderedDateTimeBetween(startOfDay, endOfDay);
		if (!CollectionUtils.isEmpty(todayOrders)) {
			todayOrders = todayOrders.stream()
					.filter(o -> o.getPaymentStatus().equals(GeneralConstant.PAY_SUCCESS.toString()))
					.collect(Collectors.toList());
		}
		orderDashboard.setTotalOrdersCount((long) todayOrders.size());

		List<ItemCount> itemCounts = new ArrayList<>();
		List<Product> products = productRepository.findAll();
		if (!CollectionUtils.isEmpty(products)) {
			if (!CollectionUtils.isEmpty(todayOrders)) {
				todayOrders = todayOrders.stream()
						.filter(o -> o.getPaymentStatus().equals(GeneralConstant.PAY_SUCCESS.toString()))
						.collect(Collectors.toList());
				if (!CollectionUtils.isEmpty(todayOrders)) {
					for (Product product : products) {
						ItemCount itemCount = new ItemCount();
						long count = 0;
						for (Orders order : todayOrders) {
							List<OrderDetails> orderDetails = orderDetailsRepository.findByOrdersId(order.getId());
							for (OrderDetails orderDetail : orderDetails) {
								if (orderDetail.getProducts().getProductId() == product.getProductId()) {
									count++;
								}
							}
						}
						if (count > 0) {
							itemCount.setProducts(product);
							itemCount.setCount(count);
							itemCounts.add(itemCount);
						}
					}
				}
			}
		}

		orderDashboard.setTotalOrderDetails(itemCounts);

		Map<String, Long> itemWithQty = new HashMap<>();
		if (!CollectionUtils.isEmpty(locationOrders)) {
			locationOrders = locationOrders.stream()
					.filter(o -> o.getPaymentStatus().equals(GeneralConstant.PAY_SUCCESS.toString()))
					.collect(Collectors.toList());
			if (!CollectionUtils.isEmpty(locationOrders)) {
				for (Orders order : locationOrders) {
					List<OrderDetails> orderDetails = orderDetailsRepository.findByOrdersId(order.getId());
					if (!CollectionUtils.isEmpty(orderDetails)) {
						for (OrderDetails orderDetail : orderDetails) {
							if (itemWithQty.get(orderDetail.getProducts().getProductName()) != null) {
								Long qty = (Long) itemWithQty.get(orderDetail.getProducts().getProductName());
								qty += 1;

								itemWithQty.put(orderDetail.getProducts().getProductName(), qty);
							} else {
								itemWithQty.put(orderDetail.getProducts().getProductName(), (long) 1);
							}
						}
					}
				}
			}
		}

		orderDashboard.setLocationOrderDetails(itemWithQty);

		return orderDashboard;
	}
	
	
	public byte[] generateOrderDetailsExcelReport(LocalDate date, Long locationId) throws IOException {
		LocalDateTime startDate = null;
		LocalDateTime endDate = null;
		if (date != null) {
			startDate = date.atStartOfDay();
			endDate = startDate.plusDays(1);
		} else {
			startDate = LocalDate.now().atStartOfDay();
			endDate = startDate.plusDays(1);
		}
		List<Orders> orders = null;
		if (locationId != null) {
			orders = orderRepository.findByOrderedDateTimeBetweenAndLocationLocationId(startDate, endDate, locationId);
			if (!CollectionUtils.isEmpty(orders)) {
				orders = orders.stream()
						.filter(o -> o.getPaymentStatus().equals(GeneralConstant.PAY_SUCCESS.toString()))
						.collect(Collectors.toList());
			}
		} else {
			orders = orderRepository.findByOrderedDateTimeBetween(startDate, endDate);
			orders = orders.stream().filter(o -> o.getPaymentStatus().equals(GeneralConstant.PAY_SUCCESS.toString()))
					.sorted(Comparator.comparing(o -> ((Orders) o).getLocation().getLocationId()))
					.collect(Collectors.toList());
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-YYYY");
		if (!CollectionUtils.isEmpty(orders)) {
			XSSFWorkbook wb = new XSSFWorkbook();
			XSSFSheet sheet = wb.createSheet("Sheet1");
			XSSFRow row = sheet.createRow(0);

			Font headerFont = wb.createFont();
			headerFont.setBold(true);
			// Create a cell style with the bold font
			CellStyle headerCellStyle = wb.createCellStyle();
			headerCellStyle.setFont(headerFont);
			
			CellStyle centerStyle = wb.createCellStyle();
//	        centerStyle.setAlignment(HorizontalAlignment.CENTER);
	        centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
	        
			XSSFCell cell;

			cell = row.createCell(0);
			cell.setCellValue("Order Details For " + date.format(formatter));
			cell.setCellStyle(headerCellStyle);

			row = sheet.createRow(2);
			cell = row.createCell(0);
			cell.setCellValue("Sl.No");
			cell.setCellStyle(headerCellStyle);
			cell = row.createCell(1);
			cell.setCellValue("Location");
			cell.setCellStyle(headerCellStyle);
			cell = row.createCell(2);
			cell.setCellValue("Name");
			cell.setCellStyle(headerCellStyle);
			cell = row.createCell(3);
			cell.setCellValue("Phone");
			cell.setCellStyle(headerCellStyle);
			cell = row.createCell(4);
			cell.setCellValue("Order Id");
			cell.setCellStyle(headerCellStyle);
			cell = row.createCell(5);
			cell.setCellValue("Product Name");
			cell.setCellStyle(headerCellStyle);
			cell = row.createCell(6);
			cell.setCellValue("Quantity");
			cell.setCellStyle(headerCellStyle);

			int j = 3;
			int idx = 1;
			int r = 0;
			for (Orders order : orders) {
				r = j;
				row = sheet.createRow(j);
				cell = row.createCell(0);
				cell.setCellValue(idx);
				cell.setCellStyle(centerStyle);
				cell = row.createCell(1);
				 String locationInfo = order.getLocation().getLocationName() 
                         + " (" 
                         + order.getLocation().getCompanyName() 
                         + ")";
                cell.setCellValue(locationInfo);
				//cell.setCellValue(order.getLocation().getLocationName());
				cell.setCellStyle(centerStyle);
				cell = row.createCell(2);
				cell.setCellValue(order.getUserDetail().getName());
				cell.setCellStyle(centerStyle);
				cell = row.createCell(3);
				cell.setCellValue(order.getUserDetail().getPhone());
				cell.setCellStyle(centerStyle);
				cell = row.createCell(4);
				cell.setCellValue(order.getOrderId());
				cell.setCellStyle(centerStyle);
				List<OrderDetails> orderDetails = orderDetailsRepository.findByOrdersId(order.getId());
				boolean flag = false;
				for (OrderDetails orderDetail : orderDetails) {
					if (flag == false) {
						cell = row.createCell(5);
						cell.setCellValue(orderDetail.getProducts().getProductName());
						cell.setCellStyle(centerStyle);
						cell = row.createCell(6);
						cell.setCellValue(orderDetail.getQuantity());
						cell.setCellStyle(centerStyle);
						flag = true;
						j++;
					} else {
						row = sheet.createRow(j);
						cell = row.createCell(5);
						cell.setCellValue(orderDetail.getProducts().getProductName());
						cell.setCellStyle(centerStyle);
						cell = row.createCell(6);
						cell.setCellValue(orderDetail.getQuantity());
						cell.setCellStyle(centerStyle);
						j++;
					}
				}
				
				
				if (orderDetails.size() > 1) {
					sheet.addMergedRegion(new CellRangeAddress(r, j - 1, 0, 0));
					sheet.addMergedRegion(new CellRangeAddress(r, j - 1, 1, 1));
					sheet.addMergedRegion(new CellRangeAddress(r, j - 1, 2, 2));
					sheet.addMergedRegion(new CellRangeAddress(r, j - 1, 3, 3));
					sheet.addMergedRegion(new CellRangeAddress(r, j - 1, 4, 4));
				}
				idx++;
			}
			ByteArrayOutputStream fileOut = new ByteArrayOutputStream();
			wb.write(fileOut);
			fileOut.close();
			byte bsg[] = fileOut.toByteArray();
			wb.close();
			return bsg;
		}
		return null;
	}

	
	public OrderSummary getOrderWithOrderDetailsById(Long id) throws ApplicationException {
		OrderSummary orderSummary = new OrderSummary();
		Optional<Orders> orders = orderRepository.findById(id);
		if (orders.isPresent()) {
			List<OrderDetails> orderDetails = orderDetailsRepository.findByOrdersId(id);
			if (!CollectionUtils.isEmpty(orderDetails)) {
				orderSummary.setOrders(orders.get());
				orderSummary.setOrderDetails(orderDetails);
			}
		} else {
			throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "No order found");
		}
		return orderSummary;
	}
	
	
	public byte[] getTotalQuantityOrderDetailsExcel(LocalDate date, Long locationId) throws IOException {
	    LocalDateTime startDate = null;
	    LocalDateTime endDate = null;

	    if (date != null) {
	        startDate = date.atStartOfDay();
	        endDate = startDate.plusDays(1);
	    } else {
	        startDate = LocalDate.now().atStartOfDay();
	        endDate = startDate.plusDays(1);
	    }

	    List<Orders> orders = locationId != null ?
	            orderRepository.findByOrderedDateTimeBetweenAndLocationLocationId(startDate, endDate, locationId) :
	            orderRepository.findByOrderedDateTimeBetween(startDate, endDate);

	    if (!CollectionUtils.isEmpty(orders)) {
	        orders = orders.stream()
	                .filter(o -> o.getPaymentStatus().equals(GeneralConstant.PAY_SUCCESS.toString()))
	                .sorted(Comparator.comparing(o -> o.getLocation().getLocationId()))
	                .collect(Collectors.toList());
	    }

	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-YYYY");
	    if (!CollectionUtils.isEmpty(orders)) {
	        XSSFWorkbook wb = new XSSFWorkbook();
	        XSSFSheet sheet = wb.createSheet("Sheet1");
	        XSSFRow row = sheet.createRow(0);

	        Font headerFont = wb.createFont();
	        headerFont.setBold(true);
	        CellStyle headerCellStyle = wb.createCellStyle();
	        headerCellStyle.setFont(headerFont);

	        CellStyle centerStyle = wb.createCellStyle();
	        centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

	        XSSFCell cell;

	        // Header Row
	        cell = row.createCell(0);
	        cell.setCellValue("Order Details For " + date.format(formatter));
	        cell.setCellStyle(headerCellStyle);

	        // Table Headers
	        row = sheet.createRow(2);
	        cell = row.createCell(0);
	        cell.setCellValue("Sl.No");
	        cell.setCellStyle(headerCellStyle);
	        cell = row.createCell(1);
	        cell.setCellValue("Location");
	        cell.setCellStyle(headerCellStyle);
	        cell = row.createCell(2);
	        cell.setCellValue("Product Name");
	        cell.setCellStyle(headerCellStyle);
	        cell = row.createCell(3);
	        cell.setCellValue("Quantity");
	        cell.setCellStyle(headerCellStyle);

	        // Map to hold aggregated quantities by product and location
	        Map<String, Map<String, Long>> locationProductQuantityMap = new HashMap<>();

	        for (Orders order : orders) {
	            String locationName = order.getLocation().getLocationName();
	            String companyName = order.getLocation().getCompanyName();  // Get company name
	            List<OrderDetails> orderDetails = orderDetailsRepository.findByOrdersId(order.getId());

	            // Concatenate location name and company name
	            String locationInfo = locationName + " (" + companyName + ")";

	            Map<String, Long> productQuantityMap = locationProductQuantityMap
	                    .computeIfAbsent(locationInfo, k -> new HashMap<>());

	            for (OrderDetails orderDetail : orderDetails) {
	                String productName = orderDetail.getProducts().getProductName();
	                productQuantityMap.put(productName,
	                        productQuantityMap.getOrDefault(productName, 0L) + orderDetail.getQuantity());
	            }
	        }

	        int j = 3;  // Start row for data
	        int idx = 1;  // Initialize serial number

	        // Write aggregated data to the Excel sheet
	        for (Map.Entry<String, Map<String, Long>> locationEntry : locationProductQuantityMap.entrySet()) {
	            String locationInfo = locationEntry.getKey();
	            Map<String, Long> productQuantityMap = locationEntry.getValue();

	            // Add serial number for each unique location
	            row = sheet.createRow(j);
	            cell = row.createCell(0);
	            cell.setCellValue(idx++);
	            cell.setCellStyle(centerStyle);

	            // Add location and company name cell
	            cell = row.createCell(1);
	            cell.setCellValue(locationInfo);
	            cell.setCellStyle(centerStyle);

	            // Add aggregated product quantities
	            boolean isFirstProduct = true;
	            for (Map.Entry<String, Long> entry : productQuantityMap.entrySet()) {
	                if (!isFirstProduct) {
	                    row = sheet.createRow(j);
	                }
	                cell = row.createCell(2);
	                cell.setCellValue(entry.getKey());
	                cell.setCellStyle(centerStyle);
	                cell = row.createCell(3);
	                // Convert the Long value to int
	                cell.setCellValue(entry.getValue().intValue());
	                cell.setCellStyle(centerStyle);

	                isFirstProduct = false;
	                j++;
	            }
	        }

	        ByteArrayOutputStream fileOut = new ByteArrayOutputStream();
	        wb.write(fileOut);
	        fileOut.close();
	        byte[] bsg = fileOut.toByteArray();
	        wb.close();
	        return bsg;
	    }

	    return null;
	}
		
	

	public String updateOrderStatus(OrderStatusDto orderStatusDto) throws ApplicationException{
		if(orderStatusDto !=null) {
		   Orders order =	orderRepository.findByOrderId(orderStatusDto.getOrderId());
			   if(order !=null) {
				
				   order.setCashOrderStatus(orderStatusDto.isCashOrderStatus());
				   orderRepository.save(order);
				   return "Cash Order Status Updated!";

			   }else {
		           throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "Order Not Found");

			   }
		}else {
	           throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "Data Not Found");

		}
	}


	public String updateDeliveredStatus(OrderStatusDto orderStatusDto) throws ApplicationException{
		if(orderStatusDto !=null) {
			   Orders order =	orderRepository.findByOrderId(orderStatusDto.getOrderId());
				   if(order !=null) {
					
					   order.setDeliveredStatus(orderStatusDto.isDeliveredStatus());
					   orderRepository.save(order);
					   return "Delivered Status Updated!";

				   }else {
			           throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "Order Not Found");

				   }
			}else {
		           throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "Data Not Found");
			}
	}

	
    public byte[] getDateWiseUserOrderDetailsExcel(LocalDate date) throws IOException {
        String paymentStatus = "PAY_SUCCESS"; 

        LocalDateTime startDateTime = date.atStartOfDay();
        LocalDateTime endDateTime = date.plusDays(1).atStartOfDay();

        List<Orders> orders = orderRepository.findByOrderedDateTimeAndPaymentStatus(startDateTime, endDateTime, paymentStatus);
        
        return generateExcelReport(orders);
    }

    public byte[] getDateWiseUserOrderDetailsExcel(LocalDate fromDate, LocalDate toDate) throws IOException {
        String paymentStatus = "PAY_SUCCESS"; 

        LocalDateTime startDateTime = fromDate.atStartOfDay();
        LocalDateTime endDateTime = toDate.plusDays(1).atStartOfDay();


        List<Orders> orders = orderRepository.findByOrderedDateTimeAndPaymentStatus(startDateTime, endDateTime, paymentStatus);
        
        return generateExcelReport(orders);
    }

    public byte[] generateExcelReport(List<Orders> orders) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("User Order Report");

        Row headerRow = sheet.createRow(0);
        String[] headers = {"S.no", "Order Date", "Employee Code","Name", "Order ID",  "Cash", "Wallet", "Razorpay", "Total Amount","Product Name", "Quantity", "Price"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(createHeaderCellStyle(workbook));
        }

        int rowNum = 1;
        int serialNumber = 1;

        for (Orders order : orders) {
            List<OrderDetails> orderDetails = orderDetailsRepository.findByOrdersId(order.getId());

            boolean isFirstProduct = true;

            for (OrderDetails orderDetail : orderDetails) {
                Row row = sheet.createRow(rowNum++);

                if (isFirstProduct) {
                    row.createCell(0).setCellValue(serialNumber++);

                    Cell dateCell = row.createCell(1);
                    dateCell.setCellValue(order.getOrderedDateTime());
                    dateCell.setCellStyle(createDateCellStyle(workbook)); 

                    row.createCell(2).setCellValue(order.getUserDetail().getEmployeeCode());
                    row.createCell(3).setCellValue(order.getUserDetail().getName());
                    row.createCell(4).setCellValue(order.getOrderId());
                    row.createCell(5).setCellValue(order.getTotalAmount());
                    row.createCell(6).setCellValue(order.getCashAmount());
                    row.createCell(7).setCellValue(order.getWalletAmount());
                    row.createCell(8).setCellValue(order.getRazorpayAmount());

                    isFirstProduct = false; 
                }

                row.createCell(9).setCellValue(orderDetail.getProducts().getProductName()); 
                row.createCell(10).setCellValue(orderDetail.getQuantity());                  
                row.createCell(11).setCellValue(orderDetail.getUnitPrice());                
            }
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }


    private CellStyle createHeaderCellStyle(Workbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);
        return headerStyle;
    }

    private CellStyle createDateCellStyle(Workbook workbook) {
        CellStyle dateStyle = workbook.createCellStyle();
        CreationHelper creationHelper = workbook.getCreationHelper();
        dateStyle.setDataFormat(creationHelper.createDataFormat().getFormat("yyyy-mm-dd")); 
        return dateStyle;
    }


    public List<Orders> getWalletDetailsById(Long userId) throws ApplicationException {
        if (userId == null) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "Id Not Found");
        }

        List<Orders> result = new ArrayList<>();

        // Fetch successful orders with wallet amount only
        List<Orders> walletOnlyOrders = orderRepository.findSuccessfulOrdersWithWalletOnly(userId);
        result.addAll(walletOnlyOrders);

        // Fetch successful orders with both wallet and Razorpay amount
        List<Orders> walletAndRazorpayOrders = orderRepository.findSuccessfulOrdersWithWalletAndRazorpay(userId);
        result.addAll(walletAndRazorpayOrders);

        return result;
    }


}
	
	

