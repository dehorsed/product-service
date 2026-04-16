package org.dehorsed.microservices.product;

import java.math.BigDecimal;

import org.dehorsed.microservices.product.dto.ProductRequest;
import org.dehorsed.microservices.product.model.Product;
import org.dehorsed.microservices.product.repository.ProductRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mongodb.MongoDBContainer;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import tools.jackson.databind.ObjectMapper;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductserviceApplicationTests {

    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:8.0");

    @LocalServerPort
    private Integer serverPort;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = serverPort;
        productRepository.deleteAll();
    }

    private ProductRequest createProductRequest() {
        return new ProductRequest("IPhone", "Phone", BigDecimal.valueOf(1000));
    }

    @Test
    void createProduct_withValidOutput_createsOneUser() throws Exception {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(createProductRequest()))
                .when()
                .post("/api/product")
                .then()
                .statusCode(201);

        var products = productRepository.findAll();

        Assertions.assertEquals(1, products.size());
        Assertions.assertEquals("IPhone", products.get(0).getName());
    }

    @Test
    void createProduct_withInvalidOutput_returnsBadRequest() throws Exception {
        ProductRequest request = new ProductRequest("IPhone", "Phone", null);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .when()
                .post("/api/product")
                .then()
                .statusCode(400);
    }

    @Test
    void getAllProducts_returnsListOfProducts() throws Exception {
        productRepository.save(new Product("IPhone", "Phone", BigDecimal.valueOf(1000)));

        RestAssured.given()
                .when()
                .get("/api/product")
                .then()
                .statusCode(200)
                .body("size()", Matchers.equalTo(1))
                .body("[0].name", Matchers.equalTo("IPhone"));
    }

    @Test
    void getById_withExistingId_returnsProduct() throws Exception {
        Product savedProduct = productRepository.save(new Product("IPhone", "Phone", BigDecimal.valueOf(1000)));

        RestAssured.given()
                .when()
                .get("/api/product/{id}", savedProduct.getId())
                .then()
                .statusCode(200)
                .body("id", Matchers.equalTo(savedProduct.getId()))
                .body("name", Matchers.equalTo("IPhone"))
                .body("description", Matchers.equalTo("Phone"))
                .body("price", Matchers.equalTo(1000));
    }

    @Test
    void getById_withNonExistingId_returnsNotFound() throws Exception {
        String nonExistingId = "67c0f8a2f9b5c6d4e2a1b3c5";

        RestAssured.given()
                .when()
                .get("/api/product/{id}", nonExistingId)
                .then()
                .statusCode(404);
    }
}
