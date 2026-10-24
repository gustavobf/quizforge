package com.quizforge.adapter.in.web.controller;

import com.quizforge.adapter.in.web.docs.*;
import com.quizforge.adapter.in.web.dto.request.*;
import com.quizforge.adapter.in.web.dto.response.*;
import com.quizforge.application.port.in.*;
import jakarta.validation.*;
import lombok.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/subjects")
@RequiredArgsConstructor
public class SubjectController implements SubjectControllerDocs {

    private final CreateSubjectUseCase createSubjectUseCase;
    private final GetAllSubjectsUseCase getAllSubjectsUseCase;
    private final GetSubjectByIdUseCase getSubjectByIdUseCase;
    private final UpdateSubjectUseCase updateSubjectUseCase;
    private final DeleteSubjectUseCase deleteSubjectUseCase;

    @Override
    @GetMapping
    public ResponseEntity<List<SubjectResponse>> getAllSubjects () {
        List<SubjectResponse> responses = getAllSubjectsUseCase.execute();
        return ResponseEntity.ok(responses);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<SubjectResponse> getSubjectById (@PathVariable Long id) {
        SubjectResponse response = getSubjectByIdUseCase.execute(id);
        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping
    public ResponseEntity<SubjectResponse> createSubject (@Valid @RequestBody CreateSubjectRequest request) {
        SubjectResponse response = createSubjectUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<SubjectResponse> updateSubject (@PathVariable Long id,
                                                          @Valid @RequestBody CreateSubjectRequest request) {
        SubjectResponse response = updateSubjectUseCase.execute(id, request);
        return ResponseEntity.ok(response);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubject (@PathVariable Long id) {
        deleteSubjectUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}