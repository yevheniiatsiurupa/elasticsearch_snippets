package com.elasticsearchsnippets.repositories;

import com.elasticsearchsnippets.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ProductRepository extends ElasticsearchRepository<Product, String> {

    Page<Product> findByName(String name, Pageable pageable);

    List<Product> findByNameContaining(String name);

    List<Product> findByManufacturerAndCategory (String manufacturer, String category);
}
