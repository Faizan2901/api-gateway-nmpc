package com.couponmanagement.ecommerce.entity;

import java.util.List;

public class BxGyCoupon {

	private List<ProductQuantity> buyProducts;
	private List<ProductQuantity> getProducts;
	private int repetitionLimit;

	public List<ProductQuantity> getBuyProducts() {
		return buyProducts;
	}

	public void setBuyProducts(List<ProductQuantity> buyProducts) {
		this.buyProducts = buyProducts;
	}

	public List<ProductQuantity> getGetProducts() {
		return getProducts;
	}

	public void setGetProducts(List<ProductQuantity> getProducts) {
		this.getProducts = getProducts;
	}

	public int getRepetitionLimit() {
		return repetitionLimit;
	}

	public void setRepetitionLimit(int repetitionLimit) {
		this.repetitionLimit = repetitionLimit;
	}

	@Override
	public String toString() {
		return "BxGyCoupon [buyProducts=" + buyProducts + ", getProducts=" + getProducts + ", repetitionLimit="
				+ repetitionLimit + "]";
	}
	
	

}
