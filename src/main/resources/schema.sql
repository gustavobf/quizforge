CREATE TABLE IF NOT EXISTS tb_subject (
                                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                          name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500)
    );

CREATE TABLE IF NOT EXISTS tb_question (
                                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                           statement VARCHAR(500) NOT NULL,
    subject_id BIGINT NOT NULL,
    FOREIGN KEY (subject_id) REFERENCES tb_subject(id)
    );

CREATE TABLE IF NOT EXISTS tb_alternative (
                                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                              description VARCHAR(500) NOT NULL,
    correct BOOLEAN NOT NULL,
    question_id BIGINT NOT NULL,
    FOREIGN KEY (question_id) REFERENCES tb_question(id)
    );