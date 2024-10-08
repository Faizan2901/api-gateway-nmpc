package com.couponmanagement.ecommerce.entity;

public class ProductWiseCoupon {
    private Long productId;
    private double discount;
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	public double getDiscount() {
		return discount;
	}
	public void setDiscount(double discount) {
		this.discount = discount;
	}
	@Override
	public String toString() {
		return "ProductWiseCoupon [productId=" + productId + ", discount=" + discount + "]";
	}
    
   
}

