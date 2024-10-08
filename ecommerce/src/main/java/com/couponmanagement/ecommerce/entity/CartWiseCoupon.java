package com.couponmanagement.ecommerce.entity;

public class CartWiseCoupon {
	private double threshold;
	private double discount;

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	@Override
	public String toString() {
		return "CartWiseCoupon [threshold=" + threshold + ", discount=" + discount + "]";
	}

}
