package com.codeQuest.Chatterly.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/homepage")
    public String homePage() {
        return "redirect:/index.html";
    }
}
