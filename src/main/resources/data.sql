-- Subjects (IDs auto-generated)
INSERT INTO tb_subject (name, description)
VALUES ('Java', 'Java programming language'),
       ('Python', 'Python programming language');

-- Questions (IDs auto-generated)
INSERT INTO tb_question (statement, subject_id)
VALUES ('What is the capital of Brazil?', 1);

-- Alternatives (IDs auto-generated)
INSERT INTO tb_alternative (description, correct, question_id)
VALUES ('São Paulo', FALSE, 1),
       ('Rio de Janeiro', FALSE, 1),
       ('Brasília', TRUE, 1),
       ('Salvador', FALSE, 1);