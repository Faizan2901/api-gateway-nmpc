package com.couponmanagement.ecommerce.entity;

import java.util.List;

public class Cart {
	
    private List<CartItem> items;
    private double totalAmount;
	public List<CartItem> getItems() {
		return items;
	}
	public void setItems(List<CartItem> items) {
		this.items = items;
	}
	public double getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

    
    
}


