package com.juwon.springcommunity.repository;

import com.juwon.springcommunity.domain.ChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatMessageRepository {

    /**
     * 채팅 메시지를 DB에 저장합니다.
     * @param chatMessage 저장할 채팅 메시지 정보
     */
    void save(ChatMessage chatMessage);

    /**
     * 특정 채팅방의 초기 메시지 목록을 조회합니다.
     * @param roomId 조회할 채팅방의 ID
     * @param limit 가져올 메시지 개수
     * @return 해당 채팅방의 최신 메시지 리스트
     */
    List<ChatMessage> findInitialMessages(@Param("roomId") Long roomId, @Param("limit") int limit);

    /**
     * 특정 메시지 ID 이전의 메시지 목록을 페이지 단위로 조회합니다.
     * @param roomId 조회할 채팅방의 ID
     * @param lastMessageId 이 메시지 ID보다 오래된 메시지를 조회
     * @param limit 가져올 메시지 개수
     * @return 해당 채팅방의 이전 메시지 리스트
     */
    List<ChatMessage> findMessagesWithPaging(@Param("roomId") Long roomId, @Param("lastMessageId") Long lastMessageId, @Param("limit") int limit);

    /**
     * 특정 채팅방의 모든 메시지를 삭제합니다.
     * @param roomId 삭제할 채팅방의 ID
     */
    void deleteByRoomId(Long roomId);
}
