package dev.justine.payroll.employee;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import dev.justine.payroll.config.AppProperties;
import dev.justine.payroll.config.SecurityConfig;
import dev.justine.payroll.employee.EmployeeDtos.EmployeeResponse;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/** Web slice: controller + advice + the real security rules. The service and token decoder are mocks. */
@WebMvcTest(EmployeeController.class)
@Import(SecurityConfig.class)
@EnableConfigurationProperties(AppProperties.class)
@TestPropertySource(properties = "app.cors-allowed-origins=http://localhost:5173")
class EmployeeControllerTest {

    @Autowired MockMvc mvc;
    @MockitoBean EmployeeService service;
    @MockitoBean JwtDecoder jwtDecoder;   // the real one would try to reach Keycloak

    private static RequestPostProcessor as(String username, String role) {
        return jwt().jwt(j -> j.claim("preferred_username", username)).authorities(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Test
    void anonymousIsRejected() throws Exception {
        mvc.perform(get("/api/employees/1")).andExpect(status().isUnauthorized());
    }

    @Test
    void plainEmployeeIsForbidden() throws Exception {
        mvc.perform(get("/api/employees/1").with(as("ana", "EMPLOYEE"))).andExpect(status().isForbidden());
    }

    @Test
    void hrCanReadAnEmployee() throws Exception {
        when(service.get(1L)).thenReturn(new EmployeeResponse(1L, "E-0001", "Ana Santos", "ana@demo.ph", "ana",
            EmploymentType.REGULAR, new BigDecimal("30000"), 3L, "Engineering", true, 0));

        mvc.perform(get("/api/employees/1").with(as("hana", "HR")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.fullName").value("Ana Santos"))
            .andExpect(jsonPath("$.departmentName").value("Engineering"));
    }

    @Test
    void payrollAdminCanReadButNotCreate() throws Exception {
        when(service.departments()).thenReturn(List.of());
        mvc.perform(get("/api/departments").with(as("paolo", "PAYROLL_ADMIN"))).andExpect(status().isOk());
        mvc.perform(post("/api/employees").with(as("paolo", "PAYROLL_ADMIN"))
                .contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isForbidden());
    }

    @Test
    void invalidBodyReturnsFieldErrors() throws Exception {
        mvc.perform(post("/api/employees").with(as("hana", "HR"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"employeeNo":"X1","fullName":"","email":"nope","employmentType":"REGULAR","baseSalary":-5,"departmentId":1}
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("error.validation"))
            .andExpect(jsonPath("$.errors.employeeNo").exists())
            .andExpect(jsonPath("$.errors.email").exists())
            .andExpect(jsonPath("$.errors.baseSalary").exists())
            .andExpect(header().exists("X-Correlation-Id"));
    }

    @Test
    void createReturns201WithLocation() throws Exception {
        when(service.create(any())).thenReturn(new EmployeeResponse(61L, "E-0100", "Lia Tan", "lia@demo.ph", null,
            EmploymentType.REGULAR, new BigDecimal("38000"), 2L, "Finance", true, 0));

        mvc.perform(post("/api/employees").with(as("hana", "HR"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"employeeNo":"E-0100","fullName":"Lia Tan","email":"lia@demo.ph","employmentType":"REGULAR","baseSalary":38000,"departmentId":2}
                    """))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "/api/employees/61"));
    }

}
