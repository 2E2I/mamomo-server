package com.hsu.mamomo.repository.jpa;

import com.hsu.mamomo.domain.Banner;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BannerRepository extends JpaRepository<Banner, Long> {

    Optional<Banner> findBannerByImgUrl(String imgUrl);
}
