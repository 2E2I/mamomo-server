package com.hsu.mamomo.repository.jpa;

import com.hsu.mamomo.domain.Banner;
import com.hsu.mamomo.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BannerRepository extends JpaRepository<Banner, Long> {

    Optional<List<Banner>> findBannersByUser(User user);

    Optional<Banner> findBannerByBannerId(String bannerId);
}
