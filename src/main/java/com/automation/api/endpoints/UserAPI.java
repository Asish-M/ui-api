package com.automation.api.endpoints;

import com.automation.api.BaseAPI;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.Map;

/**
 * API endpoints for User operations targeting reqres.in.
 */
public class UserAPI extends BaseAPI {

    private static final String USERS_ENDPOINT = "/users";

    @Step("GET all users — page {page}")
    public Response getUsers(int page) {
        return get(USERS_ENDPOINT, Map.of("page", page));
    }

    @Step("GET all users — default page")
    public Response getUsers() {
        return get(USERS_ENDPOINT);
    }

    @Step("GET user by ID: {userId}")
    public Response getUserById(int userId) {
        return get(USERS_ENDPOINT + "/" + userId);
    }

    @Step("POST create user")
    public Response createUser(String name, String job) {
        Map<String, String> body = Map.of("name", name, "job", job);
        return post(USERS_ENDPOINT, body);
    }

    @Step("PUT update user ID: {userId}")
    public Response updateUser(int userId, String name, String job) {
        Map<String, String> body = Map.of("name", name, "job", job);
        return put(USERS_ENDPOINT + "/" + userId, body);
    }

    @Step("DELETE user ID: {userId}")
    public Response deleteUser(int userId) {
        return delete(USERS_ENDPOINT + "/" + userId);
    }
}
