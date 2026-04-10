package com.hisabnikash.erp.identityaccess.auth.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void loginCanBeRepeatedAndUsersEndpointAcceptsUiQueryParameters() throws Exception {
        String firstToken = loginAndExtractAccessToken();
        String secondToken = loginAndExtractAccessToken();

        mockMvc.perform(get("/api/v1/users")
                        .queryParam("page", "1")
                        .queryParam("limit", "10")
                        .queryParam("sort", "displayName")
                        .queryParam("order", "asc")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + firstToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].username").value("admin"))
                .andExpect(jsonPath("$.data[0].createdBy").isNotEmpty())
                .andExpect(jsonPath("$.data[0].createdAt").isNotEmpty())
                .andExpect(jsonPath("$.data[0].lastUpdatedBy").isNotEmpty())
                .andExpect(jsonPath("$.data[0].lastUpdatedAt").isNotEmpty());

        mockMvc.perform(get("/api/v1/users")
                        .queryParam("page", "1")
                        .queryParam("limit", "10")
                        .queryParam("sort", "displayName")
                        .queryParam("order", "asc")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + secondToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].username").value("admin"));
    }

    @Test
    void userCanBeUpdatedThroughDedicatedEndpoint() throws Exception {
        String token = loginAndExtractAccessToken();
        JsonNode user = getFirstUser(token);
        String userId = user.path("id").asText();

        var request = objectMapper.createObjectNode();
        request.put("username", user.path("username").asText());
        request.put("email", user.path("email").asText());
        request.put("displayName", "Platform Admin Updated");
        request.put("tenantId", user.path("tenantId").asText());
        var roleIds = request.putArray("roleIds");
        user.path("roles").forEach(role -> roleIds.add(role.path("id").asText()));

        mockMvc.perform(put("/api/v1/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(userId))
                .andExpect(jsonPath("$.data.username").value("admin"))
                .andExpect(jsonPath("$.data.displayName").value("Platform Admin Updated"))
                .andExpect(jsonPath("$.data.createdBy").isNotEmpty())
                .andExpect(jsonPath("$.data.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.data.lastUpdatedBy").isNotEmpty())
                .andExpect(jsonPath("$.data.lastUpdatedAt").isNotEmpty());

        mockMvc.perform(get("/api/v1/users/{id}", userId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.displayName").value("Platform Admin Updated"));
    }

    private String loginAndExtractAccessToken() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "identifier": "admin",
                                  "password": "Admin@12345",
                                  "tenantId": "ERP-DEFAULT"
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString());
        return response.path("data").path("accessToken").asText();
    }

    private JsonNode getFirstUser(String token) throws Exception {
        MvcResult result = mockMvc.perform(get("/api/v1/users")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString());
        return response.path("data").get(0);
    }
}
