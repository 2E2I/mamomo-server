INSERT INTO authority (authority_name) value ('ROLE_USER');
INSERT INTO authority (authority_name) value ('ROLE_ADMIN');

ALTER table mamomo.topic DEFAULT CHARACTER SET utf8;
INSERT INTO topic (topic_id, topic_name) value (1,'아동|청소년');
INSERT INTO topic (topic_id, topic_name) value (2,'어르신');
INSERT INTO topic (topic_id, topic_name) value (3,'장애인');
INSERT INTO topic (topic_id, topic_name) value (4,'어려운이웃');
INSERT INTO topic (topic_id, topic_name) value (5,'다문화');
INSERT INTO topic (topic_id, topic_name) value (6,'지구촌');
INSERT INTO topic (topic_id, topic_name) value (7,'가족|여성');
INSERT INTO topic (topic_id, topic_name) value (8,'우리사회');
INSERT INTO topic (topic_id, topic_name) value (9,'동물');
INSERT INTO topic (topic_id, topic_name) value (10,'환경');

INSERT INTO user (id, birth, email, nickname, password, sex) value ('550e8400-e29b-41d4-a716-446655440000','2000-01-01','test@email.com','testNickName','{bcrypt}$2a$10$GXbyN.DHQXq1gWKKsVu7DeQ6Gc4kE8QeFb..RpOHHYw00sRQRkNha','M');
