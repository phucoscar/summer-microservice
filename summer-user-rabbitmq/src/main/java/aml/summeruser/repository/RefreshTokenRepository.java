package aml.summeruser.repository;

import aml.summeruser.entity.RefreshToken;
import aml.summeruser.entity.SinhVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteBySinhVien(SinhVien sinhVien);
}
