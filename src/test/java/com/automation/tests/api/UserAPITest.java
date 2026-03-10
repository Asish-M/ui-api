package com.automation.tests.api;

import com.automation.api.BaseAPITest;
import com.automation.api.endpoints.UserAPI;
import com.automation.listeners.RetryAnalyzer;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * API tests for User endpoints (reqres.in).
 */
@Epic("API Tests")
@Feature("User API")
public class UserAPITest extends BaseAPITest {

    private UserAPI userAPI;

    @BeforeClass
    public void initAPI() {
        userAPI = new UserAPI();
    }

    @Test(description = "GET /users — verify users list",
            retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.CRITICAL)
    @Story("List Users")
    public void testGetUsers() {
        Response response = userAPI.getUsers(1);

        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");
        Assert.assertNotNull(response.jsonPath().getList("data"), "Data list should not be null");
        Assert.assertTrue(response.jsonPath().getList("data").size() > 0,
                "Users list should not be empty");
        Assert.assertEquals(response.jsonPath().getInt("page"), 1, "Page should be 1");
    }

    @Test(description = "GET /users/{id} — verify single user",
            retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.CRITICAL)
    @Story("Get User By ID")
    public void testGetUserById() {
        Response response = userAPI.getUserById(2);

        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");
        Assert.assertEquals(response.jsonPath().getInt("data.id"), 2, "User ID should be 2");
        Assert.assertNotNull(response.jsonPath().getString("data.email"),
                "Email should not be null");
    }

    @Test(description = "POST /users — create a new user",
            retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.CRITICAL)
    @Story("Create User")
    public void testCreateUser() {
        Response response = userAPI.createUser("John Doe", "SDET Lead");

        Assert.assertEquals(response.getStatusCode(), 201, "Status code should be 201");
        Assert.assertEquals(response.jsonPath().getString("name"), "John Doe",
                "Name should match");
        Assert.assertEquals(response.jsonPath().getString("job"), "SDET Lead",
                "Job should match");
        Assert.assertNotNull(response.jsonPath().getString("id"),
                "Created user should have an ID");
        Assert.assertNotNull(response.jsonPath().getString("createdAt"),
                "Created timestamp should be present");
    }

    @Test(description = "PUT /users/{id} — update an existing user",
            retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Story("Update User")
    public void testUpdateUser() {
        Response response = userAPI.updateUser(2, "Jane Doe", "QA Manager");

        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");
        Assert.assertEquals(response.jsonPath().getString("name"), "Jane Doe",
                "Updated name should match");
        Assert.assertEquals(response.jsonPath().getString("job"), "QA Manager",
                "Updated job should match");
    }

    @Test(description = "DELETE /users/{id} — delete a user",
            retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Story("Delete User")
    public void testDeleteUser() {
        Response response = userAPI.deleteUser(2);

        Assert.assertEquals(response.getStatusCode(), 204,
                "Status code should be 204 No Content");
    }

    @Test(description = "GET /users/{id} — verify 404 for non-existent user")
    @Severity(SeverityLevel.MINOR)
    @Story("User Not Found")
    public void testUserNotFound() {
        Response response = userAPI.getUserById(9999);

        Assert.assertEquals(response.getStatusCode(), 404,
                "Status code should be 404 for non-existent user");
    }
}
