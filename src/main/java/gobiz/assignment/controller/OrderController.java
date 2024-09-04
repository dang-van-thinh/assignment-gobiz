package gobiz.assignment.controller;

import gobiz.assignment.model.dto.order.CreateOrderRequest;
import gobiz.assignment.model.dto.order.OrderResponse;
import gobiz.assignment.model.dto.order.UpdateOrderRequest;
import gobiz.assignment.service.OrderService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/orders")
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateOrderRequest request) throws BadRequestException {
        return ResponseEntity.ok(orderService.create(request));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOneOrder(@PathVariable String orderId){
        return ResponseEntity.ok(orderService.getOneOrder(orderId));
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<?> updateOrder(
            @PathVariable String orderId,
            @RequestBody UpdateOrderRequest request
            ) throws BadRequestException {
        return ResponseEntity.ok(orderService.updateOrder(orderId,request));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> all(){
        return ResponseEntity.ok(orderService.getAll());
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchOrder(
            @RequestParam(value = "name",required = false) String name,
            @RequestParam(value = "orderId",required = false) String orderId
    ){
        return ResponseEntity.ok(orderService.search(name,orderId));
    }
}
