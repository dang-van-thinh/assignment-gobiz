package gobiz.assignment.repository;

import gobiz.assignment.entity.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Products,String> {
    List<Products> findAllByIdInAndDeleteAtIsNull(List<String> id);
    List<Products> findAllByDeleteAtIsNull();
    List<Products> findAllByDeleteAtIsNotNull();
    List<Products> findAllByNameIsLikeIgnoreCaseOrDescriptionIsLikeIgnoreCase(String name, String description);

}
