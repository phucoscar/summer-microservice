package aml.summeruser.service;

import aml.summercore.base.Result;
import aml.summeruser.dto.LoginDto;
import aml.summeruser.dto.LoginResponse;
import aml.summeruser.dto.SinhVienDto;
import aml.summeruser.entity.RefreshToken;
import aml.summeruser.entity.Role;
import aml.summeruser.entity.SinhVien;
import aml.summeruser.repository.SinhVienRepository;
import aml.summeruser.util.JwtUtil;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SinhVienService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private SinhVienRepository sinhVienRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RefreshTokenService refreshTokenService;

    public Result checkLogin(LoginDto dto) {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword());
        try {
            Authentication authentication = authenticationManager.authenticate(auth);
            SinhVien sinhVien = sinhVienRepo.findByUsername(dto.getUsername()).get();
            String accessToken = jwtUtil.generateAccessToken(sinhVien.getMaSV(), sinhVien.getUsername(), sinhVien.getHoTen(), sinhVien.getEmail());
            List<String> roles = sinhVien.getRoles().stream().map(Role::getName).collect(Collectors.toList());
            RefreshToken refreshToken = refreshTokenService.generateRefreshToken(sinhVien.getMaSV());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return Result.success("Login successful", new LoginResponse(dto.getUsername(), accessToken, refreshToken.getToken(), roles));
        } catch (Exception e) {
            return Result.fail("Username or password is invalid");
        }
    }

    public void doLogout() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null){
            User user = (User) auth.getPrincipal();
            Optional<SinhVien> op = sinhVienRepo.findByUsername(user.getUsername());
            op.ifPresent(sinhVien -> refreshTokenService.deleteRefreshToken(sinhVien));
            SecurityContextHolder.getContext().setAuthentication(null);
        }
    }

    public Result generateNewToken(String token) {
        Optional<RefreshToken> op = refreshTokenService.findByToken(token);
        if (op.isPresent()) {
            RefreshToken refreshToken = refreshTokenService.verifyExpiration(op.get());
            if (refreshToken != null) {
                SinhVien sinhVien = refreshToken.getSinhVien();
                String username = sinhVien.getUsername();
                String newToken = jwtUtil.generateAccessToken(sinhVien.getMaSV(), sinhVien.getUsername(), sinhVien.getHoTen(), sinhVien.getEmail());
                return Result.success("Create new token success", new LoginResponse(username, newToken, refreshToken.getToken(), null));
            }
            return Result.fail("Refresh Token is expired");
        }

        return Result.fail("Refresh Token is not in database");
    }

    public String addSinhVien(SinhVienDto dto) {
        Gson gson = new Gson();
        if (dto == null)
            return gson.toJson(Result.fail());
        SinhVien sv = new SinhVien();
        sv.setHoTen(dto.getHoTen());
        sv.setNgaySinh(dto.getNgaySinh());
        sv.setCccd(dto.getCccd());
        sv.setDiaChi(dto.getDiaChi());
        sv.setUsername(dto.getUsername());
        sv.setPassword(passwordEncoder.encode(dto.getPassword()));
        SinhVien result = sinhVienRepo.save(sv);
        String response = gson.toJson(new Result(200, "Success", result));
        return response;
    }

    public String showListUser(HttpSession session) {
        String username = (String)session.getAttribute("username");
        System.out.println(username != null? username : "null");
        List<SinhVien> list = (List<SinhVien>) session.getAttribute("sinhViens");
        String response = new Gson().toJson(new Result<>(200, "Success", list != null? list : new ArrayList<>()));
        return response;
    }
}
