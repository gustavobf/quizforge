package com.quizforge.application.service;

import com.quizforge.adapter.in.web.dto.request.*;
import com.quizforge.adapter.in.web.dto.response.*;
import com.quizforge.application.port.in.*;
import com.quizforge.application.port.out.*;
import com.quizforge.domain.model.*;
import lombok.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

@Service
@RequiredArgsConstructor
public class CreateSubjectService implements CreateSubjectUseCase {

    private final SubjectRepositoryPort subjectRepository;

    @Override
    @Transactional
    public SubjectResponse execute (CreateSubjectRequest request) {
        Subject subject = Subject.builder().name(request.getName()).description(request.getDescription()).build();

        Subject savedSubject = subjectRepository.save(subject);

        return SubjectResponse.builder().id(savedSubject.getId()).name(savedSubject.getName())
                .description(savedSubject.getDescription()).build();
    }
}