package org.dehorsed.microservices.product.repository;

import org.dehorsed.microservices.product.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {

}
