package com.quizforge.application.port.in;

import com.quizforge.adapter.in.web.dto.response.*;
import org.springframework.web.multipart.*;

public interface ImportQuestionUseCase {
    ImportQuestionsResponse execute (MultipartFile file, Long subjectId);
}
