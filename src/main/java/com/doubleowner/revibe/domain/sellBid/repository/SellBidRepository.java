package com.doubleowner.revibe.domain.sellBid.repository;

import com.doubleowner.revibe.domain.buyBid.entity.BuyBid;
import com.doubleowner.revibe.domain.sellBid.entity.SellBid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SellBidRepository extends JpaRepository<SellBid, Long> {
    @Query("select sb from SellBid sb where sb.user.id=:userId")
    Page<SellBid> findByUserId(@Param("userId") Long userId, Pageable pageable);
}
