package bside.com.project308.controller;

import bside.com.project308.dto.UserPrincipal;
import bside.com.project308.dto.security.KakaoUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Slf4j
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/info")
    @ResponseBody
    public String info(Authentication authentication, @AuthenticationPrincipal UserPrincipal userPrincipal) {

        String body = authentication.toString();
        log.info("body {}", body);
        log.info("{}", userPrincipal);

        return "ok";
    }

}
