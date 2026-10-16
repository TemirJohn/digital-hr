-- =============================================
-- V4: LMS — Jana Academy (courses & assignments)
-- =============================================

CREATE TABLE courses (
    id             BIGSERIAL PRIMARY KEY,
    title          VARCHAR(255) NOT NULL,
    description    TEXT,
    content_url    VARCHAR(500),
    duration_hours INT,
    is_active      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMP DEFAULT NOW()
);

CREATE TABLE course_assignments (
    id               BIGSERIAL PRIMARY KEY,
    course_id        BIGINT NOT NULL REFERENCES courses(id),
    employee_id      BIGINT NOT NULL REFERENCES employees(id),
    status           VARCHAR(50) NOT NULL DEFAULT 'ASSIGNED',
    progress_percent INT NOT NULL DEFAULT 0,
    assigned_at      TIMESTAMP DEFAULT NOW(),
    completed_at     TIMESTAMP
);

CREATE INDEX idx_course_assign_employee ON course_assignments(employee_id);
CREATE INDEX idx_course_assign_status   ON course_assignments(status);

-- Seed: courses
INSERT INTO courses (id, title, description, content_url, duration_hours) VALUES
(1, 'Введение в JanaPost',                'История компании, миссия, ценности и корпоративная культура',       'https://academy.janapost.kz/intro',          2),
(2, 'Стандарты обслуживания клиентов',     'Правила работы с клиентами, разрешение конфликтов',                 'https://academy.janapost.kz/service',        4),
(3, 'Техника безопасности',                'Обязательный курс по ТБ для всех сотрудников',                     'https://academy.janapost.kz/safety',         1),
(4, 'Работа с IT-системами JanaPost',      'CRM, ERP, система трекинга посылок',                               'https://academy.janapost.kz/it-systems',     6),
(5, 'Управление франшизой',               'Курс для менеджеров франшиз: финансы, KPI, управление командой',    'https://academy.janapost.kz/franchise-mgmt', 8),
(6, 'Трудовой кодекс РК — основы',        'Базовые знания ТК РК для HR и руководителей',                      'https://academy.janapost.kz/labor-law',      3),
(7, 'Эффективная логистика',              'Оптимизация маршрутов, складских операций и last-mile delivery',    'https://academy.janapost.kz/logistics',      5);

SELECT setval('courses_id_seq', 7);

-- Seed: assignments
INSERT INTO course_assignments (course_id, employee_id, status, progress_percent, assigned_at, completed_at) VALUES
(1, 32, 'COMPLETED',   100, '2022-01-10', '2022-01-12'),
(3, 32, 'COMPLETED',   100, '2022-01-10', '2022-01-13'),
(2, 32, 'IN_PROGRESS',  60, '2022-01-15', NULL),
(1, 37, 'IN_PROGRESS',  30, '2022-05-16', NULL),
(3, 37, 'ASSIGNED',       0, '2022-05-16', NULL),
(1, 30, 'COMPLETED',   100, '2021-03-02', '2021-03-03'),
(5, 21, 'IN_PROGRESS',  45, '2020-06-01', NULL),
(4, 10, 'COMPLETED',   100, '2022-03-05', '2022-03-10'),
(6, 12, 'ASSIGNED',       0, '2026-10-01', NULL),
(7, 14, 'IN_PROGRESS',  20, '2026-09-15', NULL),
(2, 15, 'ASSIGNED',       0, '2026-10-10', NULL);
