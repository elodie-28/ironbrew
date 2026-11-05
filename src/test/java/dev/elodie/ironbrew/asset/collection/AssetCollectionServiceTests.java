package dev.elodie.ironbrew.asset.collection;

import dev.elodie.ironbrew.exception.NameAlreadyTakenException;
import dev.elodie.ironbrew.exception.ResourceNotFoundException;
import dev.elodie.ironbrew.rulespackage.RulesPackage;
import dev.elodie.ironbrew.rulespackage.RulesPackageRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AssetCollectionServiceTests {
    @Mock
    private AssetCollectionRepository assetCollectionRepository;

    @Mock
    private AssetCollectionMapper assetCollectionMapper;

    @Mock
    private RulesPackageRepository rulesPackageRepository;

    @InjectMocks
    private AssetCollectionService assetCollectionService;

    @Nested
    class CreateTests {
        @Test
        void shouldSucceed() {
            RulesPackage rulesPackage = mock(RulesPackage.class);
            AssetCollectionRequest request = mock(AssetCollectionRequest.class);
            AssetCollection assetCollection = new AssetCollection();
            UUID rulesPackageId = UUID.randomUUID();
            UUID assetCollectionId = UUID.randomUUID();
            assetCollection.setId(assetCollectionId);

            when(assetCollectionMapper.toAssetCollection(request)).thenReturn(assetCollection);
            when(rulesPackageRepository.getReferenceById(rulesPackageId)).thenReturn(rulesPackage);
            when(assetCollectionRepository.save(assetCollection)).thenReturn(assetCollection);

            UUID resultId = assetCollectionService.create(request, rulesPackageId);

            assertThat(resultId).isEqualTo(assetCollectionId);

            verify(rulesPackageRepository).getReferenceById(rulesPackageId);
            verify(assetCollectionRepository).save(assetCollection);
        }

        @Test
        void shouldRaiseNameAlreadyTakenException() {
            RulesPackage rulesPackage = mock(RulesPackage.class);
            AssetCollectionRequest request = mock(AssetCollectionRequest.class);
            AssetCollection assetCollection = new AssetCollection();
            UUID rulesPackageId = UUID.randomUUID();
            UUID assetCollectionId = UUID.randomUUID();
            assetCollection.setId(assetCollectionId);

            when(assetCollectionMapper.toAssetCollection(request)).thenReturn(assetCollection);
            when(rulesPackageRepository.getReferenceById(rulesPackageId)).thenReturn(rulesPackage);

            DataIntegrityViolationException exception = new DataIntegrityViolationException(AssetCollection.UNIQUE_NAME_CONSTRAINT);
            when(assetCollectionRepository.save(assetCollection)).thenThrow(exception);

            assertThatThrownBy(() -> assetCollectionService.create(request, rulesPackageId))
                    .isInstanceOf(NameAlreadyTakenException.class);
        }
    }

    @Nested
    class FindAllAssetCollectionResponseByRulesPackageIdTests {
        @Test
        void shouldSucceed() {
            AssetCollectionResponse response = mock(AssetCollectionResponse.class);
            List<AssetCollectionResponse> expectedResponses = List.of(response);
            Sort expectedSort = Sort.by(Sort.Direction.ASC, "name");
            UUID rulesPackageId = UUID.randomUUID();

            when(assetCollectionRepository.findAllAssetCollectionResponseByRulesPackageId(rulesPackageId, expectedSort))
                    .thenReturn(expectedResponses);

            List<AssetCollectionResponse> actualResponses = assetCollectionService.findAllAssetCollectionResponseByRulesPackageId(rulesPackageId);

            assertThat(actualResponses).isEqualTo(expectedResponses);
            verify(assetCollectionRepository).findAllAssetCollectionResponseByRulesPackageId(rulesPackageId, expectedSort);
        }
    }

    @Nested
    class FindAssetCollectionResponseByIdTests {
        @Test
        void shouldSucceed() {
            AssetCollectionResponse expectedResponse = mock(AssetCollectionResponse.class);
            UUID assetCollectionId = UUID.randomUUID();

            when(assetCollectionRepository.findAssetCollectionResponseById(assetCollectionId))
                    .thenReturn(Optional.of(expectedResponse));

            AssetCollectionResponse actualResponse = assetCollectionService.findAssetCollectionResponseById(assetCollectionId);

            assertThat(actualResponse).isEqualTo(expectedResponse);
        }

        @Test
        void shouldRaiseResourceNotFoundException() {
            UUID assetCollectionId = UUID.randomUUID();

            when(assetCollectionRepository.findAssetCollectionResponseById(assetCollectionId))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> assetCollectionService.findAssetCollectionResponseById(assetCollectionId))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    class FindRulesPackageIdByIdTests {
        @Test
        void shouldSucceed() {
            UUID rulesPackageId = UUID.randomUUID();
            UUID assetCollectionId = UUID.randomUUID();

            when(assetCollectionRepository.findRulesPackageIdById(assetCollectionId)).thenReturn(rulesPackageId);

            UUID actualRulesPackageId = assetCollectionService.findRulesPackageIdById(assetCollectionId);

            assertThat(actualRulesPackageId).isEqualTo(rulesPackageId);
        }
    }

    @Nested
    class FindAssetCollectionRequestByIdTests {
        @Test
        void shouldSucceed() {
            UUID assetCollectionId = UUID.randomUUID();
            AssetCollectionRequest request = mock(AssetCollectionRequest.class);

            when(assetCollectionRepository.findAssetCollectionRequestById(assetCollectionId))
                    .thenReturn(Optional.of(request));

            AssetCollectionRequest actualRequest = assetCollectionService.findAssetCollectionRequestById(assetCollectionId);

            assertThat(actualRequest).isEqualTo(request);
        }

        @Test
        void shouldRaiseResourceNotFoundException() {
            UUID assetCollectionId = UUID.randomUUID();

            when(assetCollectionRepository.findAssetCollectionRequestById(assetCollectionId))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> assetCollectionService.findAssetCollectionRequestById(assetCollectionId))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    class UpdateTests {
        @Test
        void shouldSucceed() {
            UUID assetCollectionId = UUID.randomUUID();
            AssetCollection assetCollection = mock(AssetCollection.class);
            AssetCollectionRequest request = mock(AssetCollectionRequest.class);

            when(assetCollectionRepository.findById(assetCollectionId)).thenReturn(Optional.of(assetCollection));
            when(assetCollectionRepository.save(assetCollection)).thenReturn(assetCollection);

            assetCollectionService.update(request, assetCollectionId);

            verify(assetCollectionMapper).update(assetCollection, request);
            verify(assetCollectionRepository).save(assetCollection);
        }

        @Test
        void shouldRaiseResourceNotFoundException() {
            UUID assetCollectionId = UUID.randomUUID();
            AssetCollectionRequest request = mock(AssetCollectionRequest.class);

            when(assetCollectionRepository.findById(assetCollectionId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> assetCollectionService.update(request, assetCollectionId))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(assetCollectionRepository, never()).save(any());
        }

        @Test
        void shouldRaiseNameAlreadyTakenException() {
            UUID assetCollectionId = UUID.randomUUID();
            AssetCollection assetCollection = mock(AssetCollection.class);
            AssetCollectionRequest request = mock(AssetCollectionRequest.class);

            when(assetCollectionRepository.findById(assetCollectionId)).thenReturn(Optional.of(assetCollection));

            DataIntegrityViolationException exception = new DataIntegrityViolationException(AssetCollection.UNIQUE_NAME_CONSTRAINT);
            when(assetCollectionRepository.save(assetCollection)).thenThrow(exception);

            assertThatThrownBy(() -> assetCollectionService.update(request, assetCollectionId))
                    .isInstanceOf(NameAlreadyTakenException.class);
        }
    }

    @Nested
    class DeleteByIdTests {
        @Test
        void shouldSucceed() {
            UUID assetCollectionId = UUID.randomUUID();

            doNothing().when(assetCollectionRepository).deleteById(assetCollectionId);

            assetCollectionService.deleteById(assetCollectionId);

            verify(assetCollectionRepository).deleteById(assetCollectionId);
        }
    }
}
