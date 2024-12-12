package com.example.productservice.repos;

import com.example.productservice.models.Product;
import com.example.productservice.projections.ProductTitleAndDescription;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {

    // Sample code for using HQL query. Here Product is the class. We cannot use Product object as its output as Product has lot many other fields but the query is
    // returning only few fields, so Hibernate will throw an exception. Hence, we create a Projection (as an interface containing get() methods only for returned fields)
    @Query("select p.title as title, p.description as description from Product p where p.id = :id")
    ProductTitleAndDescription getProductTitleAndDescriptionHQL(@Param("id") Long id);

    // Sample code for using SQL query. Here product is table name. We cannot use Product object as its output as Product has lot many other fields but the query is
    // returning only few fields, so Hibernate will throw an exception. Hence, we create a Projection (as an interface containing get() methods only for returned fields)
    @Query(value = "select title, description from product where id = :id", nativeQuery = true)
    ProductTitleAndDescription getProductTitleAndDescriptionSQL(@Param("id") Long id);

    List<Product> findByTitleContains(String keyword, Pageable pageable);

}
