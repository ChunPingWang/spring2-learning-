package com.mes.testing.unit;

import com.mes.testing.domain.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Order 單元測試")
class OrderTest {

    @Nested
    @DisplayName("建立工單")
    class CreateOrder {

        @Test
        @DisplayName("應該成功建立待處理狀態的訂單")
        void createOrder_shouldHavePendingStatus() {
            Order order = new Order("ORD-001", "Customer A", 100.0);

            assertThat(order.getOrderId()).isEqualTo("ORD-001");
            assertThat(order.getCustomerName()).isEqualTo("Customer A");
            assertThat(order.getAmount()).isEqualTo(100.0);
            assertThat(order.getStatus()).isEqualTo(Order.OrderStatus.PENDING);
        }
    }

    @Nested
    @DisplayName("確認訂單")
    class ConfirmOrder {

        @Test
        @DisplayName("待處理訂單應該可以確認")
        void confirmPendingOrder_shouldSucceed() {
            Order order = new Order("ORD-002", "Customer B", 200.0);

            order.confirm();

            assertThat(order.getStatus()).isEqualTo(Order.OrderStatus.CONFIRMED);
        }

        @Test
        @DisplayName("已確認的訂單不可再次確認")
        void confirmConfirmedOrder_shouldThrow() {
            Order order = new Order("ORD-003", "Customer C", 300.0);
            order.confirm();

            assertThatThrownBy(order::confirm)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Only pending orders can be confirmed");
        }

        @Test
        @DisplayName("已取消的訂單不可確認")
        void confirmCancelledOrder_shouldThrow() {
            Order order = new Order("ORD-004", "Customer D", 400.0);
            order.cancel();

            assertThatThrownBy(order::confirm)
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("取消訂單")
    class CancelOrder {

        @Test
        @DisplayName("待處理訂單應該可以取消")
        void cancelPendingOrder_shouldSucceed() {
            Order order = new Order("ORD-005", "Customer E", 500.0);

            order.cancel();

            assertThat(order.getStatus()).isEqualTo(Order.OrderStatus.CANCELLED);
        }

        @Test
        @DisplayName("已出貨的訂單不可取消")
        void cancelShippedOrder_shouldThrow() {
            Order order = new Order("ORD-006", "Customer F", 600.0);
            order.confirm();
            order.ship();

            assertThatThrownBy(order::cancel)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot cancel shipped");
        }
    }

    @Nested
    @DisplayName("出貨訂單")
    class ShipOrder {

        @Test
        @DisplayName("已確認的訂單可以出貨")
        void shipConfirmedOrder_shouldSucceed() {
            Order order = new Order("ORD-007", "Customer G", 700.0);
            order.confirm();

            order.ship();

            assertThat(order.getStatus()).isEqualTo(Order.OrderStatus.SHIPPED);
        }

        @Test
        @DisplayName("待處理訂單不可直接出貨")
        void shipPendingOrder_shouldThrow() {
            Order order = new Order("ORD-008", "Customer H", 800.0);

            assertThatThrownBy(order::ship)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Only confirmed orders");
        }
    }
}
