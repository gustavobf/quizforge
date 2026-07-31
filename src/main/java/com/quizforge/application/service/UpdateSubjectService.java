package com.quizforge.application.service;

import com.quizforge.adapter.in.web.dto.request.CreateSubjectRequest;
import com.quizforge.adapter.in.web.dto.response.SubjectResponse;
import com.quizforge.application.port.in.UpdateSubjectUseCase;
import com.quizforge.application.port.out.SubjectRepositoryPort;
import com.quizforge.domain.exception.ResourceNotFoundException;
import com.quizforge.domain.model.Subject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateSubjectService implements UpdateSubjectUseCase {

    private final SubjectRepositoryPort subjectRepository;

    @Override
    @Transactional
    public SubjectResponse execute(Long id, CreateSubjectRequest request) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + id));

        Subject updatedSubject = Subject.builder()
                .id(subject.getId())
                .name(request.getName())
                .description(request.getDescription())
                .build();

        updatedSubject = subjectRepository.save(updatedSubject);

        return SubjectResponse.builder()
                .id(updatedSubject.getId())
                .name(updatedSubject.getName())
                .description(updatedSubject.getDescription())
                .build();
    }
}