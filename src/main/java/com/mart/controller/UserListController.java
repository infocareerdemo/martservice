package com.mart.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mart.entity.UserList;
import com.mart.service.UserListService;

@RestController
@RequestMapping("/api/v1/userlist")
public class UserListController {
	
	
	
	private final UserListExcelParser userListExcelParser;
    private final UserListService userListService;

    public UserListController(UserListExcelParser userListExcelParser, UserListService userListService) {
        this.userListExcelParser = userListExcelParser;
        this.userListService = userListService;
    }

    @PostMapping("/upload")
    public String uploadUserList(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return "No file uploaded";
        }

        // Parse the Excel file
        List<UserList> userList = userListExcelParser.parseExcelFile(file.getInputStream());

        // Save the parsed data to the database
        userListService.saveAll(userList);

        return "File uploaded and processed successfully";
    }
	    

	
}
