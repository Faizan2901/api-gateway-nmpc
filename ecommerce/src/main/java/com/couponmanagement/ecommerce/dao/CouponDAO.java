package com.couponmanagement.ecommerce.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.couponmanagement.ecommerce.entity.Coupon;

@Repository
public interface CouponDAO extends JpaRepository<Coupon, Long> {

}
