package jp.i432kg.footprint.presentation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootController {

    @GetMapping("/")
    public String index() {
        return "timeline";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/map")
    public String map() {
        return "map";
    }

    @GetMapping("/mypage")
    public String mypage() {
        return "mypage";
    }

    @GetMapping("/search")
    public String search() {
        return "search";
    }

    @GetMapping("/timeline")
    public String timeline() {
        return "timeline";
    }
}
