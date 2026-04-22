package com.hisabnikash.erp.identityaccess.authorization.api;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthorizationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void authzCatalogAndChecksExposeSeededPermissionModel() throws Exception {
        String token = loginAndExtractAccessToken();

        mockMvc.perform(get("/api/v1/authz/permissions")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].permissionKey").isNotEmpty())
                .andExpect(jsonPath("$.data[?(@.permissionKey=='IAM.USER.LIST#VIEW')]").exists())
                .andExpect(jsonPath("$.data[?(@.permissionKey=='IAM.USER.LIST#VIEW')].aliases[0]").value("identity:user:view"));

        mockMvc.perform(post("/api/v1/authz/effective-permissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.permissions[?(@=='IAM.USER.LIST#VIEW')]").exists());

        mockMvc.perform(post("/api/v1/authz/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "resourceCode": "IAM.USER.LIST",
                                  "action": "VIEW"
                                }
                                """)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.allowed").value(true))
                .andExpect(jsonPath("$.data.permissionKey").value("IAM.USER.LIST#VIEW"));
    }

    private String loginAndExtractAccessToken() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "identifier": "admin",
                                  "password": "ChangeMe123!",
                                  "tenantId": "ERP-DEFAULT"
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString());
        return response.path("data").path("accessToken").asText();
    }
}
