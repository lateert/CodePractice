package cn.codepractice.oj.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Пример изоляции внешнего HTTP API заглушкой WireMock (как в требованиях к интеграциям).
 */
class WireMockExternalApiTest {

    private WireMockServer wireMockServer;

    @BeforeEach
    void startServer() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().dynamicPort());
        wireMockServer.start();
    }

    @AfterEach
    void stopServer() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @Test
    void stubbedGet_returnsBody() {
        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo("/external/ping"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/plain")
                        .withBody("pong")));

        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:" + wireMockServer.port() + "/external/ping";
        String body = restTemplate.getForObject(url, String.class);
        assertEquals("pong", body);
    }
}
