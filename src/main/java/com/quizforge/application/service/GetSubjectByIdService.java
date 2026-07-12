package com.quizforge.application.service;

import com.quizforge.adapter.in.web.dto.response.*;
import com.quizforge.application.port.in.*;
import com.quizforge.application.port.out.*;
import com.quizforge.domain.exception.*;
import com.quizforge.domain.model.*;
import lombok.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

@Service
@RequiredArgsConstructor
public class GetSubjectByIdService implements GetSubjectByIdUseCase {

    private final SubjectRepositoryPort subjectRepository;

    @Override
    @Transactional(readOnly = true)
    public SubjectResponse execute (Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + id));

        return SubjectResponse.builder().id(subject.getId()).name(subject.getName())
                .description(subject.getDescription()).build();
    }
}