package com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.web;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static io.restassured.config.JsonConfig.jsonConfig;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.rafaelsousa.algashop.ordering.infrastructure.config.MockJwtDecoderConfig;
import com.rafaelsousa.algashop.ordering.infrastructure.config.TestcontainerPostgreSQLConfig;
import com.rafaelsousa.algashop.ordering.utils.MockJwtFactory;
import io.restassured.RestAssured;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpHeaders;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

@Import({TestcontainerPostgreSQLConfig.class, MockJwtDecoderConfig.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(
        scripts = "classpath:db/testdata/afterMigrate.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(
        scripts = "classpath:db/clean/afterMigrate.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
public abstract class AbstractPresentationIT {

    @LocalServerPort protected int port;

    protected static WireMockServer wireMockRapidex;
    protected static WireMockServer wireMockProductCatalog;

    protected void beforeEach() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        RestAssured.config()
                .jsonConfig(
                        jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL));
    }

    protected RequestSpecification givenAuthenticatedRequest(String tokenValue) {
        return RestAssured.given()
                .header(HttpHeaders.AUTHORIZATION, String.join(" ", "Bearer", tokenValue));
    }

    protected RequestSpecification givenAuthenticatedRequest() {
        return givenAuthenticatedRequest(MockJwtFactory.DEFAULT_TOKEN_VALUE);
    }

    protected RequestSpecification givenAuthenticatedWithExpiredTokenRequest() {
        return givenAuthenticatedRequest(MockJwtFactory.EXPIRED_TOKEN_VALUE);
    }

    protected RequestSpecification givenAuthenticatedWithNoScopeTokenRequest() {
        return givenAuthenticatedRequest(MockJwtFactory.NO_SCOPE_TOKEN_VALUE);
    }

    protected static void initWireMock() {
        wireMockRapidex =
                new WireMockServer(
                        options()
                                .port(8780)
                                .templatingEnabled(true)
                                .usingFilesUnderDirectory("src/test/resources/wiremock/rapidex"));

        wireMockProductCatalog =
                new WireMockServer(
                        options()
                                .port(8781)
                                .templatingEnabled(true)
                                .usingFilesUnderDirectory(
                                        "src/test/resources/wiremock/product-catalog"));

        wireMockRapidex.start();
        wireMockProductCatalog.start();
    }

    protected static void stopWireMock() {
        wireMockRapidex.stop();
        wireMockProductCatalog.stop();
    }
}