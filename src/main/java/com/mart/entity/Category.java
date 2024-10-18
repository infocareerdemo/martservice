package com.mart.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "category")
public class Category {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "category_id")
	 private Long   categoryId;
	
	 
	@Column(name = "category_name",nullable = false)
	 private String categoryName;
	  
	  /* @ManyToMany(fetch = FetchType.LAZY)
	    @JoinTable(
	        name = "product_category",
	        joinColumns = @JoinColumn(name = "category_id"),
	        inverseJoinColumns = @JoinColumn(name = "product_id")
	    )
	    private Set<Product> products = new HashSet<>();*/
	   
	  

	    @ManyToMany(mappedBy = "categories", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	    private Set<Product> products = new HashSet<>();  
	   
}
