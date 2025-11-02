package com.bugtracker.service;

import com.bugtracker.model.entity.LogEntry;
import com.bugtracker.model.enums.LogLevel;
import com.bugtracker.repository.LogEntryRepository;
import com.bugtracker.service.impl.LogServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogServiceTest {

    @Mock
    private LogEntryRepository logEntryRepository;

    @InjectMocks
    private LogServiceImpl logService;

    private UUID testEntityId;
    private LogEntry testLogEntry;

    @BeforeEach
    void setUp() {
        testEntityId = UUID.randomUUID();
        testLogEntry = LogEntry.builder()
                .id(UUID.randomUUID())
                .action("CREATE")
                .entityType("BUG")
                .entityId(testEntityId)
                .userId(1L)
                .details("Test bug created")
                .level(LogLevel.INFO)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createLogEntry_WithValidData_SavesAndReturnsLogEntry() {
        // Given
        when(logEntryRepository.save(any(LogEntry.class))).thenReturn(testLogEntry);

        // When
        LogEntry result = logService.createLogEntry(
                "CREATE", 
                "BUG", 
                testEntityId, 
                1L, 
                "Test bug created", 
                LogLevel.INFO
        );

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAction()).isEqualTo("CREATE");
        assertThat(result.getEntityType()).isEqualTo("BUG");
        assertThat(result.getEntityId()).isEqualTo(testEntityId);
        
        ArgumentCaptor<LogEntry> logCaptor = ArgumentCaptor.forClass(LogEntry.class);
        verify(logEntryRepository).save(logCaptor.capture());
        
        LogEntry savedLog = logCaptor.getValue();
        assertThat(savedLog.getAction()).isEqualTo("CREATE");
        assertThat(savedLog.getLevel()).isEqualTo(LogLevel.INFO);
    }

    @Test
    void findLogsByEntityTypeAndId_WithExistingLogs_ReturnsLogs() {
        // Given
        List<LogEntry> expectedLogs = List.of(testLogEntry);
        when(logEntryRepository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc("BUG", testEntityId))
                .thenReturn(expectedLogs);

        // When
        List<LogEntry> result = logService.findLogsByEntityTypeAndId("BUG", testEntityId);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEntityType()).isEqualTo("BUG");
        verify(logEntryRepository).findByEntityTypeAndEntityIdOrderByCreatedAtDesc("BUG", testEntityId);
    }

    @Test
    void findRecentLogs_WithLimit_ReturnsLimitedLogs() {
        // Given
        Page<LogEntry> expectedPage = new PageImpl<>(List.of(testLogEntry));
        when(logEntryRepository.findAll(any(PageRequest.class))).thenReturn(expectedPage);

        // When
        List<LogEntry> result = logService.findRecentLogs(5);

        // Then
        assertThat(result).hasSize(1);
        verify(logEntryRepository).findAll(any(PageRequest.class));
    }

    @Test
    void findByAction_WithValidAction_ReturnsPagedLogs() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<LogEntry> expectedPage = new PageImpl<>(List.of(testLogEntry));
        when(logEntryRepository.findByAction("CREATE", pageable)).thenReturn(expectedPage);

        // When
        Page<LogEntry> result = logService.findByAction("CREATE", pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getAction()).isEqualTo("CREATE");
        verify(logEntryRepository).findByAction("CREATE", pageable);
    }

    @Test
    void findById_WithExistingId_ReturnsLogEntry() {
        // Given
        UUID logId = UUID.randomUUID();
        when(logEntryRepository.findById(logId)).thenReturn(Optional.of(testLogEntry));

        // When
        LogEntry result = logService.findById(logId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testLogEntry.getId());
        verify(logEntryRepository).findById(logId);
    }

    @Test
    void searchLogs_WithKeyword_ReturnsMatchingLogs() {
        // Given
        List<LogEntry> expectedLogs = List.of(testLogEntry);
        when(logEntryRepository.searchByKeyword("bug")).thenReturn(expectedLogs);

        // When
        List<LogEntry> result = logService.searchLogs("bug");

        // Then
        assertThat(result).hasSize(1);
        verify(logEntryRepository).searchByKeyword("bug");
    }
}

