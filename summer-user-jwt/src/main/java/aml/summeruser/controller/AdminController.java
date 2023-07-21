package aml.summeruser.controller;

import aml.summercore.base.Result;
import aml.summeruser.util.JwtUtil;
import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping(produces = "application/json")
    public ResponseEntity<?> getInfo(HttpServletRequest request, HttpServletResponse response) {
        Claims claims = jwtUtil.getClaims(request.getHeader("Authorization"));
        Map<String, Object> map = new HashMap<>(claims);
        Result result = Result.success("Success", map);
        return new ResponseEntity<>(new Gson().toJson(result), HttpStatus.OK);
    }
}
