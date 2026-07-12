-- ============================================================
-- AI Interview Preparation System - MySQL Database Schema
-- ============================================================

CREATE DATABASE IF NOT EXISTS ai_interview_prep CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ai_interview_prep;

-- ---------------------------------------------------------------
-- USERS TABLE
-- ---------------------------------------------------------------
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('STUDENT', 'ADMIN') DEFAULT 'STUDENT',
    avatar_url VARCHAR(500),
    phone VARCHAR(20),
    college VARCHAR(200),
    graduation_year INT,
    target_role VARCHAR(100),
    experience_level ENUM('FRESHER', 'JUNIOR', 'MID', 'SENIOR') DEFAULT 'FRESHER',
    total_tests INT DEFAULT 0,
    total_score DECIMAL(10,2) DEFAULT 0.00,
    streak_days INT DEFAULT 0,
    last_active DATE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------------
-- TOPICS TABLE
-- ---------------------------------------------------------------
CREATE TABLE topics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    icon VARCHAR(50),
    color VARCHAR(20),
    category ENUM('LANGUAGE', 'FRAMEWORK', 'DATABASE', 'DSA', 'SYSTEM_DESIGN', 'OTHER') DEFAULT 'LANGUAGE',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------------
-- TESTS TABLE
-- ---------------------------------------------------------------
CREATE TABLE tests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    topic_id BIGINT NOT NULL,
    language VARCHAR(50) NOT NULL,
    difficulty ENUM('EASY', 'MEDIUM', 'HARD') NOT NULL,
    test_type ENUM('MCQ', 'CODING', 'THEORY', 'MIXED', 'MOCK_INTERVIEW') NOT NULL,
    status ENUM('IN_PROGRESS', 'COMPLETED', 'ABANDONED') DEFAULT 'IN_PROGRESS',
    total_questions INT DEFAULT 0,
    attempted_questions INT DEFAULT 0,
    correct_answers INT DEFAULT 0,
    score DECIMAL(5,2) DEFAULT 0.00,
    time_taken INT DEFAULT 0 COMMENT 'in seconds',
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (topic_id) REFERENCES topics(id) ON DELETE CASCADE
);

-- ---------------------------------------------------------------
-- QUESTIONS TABLE
-- ---------------------------------------------------------------
CREATE TABLE questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    test_id BIGINT NOT NULL,
    question_number INT NOT NULL,
    question_type ENUM('MCQ', 'CODING', 'THEORY', 'OUTPUT', 'DEBUGGING', 'FILL_BLANK') NOT NULL,
    question_text LONGTEXT NOT NULL,
    options JSON COMMENT 'For MCQ: [{label, text}]',
    correct_answer TEXT,
    explanation TEXT,
    language VARCHAR(50),
    difficulty ENUM('EASY', 'MEDIUM', 'HARD'),
    points INT DEFAULT 10,
    ai_generated BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (test_id) REFERENCES tests(id) ON DELETE CASCADE
);

-- ---------------------------------------------------------------
-- ANSWERS TABLE
-- ---------------------------------------------------------------
CREATE TABLE answers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    test_id BIGINT NOT NULL,
    user_answer LONGTEXT,
    is_correct BOOLEAN DEFAULT FALSE,
    points_earned DECIMAL(5,2) DEFAULT 0.00,
    time_spent INT DEFAULT 0 COMMENT 'in seconds',
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (test_id) REFERENCES tests(id) ON DELETE CASCADE
);

-- ---------------------------------------------------------------
-- AI FEEDBACK TABLE
-- ---------------------------------------------------------------
CREATE TABLE ai_feedback (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    test_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    overall_score DECIMAL(5,2),
    grade VARCHAR(5),
    strengths LONGTEXT,
    weaknesses LONGTEXT,
    mistakes LONGTEXT,
    improvement_tips LONGTEXT,
    study_recommendations LONGTEXT,
    detailed_feedback LONGTEXT,
    next_topics JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (test_id) REFERENCES tests(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ---------------------------------------------------------------
-- PROGRESS TABLE
-- ---------------------------------------------------------------
CREATE TABLE progress (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    topic_id BIGINT NOT NULL,
    language VARCHAR(50) NOT NULL,
    total_tests INT DEFAULT 0,
    avg_score DECIMAL(5,2) DEFAULT 0.00,
    best_score DECIMAL(5,2) DEFAULT 0.00,
    easy_completed INT DEFAULT 0,
    medium_completed INT DEFAULT 0,
    hard_completed INT DEFAULT 0,
    mastery_level ENUM('BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'EXPERT') DEFAULT 'BEGINNER',
    last_tested DATE,
    UNIQUE KEY unique_user_topic_lang (user_id, topic_id, language),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (topic_id) REFERENCES topics(id) ON DELETE CASCADE
);

-- ---------------------------------------------------------------
-- MOCK INTERVIEWS TABLE
-- ---------------------------------------------------------------
CREATE TABLE mock_interviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    topic_id BIGINT NOT NULL,
    language VARCHAR(50) NOT NULL,
    difficulty ENUM('EASY', 'MEDIUM', 'HARD') NOT NULL,
    target_role VARCHAR(100),
    company_type ENUM('STARTUP', 'MNC', 'PRODUCT', 'SERVICE') DEFAULT 'PRODUCT',
    interview_transcript LONGTEXT,
    overall_score DECIMAL(5,2),
    communication_score DECIMAL(5,2),
    technical_score DECIMAL(5,2),
    problem_solving_score DECIMAL(5,2),
    duration INT DEFAULT 0 COMMENT 'in minutes',
    status ENUM('SCHEDULED', 'IN_PROGRESS', 'COMPLETED') DEFAULT 'IN_PROGRESS',
    feedback LONGTEXT,
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (topic_id) REFERENCES topics(id) ON DELETE CASCADE
);

-- ---------------------------------------------------------------
-- STUDY PLANS TABLE
-- ---------------------------------------------------------------
CREATE TABLE study_plans (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    plan_json LONGTEXT NOT NULL COMMENT 'JSON with weeks, days, topics',
    duration_weeks INT DEFAULT 4,
    target_role VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ---------------------------------------------------------------
-- SEED DATA - Topics
-- ---------------------------------------------------------------
INSERT INTO topics (name, slug, description, icon, color, category) VALUES
('Java', 'java', 'Core Java, OOP, Collections, Multithreading, Java 8+', 'java', '#f89820', 'LANGUAGE'),
('Python', 'python', 'Python fundamentals, OOP, Libraries, Scripting', 'python', '#3572A5', 'LANGUAGE'),
('JavaScript', 'javascript', 'ES6+, DOM, Async, Node.js concepts', 'javascript', '#f7df1e', 'LANGUAGE'),
('C', 'c', 'C programming, pointers, memory management', 'c', '#555555', 'LANGUAGE'),
('C++', 'cpp', 'C++ OOP, STL, Templates, Memory', 'cpp', '#00599C', 'LANGUAGE'),
('C#', 'csharp', 'C# .NET, OOP, LINQ, async/await', 'csharp', '#178600', 'LANGUAGE'),
('Go', 'go', 'Go language, goroutines, channels, packages', 'go', '#00ADD8', 'LANGUAGE'),
('Kotlin', 'kotlin', 'Kotlin for Android & JVM, coroutines', 'kotlin', '#7F52FF', 'LANGUAGE'),
('TypeScript', 'typescript', 'TypeScript, types, interfaces, generics', 'typescript', '#3178C6', 'LANGUAGE'),
('PHP', 'php', 'PHP fundamentals, OOP, web concepts', 'php', '#8892BF', 'LANGUAGE'),
('SQL', 'sql', 'SQL queries, joins, indexing, optimization', 'sql', '#E38C00', 'DATABASE'),
('DSA', 'dsa', 'Data Structures & Algorithms, complexity analysis', 'dsa', '#e74c3c', 'DSA'),
('Spring Boot', 'spring-boot', 'Spring Boot, REST, JPA, Security', 'spring', '#6DB33F', 'FRAMEWORK'),
('System Design', 'system-design', 'High-level and low-level system design', 'system', '#9b59b6', 'SYSTEM_DESIGN');

-- ---------------------------------------------------------------
-- SEED DATA - Admin User (password: Admin@123)
-- ---------------------------------------------------------------
INSERT INTO users (full_name, username, email, password, role) VALUES
('Admin User', 'admin', 'admin@interviewprep.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TqxGOFBtjnJUhNH.eFLhGjKqTQCe', 'ADMIN');
