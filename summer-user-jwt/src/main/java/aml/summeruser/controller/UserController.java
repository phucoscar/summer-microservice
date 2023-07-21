package aml.summeruser.controller;

import aml.summercore.base.Result;
import aml.summeruser.dto.LoginDto;
import aml.summeruser.dto.SinhVienDto;
import aml.summeruser.entity.RefreshToken;
import aml.summeruser.entity.SinhVien;
import aml.summeruser.service.RefreshTokenService;
import aml.summeruser.service.SinhVienService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Optional;


@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private SinhVienService sinhVienService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @PostMapping(value = "/login", produces = "application/json")
    public ResponseEntity<?> login(LoginDto dto,
                                   @RequestParam(required = false) String message) {
        Result result = sinhVienService.checkLogin(dto);
        return new ResponseEntity<>(new Gson().toJson(result), HttpStatus.OK);
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logoutUser(HttpServletRequest request,
                                             HttpServletResponse response){
        sinhVienService.doLogout();
        return new ResponseEntity<>("You have been log out !", HttpStatus.OK);
    }

    @PostMapping(value = "/refreshtoken", produces = "application/json",
            consumes = "application/json")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshToken refreshTokenRequest) {
        String token = refreshTokenRequest.getToken();
        Result result = sinhVienService.generateNewToken(token);
        return new ResponseEntity<>(new Gson().toJson(result), HttpStatus.OK);
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
