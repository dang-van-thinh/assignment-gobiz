package gobiz.assignment.service;

import gobiz.assignment.entity.OrderDetail;
import gobiz.assignment.entity.Orders;
import gobiz.assignment.entity.Products;
import gobiz.assignment.entity.enums.StatusOrder;
import gobiz.assignment.model.data.OrderDetailData;
import gobiz.assignment.model.dto.order.*;
import gobiz.assignment.model.dto.orderDetail.OrderDetailResponse;
import gobiz.assignment.model.dto.product.ProductDto;
import gobiz.assignment.model.dto.product.ProductInOrderRequest;
import gobiz.assignment.repository.OrderDetailRepository;
import gobiz.assignment.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.rowset.Predicate;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final OrderDetailRepository orderDetailRepository;

    @Autowired
    public OrderService(
            OrderRepository orderRepository,
            ProductService productService,
            OrderDetailRepository orderDetailRepository
    ) {
        this.orderRepository = orderRepository;
        this.productService = productService;
        this.orderDetailRepository = orderDetailRepository;
    }

    @Transactional
    public OrderResponse create(CreateOrderRequest request) throws BadRequestException {

        if (request.getProduct().isEmpty()) {
            throw new BadRequestException("No product information !");
        }
        // lay so luong sp order theo id san pham order
        Map<String, Integer> productQuantity = request.getProduct().stream()
                .collect(Collectors.toMap(ProductInOrderRequest::getProductId, ProductInOrderRequest::getQuantity));
        // lay danh sach idProduct
        List<String> productIds = request.getProduct().stream().map(ProductInOrderRequest::getProductId).toList();
        log.info("Id sna pham " + productIds.toString());
        // tim danh sach product theo danh sach id
        List<ProductDto> products = productService.getAllProductsByIds(productIds);
        log.info("San pham " + products.toString());

        String idOrder = UUID.randomUUID().toString();

        // danh sach order_detail cua order
        List<OrderDetail> orderDetails = new ArrayList<>();
        double totalPrice = 0;
        //danh sach product cap nhat lai so luong sp trong kho
        List<Products> listProducted = new ArrayList<>();

        for (ProductDto product : products) {
            // check so luong them moi phải lon hon 0 va it hon so luong ton kho
            if (productQuantity.get(product.getId()) < 1) {
                throw new BadRequestException("Product " + product.getId() + " quantity must be greater than 0 !");
            }
            if (productQuantity.get(product.getId()) > product.getQuantity()) {
                throw new BadRequestException("Quantity of products is more than quantity in stock with product " + product.getId() + " quantity: " + productQuantity.get(product.getId()) + " !");
            }

            // set lai so luong trong kho
            product.setQuantity(product.getQuantity() - productQuantity.get(product.getId()));

            listProducted.add(new Products(
                    product.getId(),
                    product.getName(),
                    product.getDescription(),
                    product.getPrice(),
                    product.getQuantity(),
                    null
            ));
            // tinh tong tien thanh toan
            totalPrice += (product.getPrice() * productQuantity.get(product.getId()));

            orderDetails.add(
                    OrderDetail.builder()
                            .orderId(idOrder)
                            .productId(product.getId())
                            .price(product.getPrice())
                            .quantity(productQuantity.get(product.getId()))
                            .build()
            );
        }

        log.info("thong tin product sau khi them vao don hang : " + products);
// cap nhat so luong ton kho
        productService.updateQuantityProduct(listProducted);

        Orders order = new Orders(
                idOrder,
                request.getUserName(),
                request.getAddress(),
                request.getEmail(),
                request.getPhone(),
                LocalDate.now(),
                StatusOrder.PROCESSING,
                totalPrice
        );

        List<OrderDetail> orderDetailed = orderDetailRepository.saveAll(orderDetails);
        // tra ra order detail
        List<OrderDetailResponse> orderDetailResponse = new ArrayList<>();

        orderDetailed.forEach(orderDetail -> {
            products.forEach(productResponse -> {
                if (orderDetail.getProductId().equals(productResponse.getId())) {
                    orderDetailResponse.add(
                            OrderDetailResponse.builder()
                                    .id(orderDetail.getId())
                                    .orderId(orderDetail.getOrderId())
                                    .product(productResponse)
                                    .price(orderDetail.getPrice())
                                    .quantity(orderDetail.getQuantity())
                                    .build()
                    );
                }
            });
        });

        Orders ordered = orderRepository.save(order);
        // tra ra order
        return OrderResponse.builder()
                .id(ordered.getId())
                .userName(ordered.getUserName())
                .address(ordered.getAddress())
                .email(ordered.getEmail())
                .phone(ordered.getPhone())
                .dateCreate(ordered.getDateCreate())
                .status(ordered.getStatus())
                .payPrice(ordered.getPayPrice())
                .orderDetail(orderDetailResponse)
                .build();
    }

    private Orders getOrder(String orderId) {
        Optional<Orders> order = orderRepository.findById(orderId);
        if (order.isEmpty()) {
            throw new NullPointerException("Order not found !");
        }
        return order.get();
    }

    public OrderResponse getOneOrder(String orderId) {
        OrderDto orderDto = OrderDto.orderDto(getOrder(orderId));
        // truy van lay danh sach order detail theo orderId
        List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrderIdIs(orderDto.getId());
        // response cua ds order detail
        List<OrderDetailResponse> orderDetailResponses = new ArrayList<>();
        // lay ra danh sach productId cua order
        List<String> productIds = orderDetails.stream()
                .map(OrderDetail::getProductId)
                .collect(Collectors.toList());
        // thuc hien truy van lay ra thong tin: productId => product
        Map<String, ProductDto> products = productService.getAllProductsByIds(productIds).stream()
                .collect(Collectors.toMap(ProductDto::getId, Function.identity()));

        for (OrderDetail orderDetail : orderDetails) {
            orderDetailResponses.add(
                    OrderDetailResponse.builder()
                            .id(orderDetail.getId())
                            .orderId(orderDetail.getOrderId())
                            .product(products.get(orderDetail.getProductId()))
                            .price(orderDetail.getPrice())
                            .quantity(orderDetail.getQuantity())
                            .build()
            );
        }

        return OrderResponse.builder()
                .id(orderDto.getId())
                .userName(orderDto.getUserName())
                .address(orderDto.getAddress())
                .email(orderDto.getEmail())
                .phone(orderDto.getPhone())
                .dateCreate(orderDto.getDateCreate())
                .status(orderDto.getStatus())
                .payPrice(orderDto.getPayPrice())
                .orderDetail(orderDetailResponses)
                .build();
    }

    @Transactional(rollbackFor = BadRequestException.class)
    public OrderResponse updateOrder(String orderId, UpdateOrderRequest request) throws BadRequestException {
        Orders order = getOrder(orderId);
        StatusOrder oldStatusOrder = order.getStatus();
        log.info("status order present " + oldStatusOrder);
        if (StatusOrder.PROCESSING.equals(oldStatusOrder)) {
            if (request.getStatus() != null) {
                if ((StatusOrder.IN_TRANSIT.equals(request.getStatus()) || StatusOrder.CANCELED.equals(request.getStatus()))) {
                    order.setStatus(request.getStatus());
                } else {
                    throw new BadRequestException("Invalid status order !");
                }
            }
            if (request.getEmail() != null) {
                order.setEmail(request.getEmail());
            }
            if (request.getAddress() != null) {
                order.setAddress(request.getAddress());
            }
            if (request.getPhone() != null) {
                order.setPhone(request.getPhone());
            }
        }

        checkChangeInfoCustomer(request);

        if (request.getStatus() != null) {
            if (StatusOrder.IN_TRANSIT.equals(oldStatusOrder) &&
                    (StatusOrder.SUCCESS.equals(request.getStatus()) ||
                            StatusOrder.FAILED.equals(request.getStatus()))
            ) {
                order.setStatus(request.getStatus());
            }

            if (StatusOrder.SUCCESS.equals(oldStatusOrder) &&
                    StatusOrder.RETURN.equals(request.getStatus())) {
                order.setStatus(StatusOrder.RETURN);
            }

            if (StatusOrder.FAILED.equals(oldStatusOrder) &&
                    StatusOrder.RETURNED_TO_SENDER.equals(request.getStatus())) {
                order.setStatus(StatusOrder.RETURNED_TO_SENDER);
            }

            if (StatusOrder.RETURNED_TO_SENDER.equals(oldStatusOrder) &&
                    StatusOrder.RETURNED.equals(request.getStatus())) {
                order.setStatus(StatusOrder.RETURNED);
            }

            if (StatusOrder.RETURN.equals(oldStatusOrder) &&
                    StatusOrder.RETURNED.equals(request.getStatus())) {
                order.setStatus(StatusOrder.RETURNED);
            }

            if (StatusOrder.CANCELED.equals(oldStatusOrder)) {
                throw new BadRequestException("Order canceled , cannot be processed !");
            }
            if (StatusOrder.RETURNED.equals(oldStatusOrder)) {
                throw new BadRequestException("Order returned , cannot be processed !");
            }

            if (oldStatusOrder.equals(order.getStatus())) {
                throw new BadRequestException("Invalid status order !");
            }
        }

        orderRepository.save(order);
        return this.getOneOrder(order.getId());
    }

    private void checkChangeInfoCustomer(UpdateOrderRequest request) throws BadRequestException {
        if (request.getEmail() != null || request.getAddress() != null || request.getPhone() != null) {
            throw new BadRequestException("Cannot change order information because order is being shipped !");
        }
    }

    private List<OrderResponse> showOrderResponse(List<OrderDetailData> data) {
        // lay danh sach order
        Map<String, Orders> ordersMap = new HashMap<>();
        // danh sach order detail
        List<OrderDetailResponse> orderDetailResponses = new ArrayList<>();

        data.forEach(orderDetailData -> {
            orderDetailResponses.add(
                    OrderDetailResponse.builder()
                            .id(orderDetailData.getOrderDetail().getId())
                            .orderId(orderDetailData.getOrders().getId())
                            .product(ProductDto.productDto(orderDetailData.getProducts()))
                            .price(orderDetailData.getOrderDetail().getPrice())
                            .quantity(orderDetailData.getOrderDetail().getQuantity())
                            .build()
            );
            ordersMap.put(orderDetailData.getOrders().getId(), orderDetailData.getOrders());
        });
        log.info("order map size :" + ordersMap.size());
        log.info("orderDetailResponses map size :" + orderDetailResponses.size());

        List<OrderResponse> orderResponses = new ArrayList<>();

        ordersMap.forEach((orderId, order) -> {
            List<OrderDetailResponse> orderDetailOfOrder = new ArrayList<>();
            orderDetailResponses.forEach(orderDetailResponse -> {
                if (orderId.equals(orderDetailResponse.getOrderId())) {
                    orderDetailOfOrder.add(orderDetailResponse);
                }
            });
            orderResponses.add(OrderResponse.builder()
                    .id(orderId)
                    .userName(order.getUserName())
                    .address(order.getAddress())
                    .email(order.getEmail())
                    .phone(order.getPhone())
                    .dateCreate(order.getDateCreate())
                    .status(order.getStatus())
                    .payPrice(order.getPayPrice())
                    .orderDetail(orderDetailOfOrder)
                    .build());
        });
        log.info("order response size :" + orderResponses.size());
        if (orderResponses.isEmpty()) {
            throw new NullPointerException("No matching order found !");
        }
        return orderResponses;
    }

    public List<OrderResponse> getAll() {
        List<OrderDetailData> alls = orderDetailRepository.findAllOrder();
        return showOrderResponse(alls);
    }

    public List<OrderResponse> search(String name, String orderId) {
        log.info(orderId);
        log.info(name);
        List<OrderDetailData> result = orderDetailRepository.searchOrder(orderId, name);
        return showOrderResponse(result);
    }
}
