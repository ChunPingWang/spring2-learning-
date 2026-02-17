package com.mes.testing.hateoas;

import com.mes.testing.application.OrderService;
import com.mes.testing.domain.Order;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class OrderAssembler implements RepresentationModelAssembler<Order, EntityModel<Order>> {

    @Override
    public EntityModel<Order> toModel(Order order) {
        EntityModel<Order> model = EntityModel.of(order);
        
        model.add(linkTo(methodOn(OrderHateoasController.class).getOrder(order.getOrderId()))
                .withSelfRel());
        
        model.add(linkTo(methodOn(OrderHateoasController.class).getAllOrders(null))
                .withRel("all-orders"));
        
        if (order.getStatus() == Order.OrderStatus.PENDING) {
            model.add(linkTo(methodOn(OrderHateoasController.class).confirmOrder(order.getOrderId()))
                    .withRel("confirm"));
            model.add(linkTo(methodOn(OrderHateoasController.class).cancelOrder(order.getOrderId()))
                    .withRel("cancel"));
        }
        
        if (order.getStatus() == Order.OrderStatus.CONFIRMED) {
            model.add(linkTo(methodOn(OrderHateoasController.class).shipOrder(order.getOrderId()))
                    .withRel("ship"));
        }
        
        return model;
    }

    public RepresentationModel<?> getLinks() {
        RepresentationModel<?> root = new RepresentationModel<>();
        root.add(linkTo(methodOn(OrderHateoasController.class).getAllOrders(null))
                .withRel("orders"));
        return root;
    }
}
