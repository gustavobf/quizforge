package com.quizforge.application.service;

import com.quizforge.application.port.in.*;
import com.quizforge.application.port.out.*;
import com.quizforge.domain.exception.*;
import com.quizforge.domain.model.*;
import lombok.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

@Service
@RequiredArgsConstructor
public class DeleteSubjectService implements DeleteSubjectUseCase {

    private final SubjectRepositoryPort subjectRepository;

    @Override
    @Transactional
    public void execute (Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + id));

        subjectRepository.delete(subject);
    }
}