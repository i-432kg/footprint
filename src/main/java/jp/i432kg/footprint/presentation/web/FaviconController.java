package jp.i432kg.footprint.presentation.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * favicon の自動リクエストを静的 SVG へ案内する controller です。
 */
@Controller
public class FaviconController {

    /**
     * `/favicon.ico` へのアクセスを静的な SVG favicon にリダイレクトします。
     *
     * @return favicon のリダイレクト先
     */
    @GetMapping("/favicon.ico")
    public String favicon() {
        return "redirect:/favicon.svg";
    }
}
