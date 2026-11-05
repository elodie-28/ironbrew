package dev.elodie.ironbrew.asset.collection;

import dev.elodie.ironbrew.exception.NameAlreadyTakenException;
import dev.elodie.ironbrew.rulespackage.RulesPackageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
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

@WebMvcTest(AssetCollectionController.class)
public class AssetCollectionControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AssetCollectionService assetCollectionService;

    @MockitoBean
    private RulesPackageService  rulesPackageService;

    @Nested
    @DisplayName("GET /expansions/{id}/asset-collections")
    class ListTests {
        @Test
        void shouldSucceed() throws Exception {
            AssetCollectionResponse response = mock(AssetCollectionResponse.class);
            List<AssetCollectionResponse> responses = List.of(response);
            UUID expansionId = UUID.randomUUID();

            when(assetCollectionService.findAllAssetCollectionResponseByRulesPackageId(expansionId)).thenReturn(responses);
            when(rulesPackageService.getExpansionTitle(expansionId)).thenReturn("Expansion");

            mockMvc.perform(get("/expansions/{id}/asset-collections", expansionId))
                    .andExpect(status().isOk())
                    .andExpect(view().name("assetcollections/index"))
                    .andExpect(model().attribute("assetCollections", responses))
                    .andExpect(model().attribute("expansionTitle", "Expansion"))
                    .andExpect(model().attribute("expansionId", expansionId));
        }
    }

    @Nested
    @DisplayName("GET /expansions/{id}/asset-collections/new")
    class NewAssetCollectionTests {
        @Test
        void shouldSucceed() throws Exception {
            UUID expansionId = UUID.randomUUID();

            when(rulesPackageService.getExpansionTitle(expansionId)).thenReturn("Core Expansion");
            when(rulesPackageService.getRuleset(expansionId)).thenReturn("classic");

            mockMvc.perform(get("/expansions/{id}/asset-collections/new", expansionId))
                    .andExpect(status().isOk())
                    .andExpect(view().name("assetcollections/form"))
                    .andExpect(model().attribute("isEdit", false))
                    .andExpect(model().attribute("expansionTitle", "Core Expansion"));
        }
    }

    @Nested
    @DisplayName("POST /expansions/{id}/asset-collections")
    class CreateTests {
        @Test
        void shouldSucceed() throws Exception {
            UUID expansionId = UUID.randomUUID();
            UUID assetCollectionId = UUID.randomUUID();

            when(assetCollectionService.create(any(AssetCollectionRequest.class), eq(expansionId))).thenReturn(assetCollectionId);

            mockMvc.perform(post("/expansions/{id}/asset-collections", expansionId)
                            .with(csrf())
                            .param("name", "New Collection"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/asset-collections/" + assetCollectionId));
        }

        @Test
        void shouldFailValidation() throws Exception {
            UUID expansionId = UUID.randomUUID();

            when(rulesPackageService.getExpansionTitle(expansionId)).thenReturn("Core Expansion");
            when(rulesPackageService.getRuleset(expansionId)).thenReturn("classic");

            mockMvc.perform(post("/expansions/{id}/asset-collections", expansionId)
                            .with(csrf())
                            .param("name", ""))
                    .andExpect(status().isOk())
                    .andExpect(view().name("assetcollections/form"))
                    .andExpect(model().attribute("isEdit", false));

            verify(assetCollectionService, never()).create(any(), any());
        }

        @Test
        void shouldRaiseNameAlreadyTakenException() throws Exception {
            UUID expansionId = UUID.randomUUID();

            when(assetCollectionService.create(any(AssetCollectionRequest.class), eq(expansionId)))
                    .thenThrow(new NameAlreadyTakenException());
            when(rulesPackageService.getExpansionTitle(expansionId)).thenReturn("Core Expansion");
            when(rulesPackageService.getRuleset(expansionId)).thenReturn("classic");

            mockMvc.perform(post("/expansions/{id}/asset-collections", expansionId)
                            .with(csrf())
                            .param("name", "Existing Name"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("assetcollections/form"))
                    .andExpect(model().hasErrors());
        }
    }

    @Nested
    @DisplayName("GET /asset-collections/{id}")
    class ViewTests {
        @Test
        void shouldSucceed() throws Exception {
            AssetCollectionResponse response = mock(AssetCollectionResponse.class);
            UUID assetCollectionId = UUID.randomUUID();
            UUID expansionId = UUID.randomUUID();

            when(assetCollectionService.findAssetCollectionResponseById(assetCollectionId)).thenReturn(response);
            when(assetCollectionService.findRulesPackageIdById(assetCollectionId)).thenReturn(expansionId);
            when(rulesPackageService.getExpansionTitle(expansionId)).thenReturn("Core Expansion");

            mockMvc.perform(get("/asset-collections/{id}", assetCollectionId))
                    .andExpect(status().isOk())
                    .andExpect(view().name("assetcollections/view"))
                    .andExpect(model().attribute("assetCollection", response))
                    .andExpect(model().attribute("expansionId", expansionId))
                    .andExpect(model().attribute("expansionTitle", "Core Expansion"));
        }
    }

    @Nested
    @DisplayName("GET /asset-collections/{id}/edit")
    class EditTests {
        @Test
        void shouldSucceed() throws Exception {
            AssetCollectionRequest request = mock(AssetCollectionRequest.class);
            UUID assetCollectionId = UUID.randomUUID();
            UUID expansionId = UUID.randomUUID();

            when(assetCollectionService.findAssetCollectionRequestById(assetCollectionId)).thenReturn(request);
            when(assetCollectionService.findRulesPackageIdById(assetCollectionId)).thenReturn(expansionId);
            when(rulesPackageService.getExpansionTitle(expansionId)).thenReturn("Core Expansion");
            when(rulesPackageService.getRuleset(expansionId)).thenReturn("classic");

            mockMvc.perform(get("/asset-collections/{id}/edit", assetCollectionId))
                    .andExpect(status().isOk())
                    .andExpect(view().name("assetcollections/form"))
                    .andExpect(model().attribute("isEdit", true));
        }
    }

    @Nested
    class SaveTests {
        @Test
        void shouldSucceed() throws Exception {
            UUID assetCollectionId = UUID.randomUUID();

            mockMvc.perform(post("/asset-collections/{id}", assetCollectionId)
                            .with(csrf())
                            .param("name", "Updated Collection"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/asset-collections/" + assetCollectionId));

            verify(assetCollectionService).update(any(AssetCollectionRequest.class), eq(assetCollectionId));
        }

        @Test
        void shouldRaiseNameAlreadyTakenException() throws Exception {
            UUID assetCollectionId = UUID.randomUUID();
            UUID expansionId = UUID.randomUUID();

            doThrow(new NameAlreadyTakenException()).when(assetCollectionService).update(any(), eq(assetCollectionId));
            when(assetCollectionService.findRulesPackageIdById(assetCollectionId)).thenReturn(expansionId);
            when(rulesPackageService.getExpansionTitle(expansionId)).thenReturn("Core Expansion");
            when(rulesPackageService.getRuleset(expansionId)).thenReturn("classic");

            mockMvc.perform(post("/asset-collections/{id}", assetCollectionId)
                            .with(csrf())
                            .param("name", "Duplicate Name"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("assetcollections/form"))
                    .andExpect(model().hasErrors());
        }
    }

    @Nested
    class DeleteTests {
        @Test
        void shouldSucceed() throws Exception {
            UUID assetCollectionId = UUID.randomUUID();
            UUID expansionId = UUID.randomUUID();

            when(assetCollectionService.findRulesPackageIdById(assetCollectionId)).thenReturn(expansionId);

            mockMvc.perform(post("/asset-collections/{id}/delete", assetCollectionId)
                            .with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/expansions/" + expansionId + "/asset-collections"));

            verify(assetCollectionService).deleteById(assetCollectionId);
        }
    }
}
