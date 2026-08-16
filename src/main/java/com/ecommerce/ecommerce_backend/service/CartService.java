package com.ecommerce.ecommerce_backend.service;
import com.ecommerce.ecommerce_backend.dto.CartItemResponseDTO;
import com.ecommerce.ecommerce_backend.dto.CartResponseDTO;
import java.util.stream.Collectors;
import com.ecommerce.ecommerce_backend.entity.Cart;
import com.ecommerce.ecommerce_backend.entity.CartItem;
import com.ecommerce.ecommerce_backend.entity.Product;
import com.ecommerce.ecommerce_backend.entity.User;
import com.ecommerce.ecommerce_backend.repository.CartItemRepository;
import com.ecommerce.ecommerce_backend.repository.CartRepository;
import com.ecommerce.ecommerce_backend.repository.ProductRepository;
import com.ecommerce.ecommerce_backend.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository,
            UserRepository userRepository) {

        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }
    //we know which User is logged in

    private User getCurrentUser() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }
    public CartResponseDTO convertToCartResponse(Cart cart) {

        List<CartItemResponseDTO> items =
                cart.getItems()
                        .stream()
                        .map(item ->
                                new CartItemResponseDTO(
                                        item.getProduct().getId(),
                                        item.getProduct().getName(),
                                        item.getProduct().getPrice(),
                                        item.getQuantity()
                                )
                        )
                        .collect(Collectors.toList());

        return new CartResponseDTO(
                cart.getId(),
                cart.getUser().getId(),
                items
        );
    }

        // =========================
        // ADD PRODUCT TO CART
        // =========================

        public Cart addProductToCart(

                Long productId,
                Integer quantity) {

            // 1. Find user
            User user = getCurrentUser();

            // 2. Find product
            Product product = productRepository.findById(productId)
                    .orElseThrow(() ->
                            new RuntimeException("Product not found")
                    );

            // 3. Check quantity
            if (quantity == null || quantity <= 0) {
                throw new RuntimeException(
                        "Quantity must be greater than 0"
                );
            }

            // 4. Check stock
            if (product.getQuantity() < quantity) {
                throw new RuntimeException(
                        "Not enough product stock"
                );
            }

            // 5. Find existing cart or create new cart
            Cart cart = cartRepository.findByUserId(user.getId())
                    .orElseGet(() -> {

                        Cart newCart = Cart.builder()
                                .user(user)
                                .build();

                        return cartRepository.save(newCart);
                    });

            // 6. Check whether product already exists
            CartItem cartItem =
                    cartItemRepository
                            .findByCartIdAndProductId(
                                    cart.getId(),
                                    productId
                            )
                            .orElse(null);

            // 7. If product already exists
            if (cartItem != null) {

                int newQuantity =
                        cartItem.getQuantity() + quantity;

                if (product.getQuantity() < newQuantity) {
                    throw new RuntimeException(
                            "Not enough product stock"
                    );
                }

                cartItem.setQuantity(newQuantity);

            } else {

                // 8. Create new CartItem
                cartItem = CartItem.builder()
                        .cart(cart)
                        .product(product)
                        .quantity(quantity)
                        .build();
            }
            cart.getItems().add(cartItem);

            // 9. Save CartItem
            cartItemRepository.save(cartItem);

            // 10. Return updated cart
            return cartRepository.findById(cart.getId())
                    .orElseThrow(() ->
                            new RuntimeException("Cart not found")
                    );
        }


        // =========================
        // GET USER CART
        // =========================

        public Cart getCurrentUserCart() {
User user=getCurrentUser();
            return cartRepository.findByUserId(user.getId())
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Cart not found"
                            )
                    );
        }


        // =========================
        // UPDATE CART ITEM
        // =========================

        public Cart updateCartItem(

                Long productId,
                Integer quantity) {
User user=getCurrentUser();
            if (quantity == null || quantity <= 0) {
                throw new RuntimeException(
                        "Quantity must be greater than 0"
                );
            }

            // Find user's cart
            Cart cart = cartRepository.findByUserId(user.getId())
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Cart not found"
                            )
                    );

            // Find cart item
            CartItem cartItem =
                    cartItemRepository
                            .findByCartIdAndProductId(
                                    cart.getId(),
                                    productId
                            )
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Product not found in cart"
                                    )
                            );

            // Check stock
            Product product = cartItem.getProduct();

            if (product.getQuantity() < quantity) {
                throw new RuntimeException(
                        "Not enough product stock"
                );
            }

            // Update quantity
            cartItem.setQuantity(quantity);

            cartItemRepository.save(cartItem);

            return cart;
        }


        // =========================
        // REMOVE PRODUCT
        // =========================

        public Cart removeProductFromCart(

                Long productId) {
User user=getCurrentUser();
            Cart cart = cartRepository.findByUserId(user.getId())
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Cart not found"
                            )
                    );

            CartItem cartItem =
                    cartItemRepository
                            .findByCartIdAndProductId(
                                    cart.getId(),
                                    productId
                            )
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Product not found in cart"
                                    )
                            );

            cartItemRepository.delete(cartItem);

            return cart;
        }


        // =========================
        // CLEAR CART
        // =========================

        public void clearCart() {
User user=getCurrentUser();
            Cart cart = cartRepository.findByUserId(user.getId())
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Cart not found"
                            )
                    );

            List<CartItem> items = cart.getItems();

            cartItemRepository.deleteAll(items);
        }
    }

