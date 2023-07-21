package aml.summeruser.repository;

import aml.summeruser.entity.SinhVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SinhVienRepository extends JpaRepository<SinhVien, Integer> {
    Optional<SinhVien> findByUsername(String username);
}
