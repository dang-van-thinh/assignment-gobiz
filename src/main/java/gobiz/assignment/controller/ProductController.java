package gobiz.assignment.controller;

import gobiz.assignment.model.dto.product.ProductDto;
import gobiz.assignment.model.dto.product.UpdateProductDto;
import gobiz.assignment.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/products")
public class ProductController {
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody ProductDto request){
        return ResponseEntity.ok(productService.create(request));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<?> getOne(@PathVariable String productId){
        return ResponseEntity.ok(productService.getOne(productId));
    }

    @GetMapping
    public ResponseEntity<?> all(){
        return ResponseEntity.ok(productService.all());
    }

    @PutMapping("/{productId}")
    public ResponseEntity<?> update(
            @PathVariable String productId,
            @RequestBody UpdateProductDto request
            ){
        return ResponseEntity.ok(productService.update(productId,request));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> softDelete(@PathVariable String productId){
        productService.softDelete(productId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{productId}/delete")
    public ResponseEntity<?> hardDelete(@PathVariable String productId){
        productService.hardDelete(productId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{productId}/restore")
    public ResponseEntity<?> restoreProduct(@PathVariable String productId){
        return ResponseEntity.ok(productService.restoreProduct(productId));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchProduct(
            @RequestParam(value = "key" ,required = false) String name
    ){
        return ResponseEntity.ok(productService.searchProduct(name));
    }
}
