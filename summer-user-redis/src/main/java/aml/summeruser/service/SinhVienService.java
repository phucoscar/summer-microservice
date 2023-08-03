package aml.summeruser.service;

import aml.summercore.base.Result;
import aml.summercore.util.JsonUtil;
import aml.summeruser.dto.LoginDto;
import aml.summeruser.dto.LoginResponse;
import aml.summeruser.dto.SinhVienDto;
import aml.summeruser.entity.RefreshToken;
import aml.summeruser.entity.Role;
import aml.summeruser.entity.SinhVien;
import aml.summeruser.repository.SinhVienRepository;
import aml.summeruser.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
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

    private static final Logger logger = LoggerFactory.getLogger(SinhVienService.class);

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

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public Result checkLogin(LoginDto dto) {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword());
       logger.info("Username: {}, password: {}", dto.getUsername(), dto.getPassword());
        try {
            Authentication authentication = authenticationManager.authenticate(auth);
            SinhVien sinhVien = sinhVienRepo.findByUsername(dto.getUsername()).get();
            String accessToken = jwtUtil.generateAccessToken(sinhVien.getMaSV(), sinhVien.getUsername(), sinhVien.getHoTen(), sinhVien.getEmail());
            List<String> roles = sinhVien.getRoles().stream().map(Role::getName).collect(Collectors.toList());
            RefreshToken refreshToken = refreshTokenService.generateRefreshToken(sinhVien.getMaSV());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.info("Login success");

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

        // convert to json and save into redis cache
        String json = JsonUtil.toJsonString(result);
        redisTemplate.opsForValue().set("user:" + result.getMaSV(), json);

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

    @Cacheable(value = "sinhviens")
    public List<SinhVien> findAll() {
        return sinhVienRepo.findAll();
    }

    @CacheEvict(value = "sinhvien", key = "#id") // key sẽ tham chiếu tới param có tên trùng trong method.
    public void deleteSinhVien(int id) {
        sinhVienRepo.deleteById(id);
    }

    public Result getUserById(Integer id) {
        String json = redisTemplate.opsForValue().get("user:" + id);
        if (json == null) {
            Optional<SinhVien> op = sinhVienRepo.findById(id);
            if (op.isPresent()) {
                SinhVien sinhVien = op.get();
                json = JsonUtil.toJsonString(sinhVien);
                redisTemplate.opsForValue().set("user:" + sinhVien.getMaSV(), json);
                return Result.success("Success", sinhVien);
            } else {
                return Result.fail("User not found!");
            }
        } else {
            SinhVien sinhVien = JsonUtil.parseObject(json, SinhVien.class);
            return Result.success("Success", sinhVien);
        }
    }
}
