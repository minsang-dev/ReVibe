package com.doubleowner.revibe.domain.cart.service;

import com.doubleowner.revibe.domain.cart.dto.request.CartRequestDto;
import com.doubleowner.revibe.domain.cart.dto.response.CartResponseDto;
import com.doubleowner.revibe.domain.cart.entity.Cart;
import com.doubleowner.revibe.domain.cart.repository.CartRepository;
import com.doubleowner.revibe.domain.option.entity.Option;
import com.doubleowner.revibe.domain.option.repository.OptionRepository;
import com.doubleowner.revibe.domain.user.entity.User;
import com.doubleowner.revibe.global.exception.CustomException;
import com.doubleowner.revibe.global.exception.errorCode.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.doubleowner.revibe.global.exception.errorCode.ErrorCode.ALREADY_EXIST;
import static com.doubleowner.revibe.global.exception.errorCode.ErrorCode.NOT_FOUND_VALUE;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    private final OptionRepository optionRepository;

    // 장바구니 담기
    public CartResponseDto addCart(User loginUser,CartRequestDto requestDto) {

        Option option = optionRepository.findByIdOrElseThrow(requestDto.getOptionId());

        // 이미 장바구니에 해당상품이 존재 할 경우 예외처리
        if(cartRepository.existsByUserIdAndOptionId(loginUser.getId(),requestDto.getOptionId())){
            throw new CustomException(ALREADY_EXIST);
        }

        Cart cart = new Cart(loginUser, option);

        cartRepository.save(cart);

        return CartResponseDto.toDto(cart);
    }

    // 내 장바구니 조회
    public List<CartResponseDto> getMyCarts(User loginUser) {

        List<Cart> carts = cartRepository.findAllByUser(loginUser);

        return carts.stream().map(CartResponseDto::toDto).toList();
    }

    // 장바구니 상품 삭제
    @Transactional
    public void deleteCart(User loginUser, Long cartId) {
        Cart cart = cartRepository.findByIdAndUser(cartId, loginUser);
        if(cart == null){
            throw new CustomException(NOT_FOUND_VALUE);
        }
        cartRepository.delete(cart);
    }

}
