package guru.springframework.brewery.web.controllers;

import guru.springframework.brewery.domain.Beer;
import guru.springframework.brewery.domain.Customer;
import guru.springframework.brewery.repositories.BeerOrderRepository;
import guru.springframework.brewery.repositories.BeerRepository;
import guru.springframework.brewery.repositories.CustomerRepository;
import guru.springframework.brewery.web.model.BeerOrderPagedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.http.ResponseEntity;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BeerOrderControllerIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private BeerOrderRepository beerOrderRepository;

    @MockBean
    private BeerRepository beerRepository;
    @MockBean
    private CustomerRepository customerRepository;

    Customer customer;

    @BeforeEach
    void setUp() {
        customer = customerRepository.findAll().get(0);
    }

    @Test
    void givenHappyPath_whenOrdersEndpointCalled_thenBeerOrderPagedListIsReturned_200OK() {
        String url = "/api/v1/customers/" + customer.getId().toString() + "/orders";

        BeerOrderPagedList pagedList = restTemplate.getForObject(url, BeerOrderPagedList.class);

        assertThat(pagedList.getContent()).hasSize(3);

//        given(beerRepository.findById(any(UUID.class))).willReturn(Optional.of(new Beer()));
//        given(customerRepository.findById(any(UUID.class))).willReturn(Optional.of(new Customer()));
//
//        ResponseEntity<BeerOrderPagedList> list = restTemplate.getForEntity("/api/v1/customers/" + UUID.randomUUID() + "orders", BeerOrderPagedList.class);
//
//        assertThat(PageRequest.of(1,1)).isEqualTo(list);
    }
}