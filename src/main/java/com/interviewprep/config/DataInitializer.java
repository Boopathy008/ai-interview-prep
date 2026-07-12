package com.interviewprep.config;

import com.interviewprep.entity.Topic;
import com.interviewprep.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Seeds the topics table on first startup if it's empty.
 * This is a fallback — the canonical way is to run docs/schema.sql first.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final TopicRepository topicRepository;

    @Override
    public void run(String... args) {
        if (topicRepository.count() == 0) {
            log.info("Seeding topics...");
            seedTopics();
            log.info("Topics seeded successfully.");
        }
    }

    private void seedTopics() {
        List<Topic> topics = List.of(
            topic("Java",          "java",          "Core Java, OOP, Collections, Multithreading, Java 8+", "☕", "#f89820", Topic.Category.LANGUAGE),
            topic("Python",        "python",        "Python fundamentals, OOP, Libraries, Scripting",       "🐍", "#3572A5", Topic.Category.LANGUAGE),
            topic("JavaScript",    "javascript",    "ES6+, DOM, Async, Promises, Node.js concepts",         "🟨", "#f7df1e", Topic.Category.LANGUAGE),
            topic("TypeScript",    "typescript",    "TypeScript, types, interfaces, generics",              "🔷", "#3178C6", Topic.Category.LANGUAGE),
            topic("C",             "c",             "C programming, pointers, memory management",           "⚙️", "#555555", Topic.Category.LANGUAGE),
            topic("C++",           "cpp",           "C++ OOP, STL, Templates, Memory management",          "🔵", "#00599C", Topic.Category.LANGUAGE),
            topic("C#",            "csharp",        "C# .NET, OOP, LINQ, async/await",                     "💜", "#178600", Topic.Category.LANGUAGE),
            topic("Go",            "go",            "Go language, goroutines, channels, packages",         "🐹", "#00ADD8", Topic.Category.LANGUAGE),
            topic("Kotlin",        "kotlin",        "Kotlin for Android & JVM, coroutines",                "🎯", "#7F52FF", Topic.Category.LANGUAGE),
            topic("PHP",           "php",           "PHP fundamentals, OOP, web concepts",                 "🐘", "#8892BF", Topic.Category.LANGUAGE),
            topic("SQL",           "sql",           "SQL queries, joins, indexing, optimization",          "🗄️", "#E38C00", Topic.Category.DATABASE),
            topic("DSA",           "dsa",           "Data Structures & Algorithms, complexity analysis",   "🧮", "#e74c3c", Topic.Category.DSA),
            topic("Spring Boot",   "spring-boot",   "Spring Boot, REST, JPA, Security, Spring AI",         "🍃", "#6DB33F", Topic.Category.FRAMEWORK),
            topic("System Design", "system-design", "High-level and low-level system design",              "🏗️", "#9b59b6", Topic.Category.SYSTEM_DESIGN)
        );
        topicRepository.saveAll(topics);
    }

    private Topic topic(String name, String slug, String desc, String icon,
                        String color, Topic.Category category) {
        return Topic.builder()
                .name(name).slug(slug).description(desc)
                .icon(icon).color(color).category(category)
                .isActive(true)
                .build();
    }
}
