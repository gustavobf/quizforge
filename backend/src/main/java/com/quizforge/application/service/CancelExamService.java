package com.quizforge.application.service;

import com.quizforge.application.port.in.CancelExamUseCase;
import com.quizforge.application.port.out.CurrentUserPort;
import com.quizforge.application.port.out.ExamRepositoryPort;
import com.quizforge.domain.exception.BusinessException;
import com.quizforge.domain.exception.ExamNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CancelExamService implements CancelExamUseCase {
    private final ExamRepositoryPort examRepository;
    private final CurrentUserPort currentUserPort;

    @Override
    @Transactional
    public void execute(Long examId) {
        var exam = examRepository.findByIdForUpdate(examId)
                .orElseThrow(() -> new ExamNotFoundException(examId));
        Long userId = currentUserPort.requireCurrentUserId();
        if (!Objects.equals(exam.getOwnerId(), userId)) {
            throw new BusinessException("You cannot cancel another user's exam");
        }
        if (exam.isFinished()) {
            throw new BusinessException("A completed exam cannot be cancelled");
        }
        examRepository.deleteById(examId);
    }
}