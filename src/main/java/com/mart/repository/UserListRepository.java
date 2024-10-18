package com.mart.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mart.entity.UserList;


public interface UserListRepository extends JpaRepository<UserList, Long>{



}