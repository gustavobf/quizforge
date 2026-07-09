package com.quizforge.adapter.in.web.controller;

import com.quizforge.adapter.in.web.docs.*;
import com.quizforge.adapter.in.web.dto.response.*;
import com.quizforge.application.port.out.*;
import com.quizforge.domain.model.*;
import lombok.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.*;

@RestController
@RequestMapping("/subjects")
@RequiredArgsConstructor
public class SubjectController implements SubjectControllerDocs {

    private final SubjectRepositoryPort subjectRepository;

    @Override
    @GetMapping
    public ResponseEntity<List<SubjectResponse>> getAllSubjects () {
        List<Subject> subjects = subjectRepository.findAll();

        List<SubjectResponse> responses = subjects.stream()
                .map(subject -> SubjectResponse.builder().id(subject.getId()).name(subject.getName())
                        .description(subject.getDescription()).build()).collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }
}