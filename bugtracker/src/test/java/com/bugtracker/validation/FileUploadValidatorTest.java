package com.bugtracker.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileUploadValidatorTest {

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;

    @Mock
    private ValidFileUpload validFileUpload;

    private FileUploadValidator validator;

    @BeforeEach
    void setUp() {
        validator = new FileUploadValidator();
        
      
        when(validFileUpload.maxSize()).thenReturn(10485760L); // 10MB
        when(validFileUpload.allowedTypes()).thenReturn(new String[]{"image/jpeg", "image/png"});
        when(validFileUpload.required()).thenReturn(false);
        
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        
        validator.initialize(validFileUpload);
    }

    @Test
    void isValid_WithNullFileAndNotRequired_ReturnsTrue() {
        // When
        boolean result = validator.isValid(null, context);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void isValid_WithEmptyFileAndNotRequired_ReturnsTrue() {
        // Given
        MultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);

        // When
        boolean result = validator.isValid(emptyFile, context);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void isValid_WithNullFileAndRequired_ReturnsFalse() {
        // Given
        when(validFileUpload.required()).thenReturn(true);
        validator.initialize(validFileUpload);

        // When
        boolean result = validator.isValid(null, context);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void isValid_WithValidFile_ReturnsTrue() {
        // Given
        byte[] content = "test image content".getBytes();
        MultipartFile validFile = new MockMultipartFile(
                "file", 
                "test.jpg", 
                "image/jpeg", 
                content
        );

        // When
        boolean result = validator.isValid(validFile, context);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void isValid_WithOversizedFile_ReturnsFalse() {
        // Given
        byte[] largeContent = new byte[11 * 1024 * 1024]; // 11MB
        MultipartFile largeFile = new MockMultipartFile(
                "file", 
                "large.jpg", 
                "image/jpeg", 
                largeContent
        );

        // When
        boolean result = validator.isValid(largeFile, context);

        // Then
        assertThat(result).isFalse();
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate(contains("File size exceeds"));
    }

    @Test
    void isValid_WithInvalidContentType_ReturnsFalse() {
        // Given
        byte[] content = "test content".getBytes();
        MultipartFile invalidFile = new MockMultipartFile(
                "file", 
                "test.pdf", 
                "application/pdf", 
                content
        );

        // When
        boolean result = validator.isValid(invalidFile, context);

        // Then
        assertThat(result).isFalse();
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate(contains("File type not allowed"));
    }

    @Test
    void isValid_WithNullContentType_ReturnsFalse() {
        // Given
        byte[] content = "test content".getBytes();
        MultipartFile fileWithNullType = new MockMultipartFile(
                "file", 
                "test.unknown", 
                null, 
                content
        );

        // When
        boolean result = validator.isValid(fileWithNullType, context);

        // Then
        assertThat(result).isFalse();
    }
}

