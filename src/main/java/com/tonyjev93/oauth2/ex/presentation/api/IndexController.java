package com.tonyjev93.oauth2.ex.presentation.api;


import com.tonyjev93.oauth2.ex.config.auth.dto.SessionUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@Controller
public class IndexController {

    //    private final PostService postService;
    private final HttpSession httpSession;

    @GetMapping("/")
    public String index(Model model) {

        System.out.println("index start");
//        model.addAttribute("posts", postService.findAllDesc());
        SessionUser user = (SessionUser) httpSession.getAttribute("user");

        if (user != null) {
            model.addAttribute("userName", user.getName());
            model.addAttribute("myName", user.getName());
        }

        return "index";
    }

    @GetMapping("/api/v1/test")
    public String test() {
        System.out.println("Test");

        return "test";
    }

    @GetMapping("/posts/save")
    public String posts() {
        System.out.println("Post");

        return "posts";
    }
}
