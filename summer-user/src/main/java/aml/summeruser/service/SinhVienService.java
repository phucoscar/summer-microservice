package aml.summeruser.service;

import aml.summercore.base.Result;
import aml.summeruser.dto.SinhVienDto;
import aml.summeruser.entity.SinhVien;
import aml.summeruser.repository.SinhVienRepository;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Service
public class SinhVienService {

    @Autowired
    private SinhVienRepository sinhVienRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

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
