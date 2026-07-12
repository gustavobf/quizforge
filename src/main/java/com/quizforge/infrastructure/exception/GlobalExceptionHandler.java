package com.quizforge.infrastructure.exception;

import com.quizforge.domain.exception.*;
import lombok.extern.slf4j.*;
import org.springframework.http.*;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.util.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions (MethodArgumentNotValidException ex) {
        Map<String, String> validationErrors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            validationErrors.put(error.getField(), error.getDefaultMessage());
        });

        ErrorResponse response = ErrorResponse.builder().timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value()).error("VALIDATION_ERROR").message("Validation failed")
                .validationErrors(validationErrors).build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ExamNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleExamNotFound (ExamNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "EXAM_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(ExamNotInProgressException.class)
    public ResponseEntity<ErrorResponse> handleExamNotInProgress (ExamNotInProgressException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(ExamAlreadyCompletedException.class)
    public ResponseEntity<ErrorResponse> handleExamAlreadyCompleted (ExamAlreadyCompletedException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(ExamNotCompleteException.class)
    public ResponseEntity<ErrorResponse> handleExamNotComplete (ExamNotCompleteException ex) {
        ErrorResponse response = ErrorResponse.builder().timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value()).error("EXAM_NOT_COMPLETE").message(ex.getMessage())
                .details(String.format("Answered: %d of %d questions", ex.getAnswered(), ex.getTotal())).build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(QuestionAlreadyAnsweredException.class)
    public ResponseEntity<ErrorResponse> handleQuestionAlreadyAnswered (QuestionAlreadyAnsweredException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(InvalidAnswerException.class)
    public ResponseEntity<ErrorResponse> handleInvalidAnswer (InvalidAnswerException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(QuestionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleQuestionNotFound (QuestionNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "QUESTION_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(ImportQuestionException.class)
    public ResponseEntity<ErrorResponse> handleImportQuestion (ImportQuestionException ex) {
        ErrorResponse response = ErrorResponse.builder().timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value()).error("IMPORT_ERROR").message(ex.getMessage())
                .errors(ex.getErrors()).build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException (BusinessException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "BUSINESS_ERROR", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException (Exception ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "An unexpected error occurred");
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse (HttpStatus status, String error, String message) {
        ErrorResponse response = ErrorResponse.builder().timestamp(LocalDateTime.now()).status(status.value())
                .error(error).message(message).build();
        return ResponseEntity.status(status).body(response);
    }
}