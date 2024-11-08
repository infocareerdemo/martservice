package com.mart.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mart.entity.UserList;


public interface UserListRepository extends JpaRepository<UserList, Long>{


	List<UserList> findByFutureDateTimeLessThanEqual(LocalDateTime now);

    List<UserList> findByUpdatedCurrentDateTimeBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);

}
