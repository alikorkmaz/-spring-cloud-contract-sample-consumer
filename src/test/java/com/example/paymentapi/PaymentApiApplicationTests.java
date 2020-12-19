package com.example.paymentapi;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@AutoConfigureWireMock(port = 8082)
@AutoConfigureStubRunner(ids = {"com.example:fraud-api:8083"},
        stubsMode = StubRunnerProperties.StubsMode.LOCAL)
class PaymentApiApplicationTests {

    @Test // e2e test
    public void should_check_fraud_status() {
        String json = "{\"fraud\":true,\"message\":\"Stolen Card\"}";

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity =
                restTemplate.getForEntity("http://localhost:8081/check", String.class);

        BDDAssertions.then(responseEntity.getStatusCodeValue()).isEqualTo(200);
        BDDAssertions.then(responseEntity.getBody()).isEqualTo(json);
    }

    @Test // mock
    public void should_check_fraud_status_mock() {
        String json = "{\"fraud\":true,\"message\":\"Stolen Card\"}";

        WireMock.stubFor(WireMock.get(WireMock.urlEqualTo("/check"))
                .willReturn(WireMock.aResponse().withBody(json).withStatus(200)));

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity =
                restTemplate.getForEntity("http://localhost:8082/check", String.class);

        BDDAssertions.then(responseEntity.getStatusCodeValue()).isEqualTo(200);
        BDDAssertions.then(responseEntity.getBody()).isEqualTo(json);
    }

    @Test // stubs
    public void should_check_fraud_status_stub() {
        String json = "{\"fraud\":true,\"message\":\"Stolen Card\"}";

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity =
                restTemplate.getForEntity("http://localhost:8083/check", String.class);

        BDDAssertions.then(responseEntity.getStatusCodeValue()).isEqualTo(200);
        BDDAssertions.then(responseEntity.getBody()).isEqualTo(json);
    }
}
