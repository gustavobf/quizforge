package com.quizforge.infrastructure.exception;

import com.quizforge.domain.exception.*;
import lombok.extern.slf4j.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ExamNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleExamNotFound (ExamNotFoundException ex) {
        log.warn("Exam not found: {}", ex.getExamId());
        return buildErrorResponse(HttpStatus.NOT_FOUND, "EXAM_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(ExamNotInProgressException.class)
    public ResponseEntity<ErrorResponse> handleExamNotInProgress (ExamNotInProgressException ex) {
        log.warn("Exam not in progress: {}", ex.getExamId());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(ExamAlreadyCompletedException.class)
    public ResponseEntity<ErrorResponse> handleExamAlreadyCompleted (ExamAlreadyCompletedException ex) {
        log.warn("Exam already completed: {}", ex.getExamId());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(ExamNotCompleteException.class)
    public ResponseEntity<ErrorResponse> handleExamNotComplete (ExamNotCompleteException ex) {
        log.warn("Exam not complete: {}/{}, ExamId: {}", ex.getAnswered(), ex.getTotal(), ex.getExamId());
        ErrorResponse response = ErrorResponse.builder().timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value()).error("EXAM_NOT_COMPLETE").message(ex.getMessage())
                .details(String.format("Answered: %d of %d questions", ex.getAnswered(), ex.getTotal())).build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(QuestionAlreadyAnsweredException.class)
    public ResponseEntity<ErrorResponse> handleQuestionAlreadyAnswered (QuestionAlreadyAnsweredException ex) {
        log.warn("Question already answered: {}", ex.getQuestionId());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(InvalidAnswerException.class)
    public ResponseEntity<ErrorResponse> handleInvalidAnswer (InvalidAnswerException ex) {
        log.warn("Invalid answer: QuestionId={}, AlternativeId={}", ex.getQuestionId(), ex.getAlternativeId());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(QuestionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleQuestionNotFound (QuestionNotFoundException ex) {
        log.warn("Question not found: {}", ex.getQuestionId());
        return buildErrorResponse(HttpStatus.NOT_FOUND, "QUESTION_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(ImportQuestionException.class)
    public ResponseEntity<ErrorResponse> handleImportQuestion (ImportQuestionException ex) {
        log.error("Import error: {}", ex.getMessage());
        ErrorResponse response = ErrorResponse.builder().timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value()).error("IMPORT_ERROR").message(ex.getMessage())
                .errors(ex.getErrors()).build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException (BusinessException ex) {
        log.warn("Business error: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "BUSINESS_ERROR", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException (Exception ex) {
        log.error("Unexpected error: ", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "An unexpected error occurred");
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse (HttpStatus status, String error, String message) {
        ErrorResponse response = ErrorResponse.builder().timestamp(LocalDateTime.now()).status(status.value())
                .error(error).message(message).build();
        return ResponseEntity.status(status).body(response);
    }
}