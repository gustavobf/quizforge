CREATE DATABASE quizforge;
CREATE USER quizforge WITH PASSWORD 'quizforge123';
GRANT ALL PRIVILEGES ON DATABASE quizforge TO quizforge;

\c quizforge;

CREATE TABLE IF NOT EXISTS tb_subject (
                                          id BIGSERIAL PRIMARY KEY,
                                          name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500)
    );

CREATE TABLE IF NOT EXISTS tb_question (
                                           id BIGSERIAL PRIMARY KEY,
                                           statement VARCHAR(500) NOT NULL,
    subject_id BIGINT NOT NULL,
    FOREIGN KEY (subject_id) REFERENCES tb_subject(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS tb_alternative (
                                              id BIGSERIAL PRIMARY KEY,
                                              description VARCHAR(500) NOT NULL,
    correct BOOLEAN NOT NULL,
    question_id BIGINT NOT NULL,
    FOREIGN KEY (question_id) REFERENCES tb_question(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS tb_exam (
                                       id BIGSERIAL PRIMARY KEY,
                                       title VARCHAR(200) NOT NULL,
    subject_id BIGINT,
    total_questions INT NOT NULL DEFAULT 0,
    current_question_index INT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'NOT_STARTED',
    started_at TIMESTAMP NULL,
    finished_at TIMESTAMP NULL,
    score DOUBLE PRECISION NULL,
    FOREIGN KEY (subject_id) REFERENCES tb_subject(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS exam_questions (
                                              id BIGSERIAL PRIMARY KEY,
                                              exam_id BIGINT NOT NULL,
                                              question_id BIGINT NOT NULL,
                                              order_number INT,
                                              is_answered BOOLEAN DEFAULT FALSE,
                                              is_correct BOOLEAN DEFAULT FALSE,
                                              selected_alternative_ids JSON,
                                              FOREIGN KEY (exam_id) REFERENCES tb_exam(id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES tb_question(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS user_answers (
                                            id BIGSERIAL PRIMARY KEY,
                                            exam_id BIGINT NOT NULL,
                                            question_id BIGINT NOT NULL,
                                            is_correct BOOLEAN DEFAULT FALSE,
                                            answered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                            selected_alternative_ids JSON,
                                            FOREIGN KEY (exam_id) REFERENCES tb_exam(id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES tb_question(id) ON DELETE CASCADE
    );

CREATE INDEX IF NOT EXISTS idx_question_subject_id ON tb_question(subject_id);
CREATE INDEX IF NOT EXISTS idx_alternative_question_id ON tb_alternative(question_id);
CREATE INDEX IF NOT EXISTS idx_exam_subject_id ON tb_exam(subject_id);
CREATE INDEX IF NOT EXISTS idx_exam_finished_at ON tb_exam(finished_at);
CREATE INDEX IF NOT EXISTS idx_exam_status ON tb_exam(status);
CREATE INDEX IF NOT EXISTS idx_exam_questions_exam_id ON exam_questions(exam_id);
CREATE INDEX IF NOT EXISTS idx_exam_questions_question_id ON exam_questions(question_id);
CREATE INDEX IF NOT EXISTS idx_user_answers_exam_id ON user_answers(exam_id);
CREATE INDEX IF NOT EXISTS idx_user_answers_question_id ON user_answers(question_id);