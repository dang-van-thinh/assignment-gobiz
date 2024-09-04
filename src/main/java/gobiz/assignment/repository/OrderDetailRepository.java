package gobiz.assignment.repository;

import gobiz.assignment.entity.OrderDetail;
import gobiz.assignment.model.data.OrderDetailData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail,String>, JpaSpecificationExecutor<OrderDetail> {
    List<OrderDetail> findAllByOrderIdIs(String orderId);

    @Query("select new gobiz.assignment.model.data.OrderDetailData(o,od,p ) from OrderDetail od , Orders o , Products p " +
            "where od.orderId = o.id " +
            "and od.productId = p.id")
    List<OrderDetailData> findAllOrder();

    @Query("select new gobiz.assignment.model.data.OrderDetailData(o,od,p) from OrderDetail od,Orders o,Products p " +
            "where od.orderId = o.id " +
            "and od.productId= p.id " +
            "and (:orderId is null or od.orderId = :orderId ) " +
            "and (:nameCustomer is null or o.userName like %:nameCustomer%)")
    List<OrderDetailData> searchOrder(
            @Param("orderId") String orderId,
            @Param("nameCustomer") String nameCustomer
    );
}
