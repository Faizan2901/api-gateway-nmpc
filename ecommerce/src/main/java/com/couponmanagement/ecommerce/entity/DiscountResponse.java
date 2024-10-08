package com.couponmanagement.ecommerce.entity;

public class DiscountResponse {

	private Coupon coupon;
	private double discount;

	public DiscountResponse(Coupon coupon, double discount) {
		this.coupon = coupon;
		this.discount = discount;
	}

	public Coupon getCoupon() {
		return coupon;
	}

	public void setCoupon(Coupon coupon) {
		this.coupon = coupon;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}
}
