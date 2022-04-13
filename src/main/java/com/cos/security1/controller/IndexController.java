package com.cos.security1.controller;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping(value = "/test/login")
    public @ResponseBody
    String testLogin(
            Authentication authentication,
            @AuthenticationPrincipal PrincipalDetails userDetails) { // DI (의존성 주입)
        System.out.println("/test/login =========================");
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        System.out.println("authentication : " + principalDetails.getUser());

        System.out.println("userDetails : " + userDetails.getUsername());
        return "세션 정보 확인하기";
    }

    @GetMapping(value = "/test/oauth/login")
    public @ResponseBody
    String testOauthLogin(
            Authentication authentication,
            @AuthenticationPrincipal OAuth2User oAuth) { // DI (의존성 주입)
        System.out.println("/test/oauth/login =========================");
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        System.out.println("authentication : " + oAuth2User.getAttributes());
        System.out.println("oauth2User : " + oAuth.getAuthorities());

        return "OAuth 세션 정보 확인하기";
    }

    @GetMapping({"", "/"})
    public String index(Model model) {
        return "index";
    }

    @GetMapping("/user")
    public @ResponseBody
    String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        return "user";
    }

    @GetMapping("/admin")
    public @ResponseBody
    String admin() {
        return "admin";
    }

    @GetMapping("/manager")
    public @ResponseBody
    String manager() {
        return "manager";
    }

    // spring security 에서 해당 주소 낚아챔 - SecurityConfig 파일 생성 후 동작 안함.
    @GetMapping("/loginForm")
    public String loginForm() {
        return "loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm() {
        return "joinForm";
    }

    @PostMapping("/join")
    public String join(User user) {
        System.out.println(user);
        user.setRole("ROLE_USER");

        String rawPassword = user.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        user.setPassword(encPassword);

        userRepository.save(user); // 회원가입 잘 됨. 비밀번호 : 1234 => security 로그인을 할 수 없음. 패스워드 암호화가 안돼서
        return "redirect:/loginForm";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping(value = "/info")
    public @ResponseBody String info() {
        return "개인정보";
    }

    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/data")
    public @ResponseBody String data() {
        return "데이터 정보";
    }
}
