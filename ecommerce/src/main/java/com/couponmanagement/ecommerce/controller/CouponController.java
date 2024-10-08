package com.couponmanagement.ecommerce.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.couponmanagement.ecommerce.dao.CouponDAO;
import com.couponmanagement.ecommerce.entity.Cart;
import com.couponmanagement.ecommerce.entity.CartItem;
import com.couponmanagement.ecommerce.entity.Coupon;
import com.couponmanagement.ecommerce.entity.DiscountResponse;

@RestController
public class CouponController {

	@Autowired
	private CouponDAO couponDAO;

	// POST /coupons: Create a new coupon
	@PostMapping("/coupon")
	public ResponseEntity<Coupon> createCoupon(@RequestBody Coupon coupon) {

		Coupon savedCoupon = couponDAO.save(coupon);

		return ResponseEntity.ok(savedCoupon);

	}

	// GET /coupons: Retrieve all coupons
	@GetMapping("/coupons")
	public ResponseEntity<List<Coupon>> getAllCoupon() {

		List<Coupon> allCoupon = couponDAO.findAll();

		return ResponseEntity.ok(allCoupon);

	}

	// GET /coupons/{id}: Retrieve a specific coupon by its ID
	@GetMapping("/coupons/{id}")
	public ResponseEntity<Coupon> getCouponById(@PathVariable("id") Long id) {

		Optional<Coupon> coupon = couponDAO.findById(id);

		if (!coupon.isPresent()) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(coupon.get());

	}

	// PUT /coupons/{id}: Update a specific coupon by its ID
	@PutMapping("/coupons/{id}")
	public ResponseEntity<Coupon> updateCouponById(@PathVariable("id") Long id, @RequestBody Coupon updateCoupon) {

		Optional<Coupon> dbcoupon = couponDAO.findById(id);

		if (!dbcoupon.isPresent()) {
			return ResponseEntity.notFound().build();
		}

		Coupon coupon = dbcoupon.get();

		coupon.setDetails(updateCoupon.getDetails());
		coupon.setType(updateCoupon.getType());

		couponDAO.save(coupon);

		return ResponseEntity.ok(coupon);

	}

	// DELETE /coupons/{id}: Delete a specific coupon by its ID
	@DeleteMapping("/coupons/{id}")
	public ResponseEntity<String> deleteCouponById(@PathVariable("id") Long id) {

		Optional<Coupon> dbcoupon = couponDAO.findById(id);

		if (!dbcoupon.isPresent()) {
			return ResponseEntity.notFound().build();
		}

		Coupon coupon = dbcoupon.get();
		couponDAO.deleteById(id);

		return ResponseEntity.ok("Succesfully Deleted Coupon Id :- " + id);

	}

	// POST /applicable-coupons: Fetch all applicable coupons for a given cart and
	// calculate the total discount that will be applied by each coupon.
	@PostMapping("/applicable-coupons")
	public ResponseEntity<List<DiscountResponse>> getApplicableCoupons(@RequestBody Cart cart) {

		List<Coupon> allCoupons = couponDAO.findAll();
		List<DiscountResponse> applicableDiscounts = new ArrayList<>();

		for (Coupon coupon : allCoupons) {
			int discount = calculateDiscountBasedOnCartType(coupon, cart);
			applicableDiscounts.add(new DiscountResponse(coupon, discount));
		}

		return ResponseEntity.ok(applicableDiscounts);

	}

	private int calculateDiscountBasedOnCartType(Coupon coupon, Cart cart) {

		int discount = 0;

		if (coupon.getType().equals("cart-wise")) {
			return calculateCartWiseDiscount(coupon, cart);
		} else if (coupon.getType().equals("cart-wise")) {
			return calculateProductWiseDiscount(coupon, cart);
		} else if (coupon.getType().equals("bxgy")) {
			return calculateBxGyDiscount(coupon, cart);
		}

		return 0;
	}

	private int calculateCartWiseDiscount(Coupon coupon, Cart cart) {

		Map<String, Object> details = (Map<String, Object>) coupon.getDetails();
		int threshold = (int) details.get("threshold");
		int discountAmount = (int) details.get("discount");

		if (cart.getTotalAmount() >= threshold) {
			return discountAmount;
		}
		return 0;
	}

	private int calculateProductWiseDiscount(Coupon coupon, Cart cart) {

		Map<String, Object> details = (Map<String, Object>) coupon.getDetails();
		Long productId = Long.valueOf((Integer) details.get("product_id"));
		int discountAmount = (int) details.get("discount");

		for (CartItem item : cart.getItems()) {
			if (item.getProductId().equals(productId)) {
				return discountAmount * item.getQuantity();
			}
		}
		return 0;
	}

	private int calculateBxGyDiscount(Coupon coupon, Cart cart) {

		Map<String, Object> details = (Map<String, Object>) coupon.getDetails();
		List<Map<String, Object>> buyProducts = (List<Map<String, Object>>) details.get("buy_products");
		List<Map<String, Object>> getProducts = (List<Map<String, Object>>) details.get("get_products");
		int repetitionLimit = (int) details.get("repetition_limit");

		int repetitionCount = Integer.MAX_VALUE;

		for (Map<String, Object> buyProduct : buyProducts) {
			Long buyProductId = Long.valueOf((Integer) buyProduct.get("product_id"));
			int requiredQuantity = (int) buyProduct.get("quantity");

			int quantityInCart = 0;
			for (CartItem item : cart.getItems()) {
				if (item.getProductId().equals(buyProductId)) {
					quantityInCart = item.getQuantity();
					break;
				}
			}

			int applicableRepetitions = quantityInCart / requiredQuantity;
			repetitionCount = Math.min(repetitionCount, applicableRepetitions);
		}

		repetitionCount = Math.min(repetitionCount, repetitionLimit);

		int discount = 0;
		if (repetitionCount > 0) {
			for (Map<String, Object> getProduct : getProducts) {
				Long getProductId = Long.valueOf((Integer) getProduct.get("product_id"));
				int getQuantity = (int) getProduct.get("quantity");

				for (CartItem item : cart.getItems()) {
					if (item.getProductId().equals(getProductId)) {
						discount += item.getPrice() * getQuantity * repetitionCount;
					}
				}
			}
		}

		return discount;
	}

	// POST /apply-coupon/{id}: Apply a specific coupon to the cart and return the
	// updated cart with discounted prices for each item.
	@PostMapping("/applicable-coupons/{id}")
	public ResponseEntity<Map<String, Object>> applyCouponToCart(@PathVariable("id") Long couponId,
			@RequestBody Cart cart) {

		Optional<Coupon> couponOptional = couponDAO.findById(couponId);
		if (!couponOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}

		Coupon coupon = couponOptional.get();
		applyCoupon(coupon, cart);

		Map<String, Object> response = new HashMap<>();
		response.put("updated_cart", buildUpdatedCartResponse(cart));

		return ResponseEntity.ok(response);
	}

	private void applyCoupon(Coupon coupon, Cart cart) {
		switch (coupon.getType()) {
		case "cart-wise":
			applyCartWiseCoupon(coupon, cart);
			break;
		case "product-wise":
			applyProductWiseCoupon(coupon, cart);
			break;
		case "bxgy":
			applyBxGyCoupon(coupon, cart);
			break;
		}
	}

	private void applyCartWiseCoupon(Coupon coupon, Cart cart) {

		Map<String, Object> details = (Map<String, Object>) coupon.getDetails();
		double threshold = (double) details.get("threshold");
		double discountAmount = (double) details.get("discount");

		if (cart.getTotalAmount() >= threshold) {
			double totalDiscount = discountAmount;

			for (CartItem item : cart.getItems()) {
				double itemProportion = item.getPrice() / cart.getTotalAmount();
				double itemDiscount = totalDiscount * itemProportion;
				item.setDiscountedPrice(item.getPrice() - itemDiscount);
			}

			cart.setTotalAmount(cart.getTotalAmount() - totalDiscount);
		}
	}

	private void applyProductWiseCoupon(Coupon coupon, Cart cart) {

		Map<String, Object> details = (Map<String, Object>) coupon.getDetails();
		Long productId = Long.valueOf((Integer) details.get("product_id"));
		double discountAmount = (double) details.get("discount");

		for (CartItem item : cart.getItems()) {
			if (item.getProductId().equals(productId)) {
				double totalDiscount = discountAmount * item.getQuantity();
				item.setDiscountedPrice(item.getPrice() - totalDiscount);
			}
		}

		double totalDiscount = cart.getItems().stream().filter(item -> item.getProductId().equals(productId))
				.mapToDouble(item -> item.getQuantity() * discountAmount).sum();

		cart.setTotalAmount(cart.getTotalAmount() - totalDiscount);
	}

	private Map<String, Object> buildUpdatedCartResponse(Cart cart) {
		Map<String, Object> updatedCart = new HashMap<>();

		double totalDiscount = calculateTotalDiscount(cart); // Calculate total discount on the cart
		double totalPrice = calculateTotalPrice(cart); // Calculate the original total price
		double finalPrice = totalPrice - totalDiscount; // Calculate final price after applying discount

		updatedCart.put("items", cart.getItems());
		updatedCart.put("total_price", totalPrice);
		updatedCart.put("total_discount", totalDiscount);
		updatedCart.put("final_price", finalPrice);

		return updatedCart;
	}

	private double calculateTotalDiscount(Cart cart) {
		return cart.getItems().stream().mapToDouble(CartItem::getTotalDiscount).sum();
	}

	private double calculateTotalPrice(Cart cart) {
		return cart.getItems().stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
	}

	private void applyBxGyCoupon(Coupon coupon, Cart cart) {

		Map<String, Object> details = (Map<String, Object>) coupon.getDetails();
		List<Map<String, Object>> buyProducts = (List<Map<String, Object>>) details.get("buy_products");
		List<Map<String, Object>> getProducts = (List<Map<String, Object>>) details.get("get_products");
		int repetitionLimit = (int) details.get("repetition_limit");

		int repetitionCount = Integer.MAX_VALUE;

		for (Map<String, Object> buyProduct : buyProducts) {
			Long buyProductId = Long.valueOf((Integer) buyProduct.get("product_id"));
			int requiredQuantity = (int) buyProduct.get("quantity");

			int quantityInCart = 0;
			for (CartItem item : cart.getItems()) {
				if (item.getProductId().equals(buyProductId)) {
					quantityInCart = item.getQuantity();
					break;
				}
			}

			int applicableRepetitions = quantityInCart / requiredQuantity;
			repetitionCount = Math.min(repetitionCount, applicableRepetitions);
		}

		repetitionCount = Math.min(repetitionCount, repetitionLimit);

		if (repetitionCount > 0) {
			for (Map<String, Object> getProduct : getProducts) {
				Long getProductId = Long.valueOf((Integer) getProduct.get("product_id"));
				int getQuantity = (int) getProduct.get("quantity") * repetitionCount;

				for (CartItem item : cart.getItems()) {
					if (item.getProductId().equals(getProductId)) {
						item.setDiscountedPrice(0);
						item.setTotalDiscount(item.getPrice() * getQuantity);
					}
				}
			}
		}
	}

}
