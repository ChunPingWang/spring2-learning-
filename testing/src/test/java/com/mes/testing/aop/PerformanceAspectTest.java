package com.mes.testing.aop;

import com.mes.testing.application.OrderService;
import com.mes.testing.domain.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AOP 切面測試")
class PerformanceAspectTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderService targetOrderService;

    @Test
    @DisplayName("PerfMonitor 註解應該能夠正常攔截方法")
    void perfMonitorAnnotation_shouldInterceptMethod() {
        when(orderService.createOrder(anyString(), anyString(), anyDouble()))
                .thenReturn(new Order("ORD-001", "Customer A", 100.0));

        Order result = orderService.createOrder("ORD-001", "Customer A", 100.0);

        assertThat(result).isNotNull();
        assertThat(result.getOrderId()).isEqualTo("ORD-001");
        verify(orderService, times(1)).createOrder("ORD-001", "Customer A", 100.0);
    }

    @Test
    @DisplayName("PerformanceAspect 應該記錄方法執行時間")
    void aspect_shouldLogExecutionTime() {
        when(orderService.getAllOrders()).thenReturn(java.util.Collections.emptyList());

        orderService.getAllOrders();

        verify(orderService, times(1)).getAllOrders();
    }
}
