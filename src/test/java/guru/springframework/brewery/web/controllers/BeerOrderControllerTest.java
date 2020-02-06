package guru.springframework.brewery.web.controllers;

import guru.springframework.brewery.services.BeerOrderService;
import guru.springframework.brewery.web.model.BeerOrderDto;
import guru.springframework.brewery.web.model.BeerOrderPagedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BeerOrderController.class)
class BeerOrderControllerTest {

    @MockBean
    BeerOrderService beerOrderService;

    @Autowired
    MockMvc mockMvc;

    BeerOrderPagedList validPagedList;

    @Captor
    ArgumentCaptor<PageRequest> pageRequestCaptor;

    @BeforeEach
    void setUp() {
        validPagedList = new BeerOrderPagedList(Arrays.asList(new BeerOrderDto()));
    }

    @Test
    void listOrders() throws Exception {
        given(beerOrderService.listOrders(any(), pageRequestCaptor.capture()))
                .willReturn(new BeerOrderPagedList(Arrays.asList(new BeerOrderDto())));

        mockMvc.perform(get("/api/v1/customers/" + UUID.randomUUID() + "/orders")
                .param("pageNumber", "1")
                .param("pageSize", "1")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0]").isMap());

        then(beerOrderService).should().listOrders(any(), any());

        assertThat(pageRequestCaptor.getValue()).isEqualTo(PageRequest.of(1, 1));
        // Captors are not mocks, so you can't use verify() or BDDMockito.then(), you have to use assert statements.
    }

    @Test
    void getOrder() throws Exception {
        UUID customerId = UUID.randomUUID();

        given(beerOrderService.getOrderById(any(UUID.class), any(UUID.class)))
                .willReturn(BeerOrderDto.builder().customerId(customerId).build());

        mockMvc.perform(get("/api/v1/customers/" + customerId + "/orders/" + UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.customerId").value(customerId.toString()));

        then(beerOrderService).should().getOrderById(any(UUID.class),any(UUID.class));
    }
}





