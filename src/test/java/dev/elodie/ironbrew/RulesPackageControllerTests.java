package dev.elodie.ironbrew;

import dev.elodie.ironbrew.rulespackage.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RulesPackageController.class)
class RulesPackageControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RulesPackageService rulesPackageService;

    @Nested
    @DisplayName("GET /your-expansions")
    class ListExpansions {
        @Test
        void shouldReturnIndexViewWithPageData() throws Exception {
            int page = 1;
            String userEmail = "john.doe@example.com";
            Pageable pageable = PageRequest.of(0, 10, Sort.by("releaseDate").descending());

            ExpansionResponse mockResponse = mock(ExpansionResponse.class);
            Page<ExpansionResponse> resultPage = new PageImpl<>(List.of(mockResponse), pageable, 1);

            when(rulesPackageService.getExpansionDtosByOwnerEmail(userEmail, pageable))
                    .thenReturn(resultPage);

            mockMvc.perform(get("/your-expansions")
                            .param("page", String.valueOf(page))
                            .principal(() -> userEmail))
                    .andExpect(status().isOk())
                    .andExpect(view().name("expansions/index"))
                    .andExpect(model().attributeExists("expansions", "currentPage", "totalPages"))
                    .andExpect(model().attribute("currentPage", page))
                    .andExpect(model().attribute("totalPages", 1));

            verify(rulesPackageService).getExpansionDtosByOwnerEmail(userEmail, pageable);
        }
    }

    @Nested
    @DisplayName("GET /expansions/{id}")
    class ViewExpansion {
        @Test
        void shouldReturnViewPageWhenFound() throws Exception {
            UUID id = UUID.randomUUID();
            ExpansionResponse mockResponse = mock(ExpansionResponse.class);

            when(rulesPackageService.getExpansionResponseById(id)).thenReturn(mockResponse);

            mockMvc.perform(get("/expansions/{id}", id)
                            .principal(() -> "john.doe@example.com"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("expansions/view"))
                    .andExpect(model().attribute("expansion", mockResponse));

            verify(rulesPackageService).getExpansionResponseById(id);
        }
    }

    @Nested
    @DisplayName("POST /expansions/{id}/delete")
    class DeleteExpansion {
        @Test
        @WithMockUser
        void shouldDeleteAndRedirectWithFlashAttribute() throws Exception {
            UUID id = UUID.randomUUID();

            mockMvc.perform(post("/expansions/{id}/delete", id)
                            .with(csrf())) // CSRF token required for Spring Security POST
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/your-expansions"))
                    .andExpect(flash().attribute("successDeleteMessage", "Expansion deleted successfully"));

            verify(rulesPackageService).deleteById(id);
        }
    }

    @Nested
    @DisplayName("GET /expansions/{id}/edit")
    class EditExpansionForm {
        @Test
        void shouldReturnFormViewWithEditMode() throws Exception {
            UUID id = UUID.randomUUID();
            ExpansionUpdateRequest mockRequest = mock(ExpansionUpdateRequest.class);

            when(rulesPackageService.getExpansionRequestById(id)).thenReturn(mockRequest);

            mockMvc.perform(get("/expansions/{id}/edit", id)
                            .principal(() -> "john.doe@example.com"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("expansions/form"))
                    .andExpect(model().attribute("expansion", mockRequest))
                    .andExpect(model().attribute("isEdit", true))
                    .andExpect(model().attribute("id", id));

            verify(rulesPackageService).getExpansionRequestById(id);
        }
    }

    @Nested
    @DisplayName("POST /expansions/{id}")
    class SaveExpansion {
        @Test
        void shouldRedirectWhenValidationSucceeds() throws Exception {
            UUID id = UUID.randomUUID();

            mockMvc.perform(post("/expansions/{id}", id)
                            .principal(() -> "john.doe@example.com")
                            .with(csrf())
                            .param("title", "Title")
                            .param("releaseDate", "2026-01-01")
                            .param("url", "http://example.com"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/expansions/" + id));

            verify(rulesPackageService).update(eq(id), any(ExpansionUpdateRequest.class));
        }

        @Test
        void shouldReturnFormViewWhenValidationFails() throws Exception {
            UUID id = UUID.randomUUID();

            mockMvc.perform(post("/expansions/{id}", id)
                            .principal(() -> "john.doe@example.com")
                            .with(csrf())
                            .param("title", ""))
                    .andExpect(status().isOk())
                    .andExpect(view().name("expansions/form"))
                    .andExpect(model().attribute("isEdit", true))
                    .andExpect(model().hasErrors());

            verifyNoInteractions(rulesPackageService);
        }
    }

    @Nested
    @DisplayName("GET /expansions/new")
    class NewExpansionForm {
        @Test
        void shouldReturnFormViewWithCreateMode() throws Exception {
            mockMvc.perform(get("/expansions/new")
                            .principal(() -> "john.doe@example.com"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("expansions/form"))
                    .andExpect(model().attributeExists("expansion"))
                    .andExpect(model().attribute("isEdit", false));
        }
    }

    @Nested
    @DisplayName("POST /expansions")
    class CreateExpansion {
        @Test
        void shouldRedirectToCreatedExpansionWhenValid() throws Exception {
            // Given
            UUID generatedId = UUID.randomUUID();
            String userEmail = "john.doe@example.com";

            when(rulesPackageService.create(any(ExpansionCreateRequest.class), eq(userEmail)))
                    .thenReturn(generatedId);

            // When & Then
            mockMvc.perform(post("/expansions")
                            .principal(() -> userEmail)
                            .with(csrf())
                            .param("title", "Title")
                            .param("releaseDate", "2026-01-01")
                            .param("url", "http://example.com")
                            .param("ruleset", "ironsworn"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/expansions/" + generatedId));

            verify(rulesPackageService).create(any(ExpansionCreateRequest.class), eq(userEmail));
        }

        @Test
        void shouldReturnFormViewWhenCreateValidationFails() throws Exception {
            mockMvc.perform(post("/expansions")
                            .with(csrf())
                            .param("title", ""))
                    .andExpect(status().isOk())
                    .andExpect(view().name("expansions/form"))
                    .andExpect(model().attribute("isEdit", false))
                    .andExpect(model().hasErrors());

            verifyNoInteractions(rulesPackageService);
        }
    }
}
