package com.elasticsearchsnippets.services;

import com.elasticsearchsnippets.model.Product;
import com.elasticsearchsnippets.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private static String INDEX = "productindex";
    private final ProductRepository productRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Product findById(String id) {
        return productRepository.findById(id).orElse(null);
    }

    /**
     * Finds by exact name
     */
    public Page<Product> findByNameWithSpringData(String name, Pageable pageable) {
        return productRepository.findByName(name, pageable);
    }

    /**
     * Match query (elasticsearch)
     */
    public List<Product> findByNameByNativeQuery(String name, Pageable pageable) {
        QueryBuilder queryBuilder =
                QueryBuilders
                        .matchQuery("name", name);

        Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .withPageable(pageable)
                .build();

        SearchHits<Product> productHits =
                elasticsearchOperations
                        .search(searchQuery,
                                Product.class,
                                IndexCoordinates.of(INDEX));

        return productHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    /**
     * Match query (elasticsearch) from resources
     */
    public List<Product> findByNameByStringQuery(String name) {
        String query = readFromClassPath("queries/findByName.json");
        String queryWithName = String.format(query, name);

        Query searchQuery = new StringQuery(queryWithName);

        SearchHits<Product> productHits = elasticsearchOperations.search(
                searchQuery,
                Product.class,
                IndexCoordinates.of(INDEX));

        return productHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    public List<Product> findByPriceWithCriteriaQuery() {
        Criteria criteria = new Criteria("price")
                .greaterThan(10.0)
                .lessThan(100.0);

        Query searchQuery = new CriteriaQuery(criteria);

        SearchHits<Product> productHits = elasticsearchOperations
                .search(searchQuery,
                        Product.class,
                        IndexCoordinates.of(INDEX));

        return productHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    private String readFromClassPath(String filename) {
        try (final BufferedReader in = new BufferedReader(new InputStreamReader(new ClassPathResource(filename).getInputStream()))) {
            return in.lines().collect(Collectors.joining(""));
        } catch (IOException e) {
            throw new RuntimeException("Resource file " + filename + " is not found");
        }
    }

    public List<String> fetchSuggestionsByName(String query) {
        QueryBuilder queryBuilder = QueryBuilders
                .wildcardQuery("name", query+"*");

        Query searchQuery = new NativeSearchQueryBuilder()
                .withFilter(queryBuilder)
                .withPageable(PageRequest.of(0, 2))
                .build();

        SearchHits<Product> searchSuggestions =
                elasticsearchOperations.search(searchQuery,
                        Product.class,
                        IndexCoordinates.of(INDEX));

        List<String> suggestions = new ArrayList<>();

        searchSuggestions.getSearchHits().forEach(searchHit->{
            suggestions.add(searchHit.getContent().getName());
        });
        return suggestions;
    }


    public void delete(Product product) {
        productRepository.delete(product);
    }
}
