package com.quizforge.application.service;

import com.quizforge.application.port.out.CurrentUserPort;
import com.quizforge.application.port.out.ExamRepositoryPort;
import com.quizforge.domain.exception.BusinessException;
import com.quizforge.domain.exception.ExamNotFoundException;
import com.quizforge.domain.model.Exam;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CancelExamServiceTest {
    private final ExamRepositoryPort exams = mock(ExamRepositoryPort.class);
    private final CurrentUserPort currentUser = mock(CurrentUserPort.class);
    private final CancelExamService service = new CancelExamService(exams, currentUser);

    @Test
    void deletesOnlyAnOwnedUnfinishedExamWithoutSavingAScore() {
        when(currentUser.requireCurrentUserId()).thenReturn(7L);
        when(exams.findByIdForUpdate(1L)).thenReturn(Optional.of(Exam.builder().id(1L).ownerId(7L).build()));
        service.execute(1L);
        verify(exams).deleteById(1L);
        verify(exams, never()).save(any());
    }

    @Test
    void rejectsAnotherUsersExamEvenForAnAdmin() {
        when(currentUser.requireCurrentUserId()).thenReturn(8L);
        when(currentUser.isAdmin()).thenReturn(true);
        when(exams.findByIdForUpdate(1L)).thenReturn(Optional.of(Exam.builder().ownerId(7L).build()));
        assertThrows(BusinessException.class, () -> service.execute(1L));
        verify(exams, never()).deleteById(any());
    }

    @Test
    void preservesCompletedExamsAndStatistics() {
        when(currentUser.requireCurrentUserId()).thenReturn(7L);
        when(exams.findByIdForUpdate(1L)).thenReturn(Optional.of(Exam.builder().ownerId(7L)
                .finishedAt(LocalDateTime.now()).score(100.0).build()));
        assertThrows(BusinessException.class, () -> service.execute(1L));
        verify(exams, never()).deleteById(any());
    }

    @Test
    void rejectsMissingExams() {
        when(exams.findByIdForUpdate(1L)).thenReturn(Optional.empty());
        assertThrows(ExamNotFoundException.class, () -> service.execute(1L));
    }
}