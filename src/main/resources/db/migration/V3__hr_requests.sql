-- =============================================
-- V3: HR Requests (Заявки: отпуск, аванс)
-- =============================================

CREATE TABLE hr_requests (
    id              BIGSERIAL PRIMARY KEY,
    employee_id     BIGINT NOT NULL REFERENCES employees(id),
    type            VARCHAR(50)  NOT NULL,
    status          VARCHAR(50)  NOT NULL DEFAULT 'PENDING',
    start_date      DATE,
    end_date        DATE,
    amount          DECIMAL(12,2),
    comment         TEXT,
    created_at      TIMESTAMP DEFAULT NOW(),
    updated_at      TIMESTAMP DEFAULT NOW(),
    reviewed_by     BIGINT REFERENCES users(id),
    reviewed_at     TIMESTAMP,
    rejection_reason TEXT
);

CREATE INDEX idx_hr_requests_employee ON hr_requests(employee_id);
CREATE INDEX idx_hr_requests_status   ON hr_requests(status);
CREATE INDEX idx_hr_requests_type     ON hr_requests(type);

-- Seed: demo requests
INSERT INTO hr_requests (employee_id, type, status, start_date, end_date, comment, created_at) VALUES
(30, 'VACATION', 'APPROVED',  '2026-11-01', '2026-11-14', 'Ежегодный отпуск',                       '2026-09-15 10:00:00'),
(31, 'VACATION', 'PENDING',   '2026-12-20', '2026-12-31', 'Новогодние каникулы',                    '2026-10-10 09:30:00'),
(35, 'VACATION', 'REJECTED',  '2026-10-25', '2026-11-05', 'Семейные обстоятельства',                '2026-10-05 14:20:00'),
(8,  'VACATION', 'PENDING',   '2026-11-15', '2026-11-22', 'Отпуск по семейным обстоятельствам',     '2026-10-12 11:00:00'),
(15, 'ADVANCE',  'PENDING',   NULL,          NULL,         'Аванс за октябрь',                       '2026-10-14 16:45:00'),
(32, 'ADVANCE',  'APPROVED',  NULL,          NULL,         'Нужен аванс на переезд',                 '2026-10-01 08:00:00'),
(21, 'VACATION', 'APPROVED',  '2026-09-01', '2026-09-10', 'Отпуск в сентябре',                      '2026-08-15 10:00:00'),
(12, 'ADVANCE',  'REJECTED',  NULL,          NULL,         'Аванс — не одобрен бюджетом',            '2026-09-20 09:00:00');

UPDATE hr_requests SET amount = 150000 WHERE type = 'ADVANCE' AND employee_id = 15;
UPDATE hr_requests SET amount = 200000 WHERE type = 'ADVANCE' AND employee_id = 32;
UPDATE hr_requests SET amount = 100000 WHERE type = 'ADVANCE' AND employee_id = 12;

UPDATE hr_requests SET reviewed_by = 1, reviewed_at = '2026-09-20 12:00:00' WHERE status = 'APPROVED' AND employee_id = 30;
UPDATE hr_requests SET reviewed_by = 1, reviewed_at = '2026-10-02 14:00:00' WHERE status = 'APPROVED' AND employee_id = 32;
UPDATE hr_requests SET reviewed_by = 1, reviewed_at = '2026-08-20 09:00:00' WHERE status = 'APPROVED' AND employee_id = 21;
UPDATE hr_requests SET reviewed_by = 1, reviewed_at = '2026-10-07 10:00:00', rejection_reason = 'Невозможно в данный период — пиковая нагрузка' WHERE status = 'REJECTED' AND employee_id = 35;
UPDATE hr_requests SET reviewed_by = 1, reviewed_at = '2026-09-22 11:00:00', rejection_reason = 'Бюджет на аванс исчерпан' WHERE status = 'REJECTED' AND employee_id = 12;
