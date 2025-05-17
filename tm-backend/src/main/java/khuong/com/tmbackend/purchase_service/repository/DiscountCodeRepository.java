package khuong.com.tmbackend.purchase_service.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import khuong.com.tmbackend.purchase_service.entity.DiscountCode;

@Repository
public interface DiscountCodeRepository extends JpaRepository<DiscountCode, Long> {
    
    Optional<DiscountCode> findByCode(String code);
    
    @Query("SELECT d FROM DiscountCode d WHERE " +
           "d.code = :code AND " +
           "d.active = true AND " +
           "d.validFrom <= :now AND " +
           "d.validTo >= :now AND " +
           "(d.usageLimit IS NULL OR d.usageCount < d.usageLimit)")
    Optional<DiscountCode> findValidDiscountCode(@Param("code") String code, @Param("now") Instant now);
    
    List<DiscountCode> findByActiveTrue();
    
    List<DiscountCode> findByActiveTrueAndValidToAfter(Instant now);
} 