package com.mart.entity;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
@Entity
@Table(name = "products") 
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "product_id") 
	private Long productId;

	@NotBlank(message = "Name is mandatory")
	@Size(max = 100, message = "Name must be less than 100 characters")
	@Column(name = "product_name", nullable = false, length = 100)
	private String productName;

	@Size(max = 255, message = "Description must be less than 255 characters")
	@Column(name = "product_description", length = 255, nullable = false)
	private String productDescription;

	@NotNull(message = "Price is mandatory")
	@Positive(message = "Price must be positive")
	@Column(name = "product_price", nullable = false)
	private double productPrice;
	
	@Column(name = "product_gst", nullable = false)
	private int productGST;

	@NotNull(message = "Active status is mandatory")
	@Column(name = "product_active", nullable = false)
	private boolean productActive;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "prodcut_location", referencedColumnName = "location_id", nullable = false)
	private Location location;
	
	@Column(name = "product_image")
	private byte[] productImage;

	@Column(name = "product_updated_date_time", nullable = false)
	private LocalDateTime updatedDate;
	
	@Column(name = "product_updated_by", nullable = false)
	private Long productUpdatedBy;

    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "product_category",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @JsonManagedReference
    private Set<Category> categories;
    
    
    
    
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(productId, product.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }
    
    
    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", productDescription='" + productDescription + '\'' +
                ", productPrice=" + productPrice +
                ", productGST=" + productGST +
                ", productActive=" + productActive +
                ", location=" + location + 
                ", updatedDate=" + updatedDate +
                ", productUpdatedBy=" + productUpdatedBy +
                '}';
    }

}
