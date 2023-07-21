package aml.summeruser.controller;

import aml.summeruser.dto.LoginDto;
import aml.summeruser.dto.SinhVienDto;
import aml.summeruser.service.SinhVienService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private SinhVienService sinhVienService;


    @PostMapping(value = "/login", produces = "application/json")
    public ResponseEntity<?> login(LoginDto dto, @RequestParam(required = false) String message) {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword());
        Authentication authentication = authenticationManager.authenticate(auth);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return new ResponseEntity<>("Login successful",HttpStatus.OK);
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logoutUser(HttpServletRequest request, HttpServletResponse response){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null){
            SecurityContextHolder.clearContext();
        }
        return new ResponseEntity<>("You have been log out !", HttpStatus.OK);
    }

    @PostMapping(value = "/add",  produces = "application/json")
    public ResponseEntity<?> addNewUser(SinhVienDto dto, HttpServletRequest request,
                                        HttpServletResponse response) {
        request.getSession().setAttribute("username", dto.getUsername());
        Cookie cookie = new Cookie("username", dto.getUsername());
        cookie.setMaxAge(100);
        response.addCookie(cookie);
        String result = sinhVienService.addSinhVien(dto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/showSession", produces = "application/json")
    public ResponseEntity<?> showListUser(HttpSession session) {
        String result = sinhVienService.showListUser(session);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
