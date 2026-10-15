-- =============================================
-- V1: Full database schema for Digital HR
-- =============================================

-- Users (auth)
CREATE TABLE users (
    id          BIGSERIAL PRIMARY KEY,
    email       VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role        VARCHAR(50)  NOT NULL,
    employee_id BIGINT,
    department_id BIGINT,
    is_active   BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP DEFAULT NOW(),
    updated_at  TIMESTAMP DEFAULT NOW()
);

-- Organization tree (self-referencing)
CREATE TABLE departments (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    type        VARCHAR(50)  NOT NULL,
    parent_id   BIGINT REFERENCES departments(id),
    city        VARCHAR(100),
    level       INT NOT NULL DEFAULT 0,
    is_active   BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP DEFAULT NOW(),
    updated_at  TIMESTAMP DEFAULT NOW()
);

-- Employees
CREATE TABLE employees (
    id              BIGSERIAL PRIMARY KEY,
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    middle_name     VARCHAR(100),
    position        VARCHAR(200) NOT NULL,
    department_id   BIGINT NOT NULL REFERENCES departments(id),
    city            VARCHAR(100),
    status          VARCHAR(50)  NOT NULL DEFAULT 'ACTIVE',
    hire_date       DATE NOT NULL,
    termination_date DATE,
    email           VARCHAR(255),
    phone           VARCHAR(50),
    birth_date      DATE,
    iin             VARCHAR(12),
    created_at      TIMESTAMP DEFAULT NOW(),
    updated_at      TIMESTAMP DEFAULT NOW()
);

-- Employee documents (URL-based)
CREATE TABLE employee_documents (
    id            BIGSERIAL PRIMARY KEY,
    employee_id   BIGINT NOT NULL REFERENCES employees(id) ON DELETE CASCADE,
    document_type VARCHAR(50)  NOT NULL,
    file_name     VARCHAR(255) NOT NULL,
    file_url      VARCHAR(500) NOT NULL,
    uploaded_at   TIMESTAMP DEFAULT NOW()
);

-- Employee audit log
CREATE TABLE employee_audit_log (
    id          BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL REFERENCES employees(id),
    field_name  VARCHAR(100) NOT NULL,
    old_value   TEXT,
    new_value   TEXT,
    changed_by  BIGINT REFERENCES users(id),
    changed_at  TIMESTAMP DEFAULT NOW()
);

-- Vacancies
CREATE TABLE vacancies (
    id            BIGSERIAL PRIMARY KEY,
    title         VARCHAR(255) NOT NULL,
    description   TEXT,
    department_id BIGINT NOT NULL REFERENCES departments(id),
    city          VARCHAR(100),
    status        VARCHAR(50)  NOT NULL DEFAULT 'OPEN',
    requirements  TEXT,
    salary_range  VARCHAR(100),
    created_by    BIGINT REFERENCES users(id),
    created_at    TIMESTAMP DEFAULT NOW(),
    updated_at    TIMESTAMP DEFAULT NOW()
);

-- Candidates
CREATE TABLE candidates (
    id          BIGSERIAL PRIMARY KEY,
    first_name  VARCHAR(100) NOT NULL,
    last_name   VARCHAR(100) NOT NULL,
    email       VARCHAR(255),
    phone       VARCHAR(50),
    resume_url  VARCHAR(500),
    vacancy_id  BIGINT NOT NULL REFERENCES vacancies(id),
    stage       VARCHAR(50)  NOT NULL DEFAULT 'NEW',
    notes       TEXT,
    assigned_to BIGINT REFERENCES users(id),
    created_at  TIMESTAMP DEFAULT NOW(),
    updated_at  TIMESTAMP DEFAULT NOW()
);

-- Onboarding templates
CREATE TABLE onboarding_templates (
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    department_id BIGINT REFERENCES departments(id),
    created_by    BIGINT REFERENCES users(id),
    created_at    TIMESTAMP DEFAULT NOW()
);

-- Onboarding template items
CREATE TABLE onboarding_template_items (
    id          BIGSERIAL PRIMARY KEY,
    template_id BIGINT NOT NULL REFERENCES onboarding_templates(id) ON DELETE CASCADE,
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    order_index INT NOT NULL DEFAULT 0
);

-- Onboarding checklists (instances)
CREATE TABLE onboarding_checklists (
    id          BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL REFERENCES employees(id),
    template_id BIGINT NOT NULL REFERENCES onboarding_templates(id),
    status      VARCHAR(50) NOT NULL DEFAULT 'IN_PROGRESS',
    created_at  TIMESTAMP DEFAULT NOW(),
    completed_at TIMESTAMP
);

-- Onboarding tasks
CREATE TABLE onboarding_tasks (
    id               BIGSERIAL PRIMARY KEY,
    checklist_id     BIGINT NOT NULL REFERENCES onboarding_checklists(id) ON DELETE CASCADE,
    template_item_id BIGINT REFERENCES onboarding_template_items(id),
    title            VARCHAR(255) NOT NULL,
    description      TEXT,
    is_completed     BOOLEAN NOT NULL DEFAULT FALSE,
    completed_at     TIMESTAMP,
    due_date         DATE
);

-- =============================================
-- Indexes
-- =============================================
CREATE INDEX idx_departments_parent   ON departments(parent_id);
CREATE INDEX idx_departments_type     ON departments(type);
CREATE INDEX idx_employees_department ON employees(department_id);
CREATE INDEX idx_employees_city       ON employees(city);
CREATE INDEX idx_employees_status     ON employees(status);
CREATE INDEX idx_employees_name       ON employees(last_name, first_name);
CREATE INDEX idx_vacancies_department ON vacancies(department_id);
CREATE INDEX idx_vacancies_status     ON vacancies(status);
CREATE INDEX idx_vacancies_city       ON vacancies(city);
CREATE INDEX idx_candidates_vacancy   ON candidates(vacancy_id);
CREATE INDEX idx_candidates_stage     ON candidates(stage);
CREATE INDEX idx_audit_employee       ON employee_audit_log(employee_id);
CREATE INDEX idx_audit_changed_at     ON employee_audit_log(changed_at);
CREATE INDEX idx_onb_checklist_emp    ON onboarding_checklists(employee_id);
