package com.example.productservice.services;

import com.example.productservice.exceptions.ProductNotFoundException;
import com.example.productservice.models.Category;
import com.example.productservice.models.Product;
import com.example.productservice.projections.ProductTitleAndDescription;
import com.example.productservice.repos.CategoryRepo;
import com.example.productservice.repos.ProductRepo;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("SelfProductService")
@Primary
public class SelfProductService implements ProductService {

    ProductRepo productRepository;
    CategoryRepo categoryRepository;

    public SelfProductService(ProductRepo productRepository, CategoryRepo categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Product getProductById(Long id) throws ProductNotFoundException {

        //Sample code on how to use HQL query. Currently, we are printing the output, but if needed we can convert data from projection into Product object and return Product
        ProductTitleAndDescription productTitleAndDescription = productRepository.getProductTitleAndDescriptionHQL(id);
        System.out.println("HQL Projection, Title:" + productTitleAndDescription.getTitle() + " Description:" + productTitleAndDescription.getDescription());

        //Sample code on how to use SQL query. Currently, we are printing the output, but if needed we can convert data from projection into Product object and return Product
        productTitleAndDescription = productRepository.getProductTitleAndDescriptionSQL(id);
        System.out.println("SQL Projection, Title:" + productTitleAndDescription.getTitle() + " Description:" + productTitleAndDescription.getDescription());

        //Return Product object using named query
        Optional<Product> productOptional = productRepository.findById(id);

        if (productOptional.isPresent()) {
            return productOptional.get();
        } else {
            throw new ProductNotFoundException(100L, "Product not found for id " + id);
        }
    }

    @Override
    public List<Product> getAllProducts() {
        return List.of();
    }

    @Override
    public Product replaceProduct(Long id, Product product) {
        return null;
    }

    @Override
    public Product createProduct(Product product) {
        Category category = product.getCategory();

        // If category id is null, it means the category object was not yet added to DB.
        // Hence, first add it to DB and then set the returned category object in Product. Finally save Product in DB
        if (category.getId() == null) {
            category = categoryRepository.save(category);
            product.setCategory(category);
        }
        return productRepository.save(product);
    }

    @Override
    public void createProductVoid(Product product) {
        //Creating a createProduct method which returns void in order to test the doNothing() and verify() methods for JUnit test
        System.out.println("In createProductVoid, Title:" + product.getTitle());
    }
}
