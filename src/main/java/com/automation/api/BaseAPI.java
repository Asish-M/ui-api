package com.automation.api;

import com.automation.config.ConfigManager;
import com.automation.utils.LoggerUtil;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * Base API class providing a configured {@link RequestSpecification}
 * and generic HTTP methods with Allure logging.
 */
public class BaseAPI {

    protected static final Logger LOG = LoggerUtil.getLogger(BaseAPI.class);
    protected final String baseUri;

    public BaseAPI() {
        this.baseUri = ConfigManager.getInstance().getApiBaseUrl();
    }

    /**
     * Build a preconfigured request specification.
     */
    protected RequestSpecification request() {
        return RestAssured.given()
                .baseUri(baseUri)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .filter(new AllureRestAssured())  // attaches request/response to Allure
                .log().all();
    }

    // ── Generic HTTP methods ──────────────────────────────────────

    public Response get(String endpoint) {
        LOG.info("GET {}{}", baseUri, endpoint);
        return request().get(endpoint).then().log().all().extract().response();
    }

    public Response get(String endpoint, Map<String, ?> queryParams) {
        LOG.info("GET {}{} with params: {}", baseUri, endpoint, queryParams);
        return request().queryParams(queryParams)
                .get(endpoint).then().log().all().extract().response();
    }

    public Response post(String endpoint, Object body) {
        LOG.info("POST {}{}", baseUri, endpoint);
        return request().body(body)
                .post(endpoint).then().log().all().extract().response();
    }

    public Response put(String endpoint, Object body) {
        LOG.info("PUT {}{}", baseUri, endpoint);
        return request().body(body)
                .put(endpoint).then().log().all().extract().response();
    }

    public Response patch(String endpoint, Object body) {
        LOG.info("PATCH {}{}", baseUri, endpoint);
        return request().body(body)
                .patch(endpoint).then().log().all().extract().response();
    }

    public Response delete(String endpoint) {
        LOG.info("DELETE {}{}", baseUri, endpoint);
        return request().delete(endpoint).then().log().all().extract().response();
    }
}
