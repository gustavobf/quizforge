package com.quizforge;

import com.quizforge.infrastructure.config.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.*;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class QuizforgeApplicationTests {

	@Test
	void contextLoads() {
	}

}
