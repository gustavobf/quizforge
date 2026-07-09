CREATE DATABASE quizforge;
CREATE USER quizforge WITH PASSWORD 'quizforge123';
GRANT ALL PRIVILEGES ON DATABASE quizforge TO quizforge;

\c quizforge;

CREATE TABLE IF NOT EXISTS tb_question (
                                           id BIGSERIAL PRIMARY KEY,
                                           statement VARCHAR(500) NOT NULL,
    type VARCHAR(50) NOT NULL
    );

CREATE TABLE IF NOT EXISTS tb_alternative (
                                              id BIGSERIAL PRIMARY KEY,
                                              statement VARCHAR(500) NOT NULL,
    is_correct BOOLEAN NOT NULL,
    question_id BIGINT REFERENCES tb_question(id)
    );

CREATE TABLE IF NOT EXISTS exams (
                                     id BIGSERIAL PRIMARY KEY,
                                     title VARCHAR(255) NOT NULL,
    description TEXT,
    total_questions INTEGER,
    current_question_index INTEGER DEFAULT 0,
    status VARCHAR(50) NOT NULL,
    started_at TIMESTAMP,
    finished_at TIMESTAMP,
    user_id BIGINT
    );

CREATE TABLE IF NOT EXISTS exam_questions (
                                              id BIGSERIAL PRIMARY KEY,
                                              exam_id BIGINT REFERENCES exams(id),
    question_id BIGINT,
    order_number INTEGER,
    is_answered BOOLEAN DEFAULT FALSE,
    selected_alternative_id BIGINT,
    is_correct BOOLEAN DEFAULT FALSE
    );

CREATE TABLE IF NOT EXISTS user_answers (
                                            id BIGSERIAL PRIMARY KEY,
                                            exam_id BIGINT,
                                            question_id BIGINT,
                                            alternative_id BIGINT,
                                            is_correct BOOLEAN,
                                            answered_at TIMESTAMP
);