package com.parkhomovsky.bookstore.controller;

import com.parkhomovsky.bookstore.dto.order.OrderDto;
import com.parkhomovsky.bookstore.dto.order.OrderPlaceRequestDto;
import com.parkhomovsky.bookstore.dto.order.OrderUpdateStatusRequest;
import com.parkhomovsky.bookstore.dto.order.UpdateResponseDto;
import com.parkhomovsky.bookstore.dto.orderitem.OrderItemDto;
import com.parkhomovsky.bookstore.exception.EmptyShoppingCartException;
import com.parkhomovsky.bookstore.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/orders")
@Tag(name = "Orders management",
        description = "Endpoints for managing user orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/place-order")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Place new order",
            description = "Create a new order and add it to the database")
    public OrderDto placeOrder(@RequestBody OrderPlaceRequestDto orderPlaceRequestDto)
            throws EmptyShoppingCartException {
        return orderService.process(orderPlaceRequestDto);
    }

    @GetMapping
    @Operation(summary = "Get all orders",
            description = "Retrieve all orders history")
    public List<OrderDto> getAll(Pageable pageable, Authentication authentication) {
        return orderService.getAll(pageable, authentication);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Update order status",
            description = "Update order status by admin in database")
    public UpdateResponseDto updateStatus(@PathVariable Long id,
                                          @Valid @RequestBody
                                 OrderUpdateStatusRequest orderUpdateStatusRequest) {
        return orderService.updateStatus(id, orderUpdateStatusRequest);
    }

    @GetMapping("/{orderId}/items")
    @Operation(summary = "Get all orders by orderId",
            description = "Retrieve all orders for a specific order")
    public List<OrderItemDto> getAllFromOrder(@PathVariable Long orderId, Pageable pageable) {
        return orderService.getOrderItemsDto(pageable, orderId);
    }

    @GetMapping("/{orderId}/items/{itemId}")
    @Operation(summary = "Get item by orderId and itemId",
            description = "Retrieve a specific OrderItem within an order")
    public OrderItemDto getItemFromOrder(@PathVariable Long orderId,
                                             @PathVariable Long itemId) {
        return orderService.getOrderItemByidDto(orderId, itemId);
    }
}
