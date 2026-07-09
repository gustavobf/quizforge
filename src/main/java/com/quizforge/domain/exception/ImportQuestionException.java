package com.quizforge.domain.exception;

import lombok.*;

import java.util.*;

@Getter
@Builder
public class ImportQuestionException extends RuntimeException {

    private final String message;
    private final List<String> errors;
    private final int totalRows;
    private final int successRows;
    private final String errorCode;
    private final Throwable cause;

    public ImportQuestionException (String message) {
        this(message, List.of(), 0, 0, "IMPORT_ERROR", null);
    }

    public ImportQuestionException (String message, Throwable cause) {
        this(message, List.of(), 0, 0, "IMPORT_ERROR", cause);
    }

    public ImportQuestionException (String message, List<String> errors) {
        this(message, errors, 0, 0, "IMPORT_ERROR", null);
    }

    public ImportQuestionException (String message, int totalRows, int successRows) {
        this(message, List.of(), totalRows, successRows, "IMPORT_ERROR", null);
    }

    public ImportQuestionException (String message, List<String> errors, int totalRows, int successRows,
                                    String errorCode, Throwable cause) {
        super(message, cause);
        this.message = message;
        this.errors = errors != null ? errors : List.of();
        this.totalRows = totalRows;
        this.successRows = successRows;
        this.errorCode = errorCode != null ? errorCode : "IMPORT_ERROR";
        this.cause = cause;
    }

    public int getFailedRows () {
        return totalRows - successRows;
    }

    public boolean isPartialSuccess () {
        return successRows > 0 && successRows < totalRows;
    }

    public String getSummary () {
        if (isPartialSuccess()) {
            return String.format("Successfully imported %d of %d questions. %d failed.", successRows, totalRows,
                    getFailedRows());
        }
        return message;
    }

    @Override
    public String toString () {
        if (isPartialSuccess()) {
            return String.format("ImportQuestionException: %s (total=%d, success=%d, failed=%d)", message, totalRows,
                    successRows, getFailedRows());
        }
        return String.format("ImportQuestionException: %s", message);
    }
}