package aml.summeruser.service;

import aml.summeruser.entity.Role;
import aml.summeruser.entity.SinhVien;
import aml.summeruser.repository.SinhVienRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserDetailsService {

    @Autowired
    private SinhVienRepository sinhVienRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<SinhVien> op = sinhVienRepo.findByUsername(username);
        if (!op.isPresent()) {
            return null;
        } else {
            SinhVien sinhVien = op.get();
            List<Role> roles = sinhVien.getRoles();
            Set<GrantedAuthority> ga = new HashSet<>();
            for (Role role: roles) {
                ga.add(new SimpleGrantedAuthority(role.getName()));
            }
            User user = new User(username, sinhVien.getPassword(), ga);
            return user;
        }

    }
}
