package com.ecommerce.ecommerce_backend.controller;

import com.ecommerce.ecommerce_backend.dto.CartResponseDTO;
import com.ecommerce.ecommerce_backend.entity.Cart;
import com.ecommerce.ecommerce_backend.service.CartService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // =========================
    // ADD PRODUCT TO CART
    // =========================

    @PostMapping("/add")
    public ResponseEntity<CartResponseDTO> addProductToCart(
            @RequestParam Long productId,
            @RequestParam Integer quantity) {

        Cart cart = cartService.addProductToCart(
                productId,
                quantity
        );

        CartResponseDTO response =
                cartService.convertToCartResponse(cart);

        return ResponseEntity.ok(response);
    }


    // =========================
    // GET CURRENT USER CART
    // =========================

    @GetMapping
    public ResponseEntity<CartResponseDTO> getCart() {

        Cart cart = cartService.getCurrentUserCart();

        CartResponseDTO response =
                cartService.convertToCartResponse(cart);

        return ResponseEntity.ok(response);
    }


    // =========================
    // UPDATE CART ITEM
    // =========================

    @PutMapping("/update/{productId}")
    public ResponseEntity<CartResponseDTO> updateCartItem(
            @PathVariable Long productId,
            @RequestParam Integer quantity) {

        Cart cart = cartService.updateCartItem(
                productId,
                quantity
        );

        CartResponseDTO response =
                cartService.convertToCartResponse(cart);

        return ResponseEntity.ok(response);
    }


    // =========================
    // REMOVE PRODUCT
    // =========================

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<CartResponseDTO> removeProduct(
            @PathVariable Long productId) {

        Cart cart = cartService.removeProductFromCart(
                productId
        );

        CartResponseDTO response =
                cartService.convertToCartResponse(cart);

        return ResponseEntity.ok(response);
    }


    // =========================
    // CLEAR CART
    // =========================

    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart() {

        cartService.clearCart();

        return ResponseEntity.ok(
                "Cart cleared successfully"
        );
    }
}