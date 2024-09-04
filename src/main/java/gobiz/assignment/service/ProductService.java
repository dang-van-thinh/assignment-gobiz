package gobiz.assignment.service;

import gobiz.assignment.entity.Products;
import gobiz.assignment.model.dto.product.ProductDto;
import gobiz.assignment.model.dto.product.UpdateProductDto;
import gobiz.assignment.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductDto> all() {
        List<Products> products = productRepository.findAllByDeleteAtIsNull();
        return products.stream()
                .map(ProductDto::productDto).toList();
    }

    public ProductDto create(ProductDto request) {
        String id = UUID.randomUUID().toString();
        Products products = new Products(
                id,
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getQuantity(),
                null
        );
        return ProductDto.productDto(productRepository.save(products));
    }

    public ProductDto getOne(String productId) {
        return ProductDto.productDto(getProduct(productId));
    }

    private Products getProduct(String productId) {
        Optional<Products> product = productRepository.findById(productId);
        if (product.isEmpty()) {
            throw new NullPointerException("Product Not Found");
        }
        return product.get();
    }

    public ProductDto update(String productId, UpdateProductDto request) {
        Products product = getProduct(productId);
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }

        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }

        return ProductDto.productDto(productRepository.save(product));
    }

    public List<ProductDto> getAllProductsByIds(List<String> ids) {
        List<ProductDto> productDto = productRepository.findAllByIdInAndDeleteAtIsNull(ids).stream().map(ProductDto::productDto).toList();

        if (productDto.isEmpty()) {
            throw new NullPointerException("No matching products found !");
        }
        return productDto;
    }

    public List<ProductDto> updateQuantityProduct(List<Products> products) {
        return productRepository.saveAll(products).stream().map(ProductDto::productDto).toList();
    }

    public void softDelete(String productId){
        Products producted = getProduct(productId);
        producted.setDeleteAt(LocalDate.now());
        productRepository.save(producted);
    }

    public void hardDelete(String productId){
        productRepository.delete(getProduct(productId));
    }

    public ProductDto restoreProduct(String productId){
        Products producted = getProduct(productId);
        producted.setDeleteAt(null);
        return ProductDto.productDto(productRepository.save(producted));
    }

    public List<ProductDto> searchProduct(String key){
        List<Products> search = productRepository
                .findAllByNameIsLikeIgnoreCaseOrDescriptionIsLikeIgnoreCase("%"+key+"%","%"+key+"%");
       if (search.isEmpty()){
           throw new NullPointerException("No matching products found !");
       }
        return search.stream().map(ProductDto::productDto).toList();
    }

}
