package com.doubleowner.revibe.domain.item.controller;

import com.doubleowner.revibe.domain.item.dto.request.ItemRequestDto;
import com.doubleowner.revibe.domain.item.dto.request.ItemUpdateRequestDto;
import com.doubleowner.revibe.domain.item.dto.response.ItemResponseDto;
import com.doubleowner.revibe.domain.item.service.ItemService;
import com.doubleowner.revibe.domain.user.entity.User;
import com.doubleowner.revibe.global.common.dto.CommonResponseBody;
import com.doubleowner.revibe.global.config.auth.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    // 상품 등록
    @PostMapping
    public ResponseEntity<CommonResponseBody<ItemResponseDto>> createItem(
            @Valid @RequestBody ItemRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User loginUser = userDetails.getUser();
        ItemResponseDto responseDto = itemService.createItem(loginUser, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CommonResponseBody<>("아이템을 등록했습니다", responseDto));
    }

    // 상품 수정
    @PatchMapping("/{itemId}")
    public ResponseEntity<CommonResponseBody<ItemResponseDto>> updateItem(
            @Valid @RequestBody ItemUpdateRequestDto requestDto,
            @PathVariable Long itemId
    )
    {
        ItemResponseDto responseDto = itemService.modifyItem(itemId,requestDto);

        return ResponseEntity.status(HttpStatus.OK).body((new CommonResponseBody<>("아이템이 수정 되었습니다.",responseDto)));
    }
}