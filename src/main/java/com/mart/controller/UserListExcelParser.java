package com.mart.controller;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import com.mart.entity.UserList;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;


@Component
public class UserListExcelParser {

    public List<UserList> parseExcelFile(InputStream inputStream) {
        List<UserList> userList = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) { // Skip header row
                Row row = sheet.getRow(rowIndex);

                if (row == null) continue;

                UserList user = new UserList();

                // Extract EmployeeCode
                user.setEmployeeCode(getStringCellValue(row.getCell(1)));  // Column 1: EmployeeCode

                // Extract Phone (as Long)
                user.setPhone(getLongCellValue(row.getCell(2)));  // Column 2: Phone

                // Extract Name
                user.setName(getStringCellValue(row.getCell(3)));  // Column 3: Name

                // Extract Email
                user.setEmail(getStringCellValue(row.getCell(4)));  // Column 4: Email

                // Extract Wallet
                user.setWalletAmount(getDoubleCellValue(row.getCell(5)));  // Column 5: Wallet

                // Extract CurrentDate
                user.setCurrentDate(getDateCellValue(row.getCell(6)));  // Column 6: CurrentDate

                // Extract FutureDate
                user.setFutureDate(getDateCellValue(row.getCell(7)));  // Column 7: FutureDate

                user.setUpdatedDateTime(LocalDateTime.now());
                // Add to list
                userList.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userList;
    }

    // Utility function to get String value from a cell
    private String getStringCellValue(Cell cell) {
        if (cell == null) return "";
        if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf(cell.getNumericCellValue());
        } else if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        }
        return "";
    }

    // Utility function to get Long value from a cell (for phone numbers)
    private Long getLongCellValue(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC) {
            return (long) cell.getNumericCellValue();
        } else if (cell.getCellType() == CellType.STRING) {
            try {
                return Long.valueOf(cell.getStringCellValue());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    // Utility function to get Double value from a cell (for wallet amount)
    private double getDoubleCellValue(Cell cell) {
        if (cell == null) return 0.0;
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        } else if (cell.getCellType() == CellType.STRING) {
            try {
                return Double.valueOf(cell.getStringCellValue());
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
        return 0.0;
    }

    // Utility function to get Date value from a cell (for current and future dates)
    private LocalDate getDateCellValue(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getDateCellValue().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        } else if (cell.getCellType() == CellType.STRING) {
            return LocalDate.parse(cell.getStringCellValue());
        }
        return null;
    }
}
