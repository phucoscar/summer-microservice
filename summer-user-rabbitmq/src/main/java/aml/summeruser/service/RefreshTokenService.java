package aml.summeruser.service;

import aml.summeruser.entity.RefreshToken;
import aml.summeruser.entity.SinhVien;
import aml.summeruser.repository.RefreshTokenRepository;
import aml.summeruser.repository.SinhVienRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Value("2592000") // 30 days
    private Long refreshTokenDuration;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private SinhVienRepository sinhVienRepository;

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken generateRefreshToken(int userId) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setSinhVien(sinhVienRepository.findById(userId).get());
        refreshToken.setExpDate(Instant.now().plusSeconds(refreshTokenDuration));
        refreshToken.setToken(UUID.randomUUID().toString());

        refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            return null;
        }
        return token;
    }

    @Transactional
    public void deleteRefreshToken(SinhVien sinhVien) {
        refreshTokenRepository.deleteBySinhVien(sinhVien);
    }
}
