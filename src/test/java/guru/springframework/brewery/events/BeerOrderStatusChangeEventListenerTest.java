package guru.springframework.brewery.events;

import com.github.jenspiegsa.wiremockextension.Managed;
import com.github.jenspiegsa.wiremockextension.ManagedWireMockServer;
import com.github.jenspiegsa.wiremockextension.WireMockExtension;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import guru.springframework.brewery.domain.BeerOrder;
import guru.springframework.brewery.domain.OrderStatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.web.client.RestTemplateBuilder;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@ExtendWith(WireMockExtension.class)
class BeerOrderStatusChangeEventListenerTest {

    @Managed    //Allows WireMockExtension to manage the wireMockServer ???
    WireMockServer wireMockServer = ManagedWireMockServer.with( //Allows configuration of WireMockServer
            WireMockConfiguration.wireMockConfig().dynamicPort() //Configures port number
    );

    BeerOrderStatusChangeEventListener listener;

    @BeforeEach
    void setUp() {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        listener = new BeerOrderStatusChangeEventListener(restTemplateBuilder);
    }

    @Test
    void listen() {
        wireMockServer.stubFor(post("/update").willReturn(ok()));

        BeerOrder beerOrder = BeerOrder.builder()
                .orderStatus(OrderStatusEnum.READY)
                .orderStatusCallbackUrl("http://localhost:" + wireMockServer.port() + "/update") //points to wireMockServer
                .createdDate(Timestamp.valueOf(LocalDateTime.now()))
                .build();

        BeerOrderStatusChangeEvent event = new BeerOrderStatusChangeEvent(beerOrder, OrderStatusEnum.NEW);

        listener.listen(event);

        verify(1, postRequestedFor(urlEqualTo("/update")));
    }
}