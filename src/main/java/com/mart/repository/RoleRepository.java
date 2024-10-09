package com.mart.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mart.entity.Role;


public interface RoleRepository extends JpaRepository<Role, Long> {
	
	
	
	@Override
	Optional<Role> findById(Long id);
}
