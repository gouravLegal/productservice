package com.example.productservice.services;


import com.example.productservice.dtos.FakeStoreProductDto;
import com.example.productservice.exceptions.ProductNotFoundException;
import com.example.productservice.models.Category;
import com.example.productservice.models.Product;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class FakeStoreProductService implements ProductService {

    RestTemplate restTemplate;

    public FakeStoreProductService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Product getProductById(Long id) throws ProductNotFoundException {
        FakeStoreProductDto fakeStoreProductDto = restTemplate.getForObject("https://fakestoreapi.com/products/" + id, FakeStoreProductDto.class);

        //If product with given id not found, then throw ProductNotFoundException
        if(fakeStoreProductDto == null) {
            throw new ProductNotFoundException(100L, "Product not found for id " + id);
        }
        return convertFakeStoreProductDtoToProduct(fakeStoreProductDto);
    }

    @Override
    public List<Product> getAllProducts() {
        FakeStoreProductDto[] fakeStoreProductDtoArrray = restTemplate.getForObject("https://fakestoreapi.com/products/", FakeStoreProductDto[].class);

        List<Product> productList = new ArrayList<>();

        for (FakeStoreProductDto fakeStoreProductDto : fakeStoreProductDtoArrray) {
            productList.add(convertFakeStoreProductDtoToProduct(fakeStoreProductDto));
        }

        return productList;
    }

    @Override
    public Product replaceProduct(Long id, Product product) {
        //Create FakeStoreDTO object using the id and Product object
        FakeStoreProductDto fakeStoreProductDto = new FakeStoreProductDto();
        fakeStoreProductDto.setId(id);
        fakeStoreProductDto.setTitle(product.getTitle());
        fakeStoreProductDto.setDescription(product.getDescription());
        fakeStoreProductDto.setPrice(product.getPrice());
        fakeStoreProductDto.setCategory(product.getCategory().getTitle());

        // Once the product is updated, our requirement is to return the updated values to the client
        // While the FakeStore 3rd party API returns the updated object back in response, the RestTemplate doesn't provide a PUT method which returns the response back. Return type of RestTemplate put method is void
        // Hence if we were to use the default RestTemplate put method we would have to make 2 network calls, one for update (PUT) and other for Getting the updated object
        // This would degrade the performance, and to optimize, we copy the implementation from put method and refactor it to send the request and read response from it back

        //restTemplate.put("https://fakestoreapi.com/products/" + id, fakeStoreProductDto); $$$$$$$This statement would have just updated the product and then we would have had to call the getProduct to return the updated value
        //To avoid the multiple network calls, we have replaced the code within the restTemplate put method (copied in next couple of commented lines) with the code below
        //RequestCallback requestCallback = this.httpEntityCallback(request);
        //this.execute(url, HttpMethod.PUT, requestCallback, (ResponseExtractor)null, (Object[])uriVariables);


        RequestCallback requestCallback = restTemplate.httpEntityCallback(fakeStoreProductDto, FakeStoreProductDto.class);

        ResponseExtractor<ResponseEntity<FakeStoreProductDto>> responseExtractor = restTemplate.responseEntityExtractor(FakeStoreProductDto.class);
        FakeStoreProductDto fakeStoreProductDtoResponse =
                restTemplate.execute("https://fakestoreapi.com/products/" + id, HttpMethod.PUT, requestCallback, responseExtractor).getBody();

        return convertFakeStoreProductDtoToProduct(fakeStoreProductDtoResponse);
    }

    @Override
    public Product createProduct(Product product) {
        return null;
    }

    @Override
    public void createProductVoid(Product product) {
        //Creating a createProduct method which returns void in order to test the doNothing() and verify() methods for JUnit test
        System.out.println("In createProductVoid, Title:" + product.getTitle());
    }

    public Product convertFakeStoreProductDtoToProduct(FakeStoreProductDto fakeStoreProductDto) {
        if (fakeStoreProductDto == null) {
            return null;
        }

        Product product = new Product();
        product.setId(fakeStoreProductDto.getId());
        product.setTitle(fakeStoreProductDto.getTitle());
        product.setDescription(fakeStoreProductDto.getDescription());
        product.setPrice(fakeStoreProductDto.getPrice());


        Category category = new Category();
        category.setTitle(fakeStoreProductDto.getCategory());
        product.setCategory(category);

        return product;

    }
}
