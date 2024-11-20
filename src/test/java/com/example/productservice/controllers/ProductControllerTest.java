package com.example.productservice.controllers;

import com.example.productservice.exceptions.ProductNotFoundException;
import com.example.productservice.models.Product;
import com.example.productservice.services.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ProductControllerTest {

    @Autowired
    private ProductController productController;

    @MockBean(name ="SelfProductService")
    private ProductService productService;

    @Test
    void whenProductIsRetrievedUsingIdThenCorrectProductExpected() throws ProductNotFoundException {
        //Arrange
        Product product = new Product();
        product.setId(1L);
        product.setTitle("Test Product");

        //Specify the rules when the mock object should be used to return the response instead of making a call to the actual object
        when(productService.getProductById(1L)).thenReturn(product);
        //The above rule specifies that whenever we call getProductById() method of productService by passing 1 as the id, then return the mock object "product" created above
        //This will bypass the call to the actual productService and returns the hardcoded product object.
        //If in case the call to getProductById does not match any of the rules provided above, then it will return null object for product.
        //For e.g. when we call productService.getProductById(2) where rule for id 2 is not defined above it will return null

        //Act
        //We are invoking the productController method and checking if the object returned by service class is properly getting returned by the controller
        //Here the controller would get called and internal call to productService will get replaced by the Mock object injected here (which will return the hardcoded product object)
        //As controller method is returning ResponseEntity<Product>, we need to execute getBody() method to retrieve the Product object in the Response Entity
        Product p = productController.getProductById(1L).getBody();

        //Assert
        assertEquals(product.getId(), p.getId());
        assertEquals(product.getTitle(), p.getTitle());
    }

    @Test
    void getProductByIdThrowsException() throws ProductNotFoundException {
        //Arrange
        //Nothing required in the Arrange section in this case

        //Define the rule than an exception needs to be thrown when we call getProductById with id value as 1
        when(productService.getProductById(1L)).thenThrow(ProductNotFoundException.class);

        //Act and Assert
        assertThrows(ProductNotFoundException.class, () -> productController.getProductById(1L));
    }

    @Test
    void createProduct() {
        //Arrange
        Product product = new Product();
        product.setId(1L);
        product.setTitle("Test Product");

        //When the Mock object is expected to do nothing on particular call, we define it using the doNothing() rule
        doNothing().when(productService).createProductVoid(product);

        // Act
        //Product p = productController.getProductById(1L).getBody();

        //Act & Assert
        //This validates if the call to the productService (mock) createProduct() method actually occurred or if there were any errors invoking the method
        verify(productService).createProduct(product);

        //verify(productService, atLeastOnce()).createProduct(product); //Check if the createProduct() method gets invoked at least once
        //verify(productService, times(5)).createProduct(product); //Check if the createProduct() method gets invoked 5 times
    }

}