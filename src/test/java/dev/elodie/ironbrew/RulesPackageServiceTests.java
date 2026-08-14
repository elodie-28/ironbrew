package dev.elodie.ironbrew;

import dev.elodie.ironbrew.exception.ResourceNotFoundException;
import dev.elodie.ironbrew.rulespackage.*;
import dev.elodie.ironbrew.security.user.User;
import dev.elodie.ironbrew.security.user.UserService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RulesPackageServiceTests {
    @Mock
    private RulesPackageRepository rulesPackageRepository;

    @Mock
    private RulesPackageMapper rulesPackageMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private RulesPackageService rulesPackageService;

    @Nested
    class GetExpansionDtosByOwnerEmailTests {
        @Test
        void shouldReturnPageOfExpansionResponses() {
            String email = "jane.doe@example.com";
            Pageable pageable = PageRequest.of(0, 10);
            ExpansionResponse mockResponse = mock(ExpansionResponse.class);
            Page<ExpansionResponse> expectedPage = new PageImpl<>(List.of(mockResponse));

            when(rulesPackageRepository.findExpansionDtosByOwnerEmail(email, pageable))
                    .thenReturn(expectedPage);

            Page<ExpansionResponse> actualPage = rulesPackageService.getExpansionDtosByOwnerEmail(email, pageable);

            assertThat(actualPage).isNotNull().isEqualTo(expectedPage);
            assertThat(actualPage.getTotalElements()).isEqualTo(expectedPage.getTotalElements());
            verify(rulesPackageRepository).findExpansionDtosByOwnerEmail(email, pageable);
        }
    }

    @Nested
    class GetExpansionResponseByIdTests {
        @Test
        void shouldReturnExpansionResponseWhenFound() {
            UUID id = UUID.randomUUID();
            ExpansionResponse mockResponse = mock(ExpansionResponse.class);

            when(rulesPackageRepository.findExpansionResponseById(id))
                    .thenReturn(Optional.of(mockResponse));

            ExpansionResponse result = rulesPackageService.getExpansionResponseById(id);

            assertThat(result).isNotNull().isEqualTo(mockResponse);
            verify(rulesPackageRepository).findExpansionResponseById(id);
        }

        @Test
        void shouldThrowExceptionWhenNotFound() {
            UUID id = UUID.randomUUID();
            when(rulesPackageRepository.findExpansionResponseById(id))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> rulesPackageService.getExpansionResponseById(id))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(rulesPackageRepository).findExpansionResponseById(id);
        }
    }

    @Nested
    class DeleteByIdTests {
        @Test
        void shouldCallRepositoryDelete() {
            UUID id = UUID.randomUUID();

            rulesPackageService.deleteById(id);

            verify(rulesPackageRepository).deleteById(id);
        }
    }

    @Nested
    class CreateTests {
        @Test
        void shouldCreateAndReturnGeneratedId() {
            UUID expectedId = UUID.randomUUID();
            String ownerEmail = "john.doe@example.com";
            ExpansionRequest request = mock(ExpansionRequest.class);

            Expansion expansion = new Expansion();
            expansion.setId(expectedId);

            User owner = new User();

            when(rulesPackageMapper.toExpansion(request)).thenReturn(expansion);
            when(userService.findByEmail(ownerEmail)).thenReturn(owner);

            UUID resultId = rulesPackageService.create(request, ownerEmail);

            assertThat(resultId).isEqualTo(expectedId);
            assertThat(expansion.getOwner()).isEqualTo(owner);

            verify(rulesPackageMapper).toExpansion(request);
            verify(userService).findByEmail(ownerEmail);
            verify(rulesPackageRepository).save(expansion);
        }
    }

    @Nested
    class UpdateTests {
        @Test
        void shouldUpdateExistingExpansion() {
            UUID id = UUID.randomUUID();
            ExpansionRequest request = mock(ExpansionRequest.class);
            Expansion existingExpansion = new Expansion();

            when(rulesPackageRepository.findExpansionById(id))
                    .thenReturn(Optional.of(existingExpansion));

            rulesPackageService.update(id, request);

            verify(rulesPackageRepository).findExpansionById(id);
            verify(rulesPackageMapper).update(existingExpansion, request);
            verify(rulesPackageRepository).save(existingExpansion);
        }

        @Test
        void shouldThrowExceptionWhenUpdatingNonExistentExpansion() {
            UUID id = UUID.randomUUID();
            ExpansionRequest request = mock(ExpansionRequest.class);

            when(rulesPackageRepository.findExpansionById(id))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> rulesPackageService.update(id, request))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(rulesPackageRepository).findExpansionById(id);
            verifyNoInteractions(rulesPackageMapper);
            verify(rulesPackageRepository, never()).save(any());
        }
    }

    @Nested
    class GetExpansionRequestByIdTests {
        @Test
        void shouldReturnExpansionRequestWhenFound() {
            UUID id = UUID.randomUUID();
            ExpansionRequest mockRequest = mock(ExpansionRequest.class);

            when(rulesPackageRepository.findExpansionRequestById(id))
                    .thenReturn(Optional.of(mockRequest));

            ExpansionRequest result = rulesPackageService.getExpansionRequestById(id);

            assertThat(result).isNotNull().isEqualTo(mockRequest);
            verify(rulesPackageRepository).findExpansionRequestById(id);
        }

        @Test
        void shouldThrowExceptionWhenNotFound() {
            UUID id = UUID.randomUUID();
            when(rulesPackageRepository.findExpansionRequestById(id))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> rulesPackageService.getExpansionRequestById(id))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(rulesPackageRepository).findExpansionRequestById(id);
        }
    }
}
