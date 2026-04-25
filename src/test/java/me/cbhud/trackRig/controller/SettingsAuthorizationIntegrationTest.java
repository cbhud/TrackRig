package me.cbhud.trackRig.controller;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Settings Endpoint Authorization")
class SettingsAuthorizationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void employeeCannotCreateComponentCategory() throws Exception {
        mockMvc.perform(post("/components/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "Graphics Cards",
                                "description", "GPU components"
                        ))))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void managerCanCreateComponentCategory() throws Exception {
        mockMvc.perform(post("/components/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "Power Supplies",
                                "description", "PSU units"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Power Supplies"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void employeeCannotCreateComponentStatus() throws Exception {
        mockMvc.perform(post("/components/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "Bench Stock"
                        ))))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "OWNER")
    void ownerCanCreateComponentStatus() throws Exception {
        mockMvc.perform(post("/components/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "Bench Stock"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Bench Stock"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void employeeCannotCreateWorkstationStatus() throws Exception {
        mockMvc.perform(post("/workstations/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "Deploying",
                                "color", "#22c55e"
                        ))))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "OWNER")
    void ownerCanCreateWorkstationStatus() throws Exception {
        mockMvc.perform(post("/workstations/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "Servicing",
                                "color", "#f59e0b"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Servicing"))
                .andExpect(jsonPath("$.color").value("#f59e0b"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void employeeCannotCreateMaintenanceType() throws Exception {
        mockMvc.perform(post("/maintenance/types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "Deep Cleaning",
                                "description", "Quarterly maintenance",
                                "intervalDays", 90,
                                "isActive", true
                        ))))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void managerCanCreateMaintenanceType() throws Exception {
        mockMvc.perform(post("/maintenance/types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "Cable Inspection",
                                "description", "Check cable wear",
                                "intervalDays", 30,
                                "isActive", true
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Cable Inspection"))
                .andExpect(jsonPath("$.intervalDays").value(30));
    }
}
