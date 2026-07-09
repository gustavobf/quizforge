package com.quizforge.application.port.in;

public interface AnswerQuestionUseCase {
    void execute (Long examId, Long alternativeId);
}