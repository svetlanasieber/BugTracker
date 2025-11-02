package com.bugtracker.web.controller;

import com.bugtracker.model.entity.LogEntry;
import com.bugtracker.web.dto.LogEntryDTO;
import com.bugtracker.service.LogService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/logs")
@Tag(name = "Log Management", description = "API for system log management")
public class LogRestController {

    private final LogService logService;

    public LogRestController(LogService logService) {
        this.logService = logService;
    }

    @GetMapping
    @Operation(summary = "Get all logs", description = "Returns a page of logs with filtering options")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved logs",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "400", description = "Invalid parameters")
    })
    public ResponseEntity<Map<String, Object>> getAllLogs(
            @Parameter(description = "Page number (starts from 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Filter by action") @RequestParam(required = false) String action,
            @Parameter(description = "Filter by entity type") @RequestParam(required = false) String entityType) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("timestamp").descending());
        Page<LogEntry> logsPage;
        if (action != null && entityType != null) {
            logsPage = logService.findByActionAndEntityType(action, entityType, pageRequest);
        } else if (action != null) {
            logsPage = logService.findByAction(action, pageRequest);
        } else if (entityType != null) {
            logsPage = logService.findByEntityType(entityType, pageRequest);
        } else {
            logsPage = logService.findAllLogs(pageRequest);
        }
        List<LogEntryDTO> logDTOs = logsPage.getContent().stream()
                .map(LogEntryDTO::fromEntity)
                .collect(Collectors.toList());
        Map<String, Object> response = new HashMap<>();
        response.put("logs", logDTOs);
        response.put("currentPage", logsPage.getNumber());
        response.put("totalItems", logsPage.getTotalElements());
        response.put("totalPages", logsPage.getTotalPages());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get log by ID", description = "Returns a specific log entry by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Log successfully found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = LogEntryDTO.class))),
        @ApiResponse(responseCode = "404", description = "Log not found")
    })
    public ResponseEntity<LogEntryDTO> getLogById(
            @Parameter(description = "Log entry ID") @PathVariable UUID id) {
        LogEntry logEntry = logService.findById(id);
        LogEntryDTO logDTO = LogEntryDTO.fromEntity(logEntry);
        return new ResponseEntity<>(logDTO, HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Create new log entry", description = "Creates a new log entry in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Log successfully created",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = LogEntryDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid data for log entry")
    })
    public ResponseEntity<LogEntryDTO> createLogEntry(
            @Parameter(description = "Data for the new log entry") @RequestBody Map<String, String> logRequest) {
        String action = logRequest.get("action");
        String entityType = logRequest.get("entityType");
        String entityId = logRequest.get("entityId");
        String username = logRequest.get("username");
        String details = logRequest.get("details");
        if (action == null || entityType == null || entityId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        LogEntry logEntry = logService.createLogEntry(
                action,
                entityType,
                UUID.fromString(entityId),
                username,
                details
        );
        LogEntryDTO logDTO = LogEntryDTO.fromEntity(logEntry);
        return new ResponseEntity<>(logDTO, HttpStatus.CREATED);
    }

    @GetMapping("/actions")
    public ResponseEntity<List<String>> getDistinctActions() {
        return new ResponseEntity<>(logService.findDistinctActions(), HttpStatus.OK);
    }

    @GetMapping("/entity-types")
    public ResponseEntity<List<String>> getDistinctEntityTypes() {
        return new ResponseEntity<>(logService.findDistinctEntityTypes(), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<LogEntryDTO>> searchLogs(@RequestParam String query) {
        List<LogEntryDTO> logDTOs = logService.searchLogs(query).stream()
                .map(LogEntryDTO::fromEntity)
                .collect(Collectors.toList());
        return new ResponseEntity<>(logDTOs, HttpStatus.OK);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<LogEntryDTO>> getRecentLogs(@RequestParam(defaultValue = "10") int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit, Sort.by("timestamp").descending());
        List<LogEntryDTO> logDTOs = logService.findAllLogs(pageRequest).getContent().stream()
                .map(LogEntryDTO::fromEntity)
                .collect(Collectors.toList());
        return new ResponseEntity<>(logDTOs, HttpStatus.OK);
    }
} 