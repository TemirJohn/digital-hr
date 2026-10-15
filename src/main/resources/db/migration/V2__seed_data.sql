-- =============================================
-- V2: Seed data — JanaPost demo company
-- =============================================

-- ===================
-- 1. DEPARTMENTS (org tree)
-- ===================

-- Level 0: Head Office
INSERT INTO departments (id, name, type, parent_id, city, level) VALUES
(1, 'JanaPost — Головной офис', 'HEAD_OFFICE', NULL, 'Алматы', 0);

-- Level 1: Blocks
INSERT INTO departments (id, name, type, parent_id, city, level) VALUES
(2, 'C-Level руководство',      'BLOCK', 1, 'Алматы', 1),
(3, 'Коммерческий блок',        'BLOCK', 1, 'Алматы', 1),
(4, 'Технический блок',         'BLOCK', 1, 'Алматы', 1),
(5, 'HR и юридический блок',    'BLOCK', 1, 'Алматы', 1),
(6, 'Управление франшизами',    'BLOCK', 1, 'Алматы', 1);

-- Level 2: Cities
INSERT INTO departments (id, name, type, parent_id, city, level) VALUES
(7,  'Филиал Алматы',     'CITY', 6, 'Алматы',    2),
(8,  'Филиал Астана',     'CITY', 6, 'Астана',    2),
(9,  'Филиал Караганда',  'CITY', 6, 'Караганда', 2),
(10, 'Филиал Актау',      'CITY', 6, 'Актау',     2),
(11, 'Филиал Тараз',      'CITY', 6, 'Тараз',     2);

-- Level 3: Franchises
INSERT INTO departments (id, name, type, parent_id, city, level) VALUES
(12, 'Франшиза Алматы-Центр',    'FRANCHISE', 7,  'Алматы',    3),
(13, 'Франшиза Алматы-Юг',       'FRANCHISE', 7,  'Алматы',    3),
(14, 'Франшиза Астана-Центр',    'FRANCHISE', 8,  'Астана',    3),
(15, 'Франшиза Астана-Север',    'FRANCHISE', 8,  'Астана',    3),
(16, 'Франшиза Караганда-1',     'FRANCHISE', 9,  'Караганда', 3),
(17, 'Франшиза Актау-1',         'FRANCHISE', 10, 'Актау',     3),
(18, 'Франшиза Тараз-1',         'FRANCHISE', 11, 'Тараз',     3);

SELECT setval('departments_id_seq', 18);

-- ===================
-- 2. EMPLOYEES (50 total)
-- ===================

-- C-Level (dept 2)
INSERT INTO employees (first_name, last_name, middle_name, position, department_id, city, status, hire_date, email, phone) VALUES
('Арман',    'Сулейменов', 'Бекович',     'CEO', 2, 'Алматы', 'ACTIVE', '2018-03-15', 'a.suleymenov@janapost.kz', '+77001234501'),
('Динара',   'Касымова',   'Ержановна',   'COO', 2, 'Алматы', 'ACTIVE', '2018-06-01', 'd.kasymova@janapost.kz',   '+77001234502'),
('Бауыржан', 'Нурланов',   'Серікұлы',    'CFO', 2, 'Алматы', 'ACTIVE', '2019-01-10', 'b.nurlanov@janapost.kz',   '+77001234503');

-- Commercial (dept 3)
INSERT INTO employees (first_name, last_name, middle_name, position, department_id, city, status, hire_date, email, phone) VALUES
('Айгуль', 'Тлеубаева',  'Маратовна',  'CMO',                   3, 'Алматы', 'ACTIVE', '2019-04-01', 'a.tleubaeva@janapost.kz',  '+77001234504'),
('Серік',  'Жанабаев',   'Болатұлы',   'CSO',                   3, 'Алматы', 'ACTIVE', '2019-07-15', 's.zhanabaev@janapost.kz',  '+77001234505'),
('Маржан', 'Ибрагимова', NULL,          'Менеджер по продажам', 3, 'Алматы', 'ACTIVE', '2020-02-01', 'm.ibragimova@janapost.kz', '+77001234506');

-- Tech (dept 4)
INSERT INTO employees (first_name, last_name, middle_name, position, department_id, city, status, hire_date, email, phone) VALUES
('Ерболат', 'Ахметов',    'Нурланович', 'CTO',              4, 'Алматы', 'ACTIVE', '2019-02-01', 'e.akhmetov@janapost.kz',    '+77001234507'),
('Алия',    'Сагинтаева', 'Кайратовна', 'Product Owner',    4, 'Алматы', 'ACTIVE', '2020-05-15', 'a.sagintaeva@janapost.kz',  '+77001234508'),
('Даулет',  'Оразбеков',  NULL,          'Senior Developer', 4, 'Алматы', 'ACTIVE', '2021-01-10', 'd.orazbekov@janapost.kz',   '+77001234509'),
('Камила',  'Жумабаева',  NULL,          'Developer',        4, 'Алматы', 'ACTIVE', '2022-03-01', 'k.zhumabaeva@janapost.kz',  '+77001234510');

-- HR & Legal (dept 5)
INSERT INTO employees (first_name, last_name, middle_name, position, department_id, city, status, hire_date, email, phone) VALUES
('Гульнара', 'Муратова',      'Сериковна', 'CHRO',        5, 'Алматы', 'ACTIVE', '2018-09-01', 'g.muratova@janapost.kz',      '+77001234511'),
('Асель',    'Бектурсынова',  NULL,         'HR-менеджер', 5, 'Алматы', 'ACTIVE', '2020-06-01', 'a.bektursunova@janapost.kz',  '+77001234512'),
('Нұрлан',   'Тәшенов',      NULL,         'Юрист',       5, 'Алматы', 'ACTIVE', '2021-02-15', 'n.tashenov@janapost.kz',      '+77001234513');

-- Franchise HQ (dept 6)
INSERT INTO employees (first_name, last_name, middle_name, position, department_id, city, status, hire_date, email, phone) VALUES
('Рустам', 'Калиев', 'Ерланович', 'Главный менеджер по франшизам', 6, 'Алматы', 'ACTIVE', '2019-03-01', 'r.kaliev@janapost.kz', '+77001234514');

-- City managers
INSERT INTO employees (first_name, last_name, position, department_id, city, status, hire_date, email, phone) VALUES
('Тимур',  'Сатпаев',     'Менеджер по городу', 7,  'Алматы',    'ACTIVE', '2020-01-15', 't.satpaev@janapost.kz',     '+77001234515'),
('Айнур',  'Омарова',     'Менеджер по городу', 8,  'Астана',    'ACTIVE', '2020-03-01', 'a.omarova@janapost.kz',     '+77001234516'),
('Болат',  'Кенжебаев',   'Менеджер по городу', 9,  'Караганда', 'ACTIVE', '2020-06-01', 'b.kenzhebaev@janapost.kz',  '+77001234517'),
('Жанна',  'Ускенбаева',  'Менеджер по городу', 10, 'Актау',     'ACTIVE', '2020-08-01', 'zh.uskenbaeva@janapost.kz', '+77001234518'),
('Нұрбек', 'Әлімов',      'Менеджер по городу', 11, 'Тараз',     'ACTIVE', '2021-01-01', 'n.alimov@janapost.kz',      '+77001234519');

-- Franchise owners & managers
INSERT INTO employees (first_name, last_name, position, department_id, city, status, hire_date, email, phone) VALUES
('Мурат',   'Досымов',      'Владелец франшизы',  12, 'Алматы',    'ACTIVE', '2020-05-01', 'm.dosymov@janapost.kz',     '+77001234520'),
('Сауле',   'Баймуратова',  'Менеджер франшизы',  12, 'Алматы',    'ACTIVE', '2020-07-01', 's.baimuratova@janapost.kz', '+77001234521'),
('Ернар',   'Токтаров',     'Владелец франшизы',  13, 'Алматы',    'ACTIVE', '2020-09-01', 'e.toktarov@janapost.kz',    '+77001234522'),
('Дана',    'Жексенбаева',  'Менеджер франшизы',  13, 'Алматы',    'ACTIVE', '2021-01-15', 'd.zheksenbaeva@janapost.kz','+77001234523'),
('Канат',   'Мустафин',     'Владелец франшизы',  14, 'Астана',    'ACTIVE', '2020-06-15', 'k.mustafin@janapost.kz',    '+77001234524'),
('Лаура',   'Нуржанова',    'Менеджер франшизы',  14, 'Астана',    'ACTIVE', '2020-10-01', 'l.nurzhanova@janapost.kz',  '+77001234525'),
('Ербол',   'Сейтказин',    'Владелец франшизы',  15, 'Астана',    'ACTIVE', '2021-02-01', 'e.seitkazin@janapost.kz',   '+77001234526'),
('Алтынай', 'Касенова',     'Менеджер франшизы',  15, 'Астана',    'ACTIVE', '2021-04-01', 'a.kasenova@janapost.kz',    '+77001234527'),
('Жандос',  'Бейсенов',     'Владелец франшизы',  16, 'Караганда', 'ACTIVE', '2021-05-15', 'zh.beisenov@janapost.kz',   '+77001234528'),
('Мадина',  'Турсынбаева',  'Менеджер франшизы',  16, 'Караганда', 'ACTIVE', '2021-08-01', 'm.tursynbaeva@janapost.kz', '+77001234529');

-- Franchise workers
INSERT INTO employees (first_name, last_name, position, department_id, city, status, hire_date, email, phone) VALUES
('Бақыт',     'Ермеков',          'Курьер',   12, 'Алматы',    'ACTIVE', '2021-03-01', 'b.ermekov@janapost.kz',       '+77001234530'),
('Гүлім',     'Сәрсенбаева',     'Оператор', 12, 'Алматы',    'ACTIVE', '2021-06-15', 'g.sarsenbaeva@janapost.kz',   '+77001234531'),
('Азамат',    'Нұрмағамбетов',   'Курьер',   12, 'Алматы',    'ACTIVE', '2022-01-10', 'a.nurmagambetov@janapost.kz', '+77001234532'),
('Айжан',     'Мұхамеджанова',   'Оператор', 13, 'Алматы',    'ACTIVE', '2021-11-01', 'a.mukhamedzhanova@janapost.kz','+77001234533'),
('Нұрсұлтан', 'Байділдаев',      'Курьер',   13, 'Алматы',    'ACTIVE', '2022-04-01', 'n.baildildaev@janapost.kz',   '+77001234534'),
('Мәдина',    'Қалиева',          'Курьер',   14, 'Астана',    'ACTIVE', '2021-09-01', 'm.kalieva@janapost.kz',       '+77001234535'),
('Ержан',     'Сапарбеков',       'Оператор', 14, 'Астана',    'ACTIVE', '2022-02-01', 'e.saparbekov@janapost.kz',    '+77001234536'),
('Аружан',    'Байтасова',        'Курьер',   15, 'Астана',    'ACTIVE', '2022-05-15', 'a.baitasova@janapost.kz',     '+77001234537'),
('Дастан',    'Қожахметов',       'Оператор', 15, 'Астана',    'ACTIVE', '2022-07-01', 'd.kozhakhmetov@janapost.kz',  '+77001234538'),
('Самал',     'Құрманғалиева',   'Курьер',   16, 'Караганда', 'ACTIVE', '2022-03-01', 's.kurmangalieva@janapost.kz', '+77001234539'),
('Берик',     'Жүнісов',          'Оператор', 16, 'Караганда', 'ACTIVE', '2022-06-01', 'b.zhunisov@janapost.kz',      '+77001234540'),
('Меруерт',   'Нұрпейісова',     'Курьер',   17, 'Актау',     'ACTIVE', '2022-08-01', 'm.nurpeisova@janapost.kz',    '+77001234541'),
('Бекзат',    'Төлеуов',          'Оператор', 17, 'Актау',     'ACTIVE', '2022-09-15', 'b.toleuov@janapost.kz',       '+77001234542'),
('Фариза',    'Әбілова',          'Курьер',   18, 'Тараз',     'ACTIVE', '2022-10-01', 'f.abilova@janapost.kz',       '+77001234543'),
('Олжас',     'Сүлеев',           'Оператор', 18, 'Тараз',     'ACTIVE', '2022-11-15', 'o.suleev@janapost.kz',        '+77001234544');

-- Terminated employees (for turnover analytics)
INSERT INTO employees (first_name, last_name, position, department_id, city, status, hire_date, termination_date, email, phone) VALUES
('Талғат', 'Есенов',       'Курьер',   12, 'Алматы',    'TERMINATED', '2021-01-10', '2023-06-30', 't.esenov@janapost.kz',      '+77001234545'),
('Раушан', 'Мұстафина',    'Оператор', 14, 'Астана',    'TERMINATED', '2021-05-01', '2023-09-15', 'r.mustafina@janapost.kz',   '+77001234546'),
('Нұрбол', 'Қасымов',      'Курьер',   16, 'Караганда', 'TERMINATED', '2022-01-10', '2024-03-31', 'n.kasymov_k@janapost.kz',   '+77001234547'),
('Сәуле',  'Жанәбілова',   'Менеджер', 13, 'Алматы',    'TERMINATED', '2020-08-01', '2024-01-15', 's.zhanabildaeva@janapost.kz','+77001234548');

-- On leave employees
INSERT INTO employees (first_name, last_name, position, department_id, city, status, hire_date, email, phone) VALUES
('Әсел',   'Баймұратова',    'HR-специалист', 5,  'Алматы', 'ON_LEAVE', '2021-07-01', 'a.baimuratova2@janapost.kz', '+77001234549'),
('Қайрат', 'Жолдасбеков',    'Курьер',        17, 'Актау',  'ON_LEAVE', '2022-04-15', 'k.zholdasbekov@janapost.kz', '+77001234550');

-- ===================
-- 3. VACANCIES
-- ===================
INSERT INTO vacancies (title, description, department_id, city, status, requirements, salary_range) VALUES
('Курьер',                'Доставка посылок по городу. Полная занятость.',           12, 'Алматы',    'OPEN',    'Водительские права категории B, знание города',           '150000-250000 KZT'),
('Оператор call-центра',  'Обработка входящих звонков клиентов.',                    14, 'Астана',    'OPEN',    'Грамотная речь, знание казахского и русского языков',      '120000-180000 KZT'),
('Менеджер франшизы',     'Управление франшизой в Тараз.',                           18, 'Тараз',     'OPEN',    'Опыт управления от 2 лет, знание логистики',              '300000-450000 KZT'),
('Frontend-разработчик',  'Разработка веб-интерфейсов внутренних систем.',            4,  'Алматы',    'OPEN',    'React/Vue.js, 2+ лет опыта',                              '500000-800000 KZT'),
('HR-менеджер',           'Подбор персонала для франшиз.',                            5,  'Алматы',    'OPEN',    'Опыт в HR от 1 года, знание ТК РК',                       '250000-350000 KZT'),
('Курьер',                'Доставка в районе Караганды.',                             16, 'Караганда', 'ON_HOLD', 'Наличие транспорта',                                       '130000-200000 KZT'),
('Складской работник',    'Работа на складе, приёмка и отгрузка.',                    15, 'Астана',    'CLOSED',  'Физическая выносливость',                                  '100000-150000 KZT');

-- ===================
-- 4. CANDIDATES
-- ===================
INSERT INTO candidates (first_name, last_name, email, phone, vacancy_id, stage, notes) VALUES
('Темірлан', 'Абдуллаев',   'temirlan.a@gmail.com',  '+77051112233', 1, 'NEW',       'Откликнулся через HeadHunter'),
('Мағжан',   'Серікбаев',   'magzhan.s@gmail.com',   '+77052223344', 1, 'SCREENING', 'Есть опыт курьером 1 год'),
('Асия',     'Мұхтарова',   'asiya.m@gmail.com',     '+77053334455', 1, 'INTERVIEW', 'Хорошее резюме, интервью назначено'),
('Данияр',   'Ермекбаев',   'daniyar.e@gmail.com',   '+77054445566', 2, 'NEW',        NULL),
('Жұлдыз',   'Тоқтарова',   'zhuldyz.t@gmail.com',   '+77055556677', 2, 'OFFER',     'Предложен оффер 150000 KZT'),
('Ринат',    'Хасенов',     'rinat.h@gmail.com',     '+77056667788', 3, 'INTERVIEW', 'Опыт управления 3 года'),
('Аида',     'Сейтжанова',  'aida.s@gmail.com',      '+77057778899', 4, 'SCREENING', 'React 3 года, portfolio on GitHub'),
('Марат',    'Жумабеков',   'marat.zh@gmail.com',    '+77058889900', 4, 'NEW',        NULL),
('Нүргүл',   'Оспанова',    'nurgul.o@gmail.com',    '+77059990011', 5, 'INTERVIEW', 'HR-опыт 2 года в retail'),
('Руслан',   'Кәрімов',     'ruslan.k@gmail.com',    '+77050001122', 5, 'REJECTED',  'Не прошёл интервью'),
('Алмас',    'Базарбаев',   'almas.b@gmail.com',     '+77051113344', 7, 'HIRED',     'Принят на работу');

-- ===================
-- 5. ONBOARDING TEMPLATES
-- ===================
INSERT INTO onboarding_templates (id, name, department_id, created_by) VALUES
(1, 'Стандартный онбординг — Курьер',         NULL, NULL),
(2, 'Стандартный онбординг — Офис',           NULL, NULL),
(3, 'Онбординг — Менеджер франшизы',          NULL, NULL);

SELECT setval('onboarding_templates_id_seq', 3);

-- Template items: Courier
INSERT INTO onboarding_template_items (template_id, title, description, order_index) VALUES
(1, 'Оформление документов',              'Подписать трудовой договор, NDA, согласие на обработку ПД', 1),
(1, 'Получение униформы',                  'Получить форму и бейдж на складе',                         2),
(1, 'Инструктаж по ТБ',                    'Пройти инструктаж по технике безопасности',               3),
(1, 'Обучение по маршрутам',               'Изучить маршруты доставки района',                        4),
(1, 'Установка приложения',                'Установить мобильное приложение курьера',                 5),
(1, 'Первый рабочий день с наставником',   'Провести первый день с опытным курьером',                6);

-- Template items: Office
INSERT INTO onboarding_template_items (template_id, title, description, order_index) VALUES
(2, 'Оформление документов',              'Подписать трудовой договор, NDA',                         1),
(2, 'Настройка рабочего места',            'Получить ноутбук, настроить почту и доступы',            2),
(2, 'Знакомство с командой',               'Встреча с руководителем и коллегами',                    3),
(2, 'Обучение внутренним системам',        'Пройти обучение по CRM и ERP',                          4),
(2, 'Ознакомление с политиками',           'Прочитать и подписать внутренние политики компании',     5);

-- Template items: Franchise Manager
INSERT INTO onboarding_template_items (template_id, title, description, order_index) VALUES
(3, 'Подписание договора франшизы',        'Изучить и подписать договор франшизы',                   1),
(3, 'Обучение стандартам JanaPost',        'Пройти курс по стандартам обслуживания',                2),
(3, 'Настройка точки',                      'Подготовить помещение по стандартам бренда',             3),
(3, 'Обучение IT-системам',                'Настроить POS и систему трекинга',                      4),
(3, 'Набор персонала',                      'Нанять минимальный штат (2 курьера, 1 оператор)',       5),
(3, 'Пробный запуск',                       'Провести тестовую доставку',                            6),
(3, 'Официальное открытие',                'Запустить точку в работу',                               7);

-- ===================
-- 6. ONBOARDING CHECKLISTS (for recent hires)
-- ===================
INSERT INTO onboarding_checklists (id, employee_id, template_id, status) VALUES
(1, 32, 1, 'IN_PROGRESS'),
(2, 37, 1, 'IN_PROGRESS');

SELECT setval('onboarding_checklists_id_seq', 2);

-- Tasks for checklist 1 (Азамат)
INSERT INTO onboarding_tasks (checklist_id, title, description, is_completed, completed_at, due_date) VALUES
(1, 'Оформление документов',            'Подписать трудовой договор, NDA, согласие на обработку ПД', true,  '2022-01-11', '2022-01-12'),
(1, 'Получение униформы',                'Получить форму и бейдж на складе',                         true,  '2022-01-11', '2022-01-12'),
(1, 'Инструктаж по ТБ',                  'Пройти инструктаж по технике безопасности',               true,  '2022-01-12', '2022-01-14'),
(1, 'Обучение по маршрутам',             'Изучить маршруты доставки района',                        false, NULL,          '2022-01-16'),
(1, 'Установка приложения',              'Установить мобильное приложение курьера',                 false, NULL,          '2022-01-13'),
(1, 'Первый рабочий день с наставником', 'Провести первый день с опытным курьером',                false, NULL,          '2022-01-17');

-- Tasks for checklist 2 (Аружан)
INSERT INTO onboarding_tasks (checklist_id, title, description, is_completed, completed_at, due_date) VALUES
(2, 'Оформление документов',            'Подписать трудовой договор, NDA, согласие на обработку ПД', true,  '2022-05-16', '2022-05-17'),
(2, 'Получение униформы',                'Получить форму и бейдж на складе',                         false, NULL,          '2022-05-18'),
(2, 'Инструктаж по ТБ',                  'Пройти инструктаж по технике безопасности',               false, NULL,          '2022-05-19'),
(2, 'Обучение по маршрутам',             'Изучить маршруты доставки района',                        false, NULL,          '2022-05-21'),
(2, 'Установка приложения',              'Установить мобильное приложение курьера',                 false, NULL,          '2022-05-18'),
(2, 'Первый рабочий день с наставником', 'Провести первый день с опытным курьером',                false, NULL,          '2022-05-22');
