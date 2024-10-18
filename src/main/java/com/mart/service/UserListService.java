package com.mart.service;

import java.io.InputStream;

import com.mart.entity.UserDetail;
import com.mart.entity.UserList;
import com.mart.repository.UserDetailRepository;
import com.mart.repository.UserListRepository;

import jakarta.transaction.Transactional;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class UserListService {
	
	 @Autowired
	    private UserListRepository userListRepository;
	 
	  @Autowired
	    private UserDetailRepository userDetailRepository;

	    public UserListService(UserListRepository userListRepository) {
	        this.userListRepository = userListRepository;
	    }

	    @Transactional
	    public void saveAll(List<UserList> userList) {
	        userListRepository.saveAll(userList);
	    }
	    

	    public void processExcel(MultipartFile file) throws Exception {
	        InputStream inputStream = file.getInputStream();
	        List<UserList> userList = extractUserDataFromExcel(inputStream);

	        // Save each user to the database
	        for (UserList user : userList) {
	            userListRepository.save(user);
	        }
	    }

	    private List<UserList> extractUserDataFromExcel(InputStream inputStream) throws Exception {
	        List<UserList> userList = new ArrayList<>();
	        Workbook workbook = new XSSFWorkbook(inputStream);
	        Sheet sheet = workbook.getSheetAt(0);
	        Iterator<Row> rows = sheet.iterator();

	        // Skip the header
	        if (rows.hasNext()) {
	            rows.next();
	        }

	        while (rows.hasNext()) {
	            Row row = rows.next();
	            UserList user = new UserList();

	            user.setEmployeeCode(row.getCell(1).getStringCellValue());
	            user.setPhone((long) row.getCell(2).getNumericCellValue());
	            user.setName(row.getCell(4).getStringCellValue());
	            user.setCurrentDate(row.getCell(5).getLocalDateTimeCellValue().toLocalDate());
	            user.setFutureDate(row.getCell(6).getLocalDateTimeCellValue().toLocalDate());

	            userList.add(user);
	        }

	        workbook.close();
	        return userList;
	    }
	    

	    


}
