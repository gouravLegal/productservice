package com.example.productservice.controllers;

import com.example.productservice.dtos.ProductNotFoundExceptionDto;
import com.example.productservice.exceptions.ProductNotFoundException;
import com.example.productservice.models.Product;
import com.example.productservice.services.ProductService;
import com.example.productservice.services.TokenService;
import org.hibernate.cache.spi.access.UnknownAccessTypeException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    ProductService productService;

    TokenService tokenService;

    public ProductController(@Qualifier("SelfProductService") ProductService productService, TokenService tokenService) {
        this.productService = productService;
        this.tokenService = tokenService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(
            //TODO commented the below code as we are now using OAuth to do the validation instead of manually validating the token
            //@RequestHeader("Token") String token,
            @PathVariable("id") Long id) throws ProductNotFoundException {
        Product product = productService.getProductById(id);
        ResponseEntity<Product> responseEntityProduct;

        //We cannot check for null product and return a detailed error message as part of the response as it needs to return a Product which does not have these details.
        //We therefore throw an exception in this case which gets handled by the filter (@ControllerAdvice) and changes the response type before sending the response to the client
        /*if(product == null) {
            responseEntityProduct = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            responseEntityProduct = new ResponseEntity<>(product, HttpStatus.OK);
        }*/

        //TODO commented the below code as we are now using OAuth to do the validation instead of manually validating the token
        /*if (!tokenService.validateToken(token)) {
            throw new UnknownAccessTypeException("User is not Authorized");
        }*/
        responseEntityProduct = new ResponseEntity<>(product, HttpStatus.OK);
        return responseEntityProduct;
    }

    @GetMapping()
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @PutMapping("/{id}")
    public Product replaceProduct(@PathVariable("id") Long id, @RequestBody Product product) {
        return productService.replaceProduct(id, product);
    }

    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return productService.createProduct(product);
    }

}
