package com.quizforge.application.service;

import com.quizforge.adapter.in.web.dto.response.*;
import com.quizforge.application.port.in.*;
import com.quizforge.application.port.out.*;
import com.quizforge.domain.model.*;
import lombok.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import java.util.*;
import java.util.stream.*;

@Service
@RequiredArgsConstructor
public class GetAllSubjectsService implements GetAllSubjectsUseCase {

    private final SubjectRepositoryPort subjectRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SubjectResponse> execute () {
        List<Subject> subjects = subjectRepository.findAll();

        return subjects.stream().map(subject -> SubjectResponse.builder().id(subject.getId()).name(subject.getName())
                .description(subject.getDescription()).build()).collect(Collectors.toList());
    }
}