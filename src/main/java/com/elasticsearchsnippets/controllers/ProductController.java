package com.elasticsearchsnippets.controllers;

import com.elasticsearchsnippets.model.Product;
import com.elasticsearchsnippets.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping(value = "/{id}")
    public ResponseEntity<Product> findById(@PathVariable String id) {
        return new ResponseEntity<>(productService.findById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Product> save(@RequestBody Product product) {
        return new ResponseEntity<>(productService.save(product), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<Product>> findAll(@PageableDefault Pageable pageable) {
        return new ResponseEntity<>(productService.findAll(pageable), HttpStatus.OK);
    }

    @GetMapping(value = "/name/{name}")
    public ResponseEntity<List<Product>> findByName(@PathVariable String name) {
        return new ResponseEntity<>(productService.findByNameByStringQuery(name), HttpStatus.OK);
    }

    @GetMapping(value = "/suggest/{name}")
    public ResponseEntity<List<String>> fetchSuggestions(@PathVariable String name) {
        return new ResponseEntity<>(productService.fetchSuggestionsByName(name), HttpStatus.OK);
    }

}
