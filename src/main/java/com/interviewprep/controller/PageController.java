package com.interviewprep.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    @GetMapping("/") public String landing() { return "index"; }
    @GetMapping("/login") public String login() { return "login"; }
    @GetMapping("/register") public String register() { return "register"; }
    @GetMapping("/dashboard") public String dashboard() { return "dashboard"; }
    @GetMapping("/topics") public String topics() { return "topics"; }
    @GetMapping("/test") public String test() { return "test"; }
    @GetMapping("/results") public String results() { return "results"; }
    @GetMapping("/mock-interview") public String mockInterview() { return "mock-interview"; }
    @GetMapping("/progress") public String progress() { return "progress"; }
    @GetMapping("/profile") public String profile() { return "profile"; }
    @GetMapping("/study-plan") public String studyPlan() { return "study-plan"; }
    @GetMapping("/admin") public String admin() { return "admin"; }
}
