package com.juwon.springcommunity.repository;

import com.juwon.springcommunity.domain.ChatRoom;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ChatRoomRepository {

    void save(ChatRoom chatRoom);

    Optional<ChatRoom> findByProductIdAndBuyerId(@Param("productId") Long productId, @Param("buyerId") Long buyerId);

    Optional<ChatRoom> findById(Long id);

    // 로그인한 유저의 모든 채팅방을 가져오기.
    List<ChatRoom> findAllByUserId(Long userId);
}
