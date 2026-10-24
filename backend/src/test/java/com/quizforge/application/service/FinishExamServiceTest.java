package com.quizforge.application.service;

import com.quizforge.adapter.in.web.dto.request.FinishExamRequest;
import com.quizforge.application.port.out.CurrentUserPort;
import com.quizforge.application.port.out.ExamRepositoryPort;
import com.quizforge.application.port.out.UserAnswerRepositoryPort;
import com.quizforge.domain.enumtype.QuestionType;
import com.quizforge.domain.model.Alternative;
import com.quizforge.domain.model.Exam;
import com.quizforge.domain.model.ExamQuestion;
import com.quizforge.domain.model.Question;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FinishExamServiceTest {
    @Test
    void separatesUnansweredQuestionsFromWrongAnswers() {
        ExamRepositoryPort exams = mock(ExamRepositoryPort.class);
        UserAnswerRepositoryPort answers = mock(UserAnswerRepositoryPort.class);
        CurrentUserPort currentUser = mock(CurrentUserPort.class);
        List<Alternative> alternatives = List.of(
                new Alternative(1L, "Correct", true), new Alternative(2L, "Wrong", false));
        Exam exam = Exam.builder().id(1L).ownerId(7L).title("Exam")
                .startedAt(LocalDateTime.now()).totalQuestions(3)
                .questions(List.of(
                        new ExamQuestion(1L, new Question(1L, "First", alternatives, null, QuestionType.SINGLE_CHOICE), 1),
                        new ExamQuestion(2L, new Question(2L, "Second", alternatives, null, QuestionType.SINGLE_CHOICE), 2),
                        new ExamQuestion(3L, new Question(3L, "Third", alternatives, null, QuestionType.SINGLE_CHOICE), 3)))
                .build();
        when(exams.findByIdForUpdate(1L)).thenReturn(Optional.of(exam));
        when(exams.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(currentUser.requireCurrentUserId()).thenReturn(7L);
        FinishExamRequest request = mock(FinishExamRequest.class);
        when(request.getAnswers()).thenReturn(Map.of(1L, List.of(1L), 2L, List.of(2L)));

        var result = new FinishExamService(exams, answers, currentUser).execute(1L, request);

        assertEquals(1, result.getCorrectAnswers());
        assertEquals(1, result.getWrongAnswers());
        assertEquals(1, result.getUnansweredQuestions());
        assertTrue(result.getQuestions().getFirst().getAnswered());
        assertFalse(result.getQuestions().getLast().getAnswered());
        assertEquals(100.0 / 3, result.getScore(), 0.001);
    }
}