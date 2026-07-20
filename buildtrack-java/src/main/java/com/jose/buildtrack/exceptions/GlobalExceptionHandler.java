package com.jose.buildtrack.exceptions;

import com.jose.buildtrack.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BuildNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleBuildNotFound(BuildNotFoundException exception) {

        ErrorResponseDTO response = new ErrorResponseDTO(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                exception.getMessage()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(BuildAlreadyExistException.class)
    public ResponseEntity<ErrorResponseDTO> handleBuildAlreadyExists(BuildAlreadyExistException exception) {

        ErrorResponseDTO response = new ErrorResponseDTO(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                exception.getMessage()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            IllegalStateException.class
    })
    public ResponseEntity<ErrorResponseDTO> handleBadRequest(RuntimeException exception) {

        ErrorResponseDTO response = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                exception.getMessage()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationErrors(MethodArgumentNotValidException exception) {

        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .findFirst()
                .orElse("Invalid request");

        ErrorResponseDTO response = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                message
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

	@ExceptionHandler(InvalidPlatformException.class)
        public ResponseEntity<ErrorResponseDTO> handleInvalidPlatform(InvalidPlatformException exception) {

		ErrorResponseDTO response = new ErrorResponseDTO(
				HttpStatus.BAD_REQUEST.value(),
				HttpStatus.BAD_REQUEST.getReasonPhrase(),
				exception.getMessage()
		);

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

    @ExceptionHandler(IssueNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleIssueNotFound(IssueNotFoundException exception) {

        ErrorResponseDTO response = new ErrorResponseDTO(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                exception.getMessage()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }


    @ExceptionHandler(InvalidIssueSeverityException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidIssueSeverity(InvalidIssueSeverityException exception) {

        ErrorResponseDTO response = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                exception.getMessage()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ReleaseNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleReleaseNotFound(ReleaseNotFoundException exception) {

        ErrorResponseDTO response = new ErrorResponseDTO(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                exception.getMessage()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(ReleaseAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleReleaseAlreadyExists(ReleaseAlreadyExistsException exception) {

        ErrorResponseDTO response = new ErrorResponseDTO(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                exception.getMessage()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodValidation(
            HandlerMethodValidationException exception
    ) {
        ErrorResponseDTO response = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Invalid request parameters"
        );
    
        return ResponseEntity
                .badRequest()
                .body(response);
    }
}