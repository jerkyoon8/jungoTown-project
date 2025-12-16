package com.juwon.springcommunity.service;

import com.juwon.springcommunity.domain.ChatRoom;
import com.juwon.springcommunity.domain.Product;
import com.juwon.springcommunity.domain.User;
import com.juwon.springcommunity.repository.ChatRoomRepository;
import com.juwon.springcommunity.repository.ProductRepository;
import com.juwon.springcommunity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    
     // 상품 ID와 구매자 ID를 기반으로 채팅방을 생성하거나 기존 채팅방을 조회합니다
    @Transactional
    public Long getOrCreateChatRoom(Long productId, Long buyerId) {
        // 동일한 상품에 대해 동일한 구매자가 문의한 채팅방이 있는지 먼저 확인합니다.
        Optional<ChatRoom> existingChatRoom = chatRoomRepository.findByProductIdAndBuyerId(productId, buyerId);
        if (existingChatRoom.isPresent()) {
            // 기존 채팅방이 존재하면, 그 채팅방의 ID를 반환합니다.
            return existingChatRoom.get().getId();
        }

        // 새로운 채팅방을 생성해야 하는 경우, 필요한 정보를 조회합니다.
        // 1. 상품 정보 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다. ID: " + productId));
        // 2. 구매자 정보 조회
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new IllegalArgumentException("구매자 정보를 찾을 수 없습니다. ID: " + buyerId));
        // 3. 판매자 정보 조회
        User seller = userRepository.findById(product.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("판매자 정보를 찾을 수 없습니다. ID: " + product.getUserId()));

        // 조회한 정보를 바탕으로 새로운 ChatRoom 객체를 생성합니다.
        ChatRoom chatRoom = new ChatRoom(product, buyer, seller);
        
        // 생성한 채팅방을 데이터베이스에 저장합니다.
        chatRoomRepository.save(chatRoom);
        
        // 저장 후 생성된 채팅방의 ID를 반환합니다.
        return chatRoom.getId();
    }


    // 채팅방 ID로 채팅방 정보 조회
    public ChatRoom findRoomById(Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다. ID: " + chatRoomId));
    }

    // roomId 와 sender 정보로 메세지 대상을 조회
    public String findReceiver(Long roomId, String sender){

        ChatRoom chatRoom = findRoomById(roomId);
        String receiver;

        if(sender.equals(chatRoom.getBuyer().getUsername())){
            receiver = chatRoom.getSeller().getUsername();
        }
        else if(sender.equals(chatRoom.getSeller().getUsername())){
            receiver = chatRoom.getBuyer().getUsername();
        }
        else {
            throw new IllegalStateException("보낸 사람이 이 채팅방의 참여자가 아닙니다.");
        }

        return receiver;
    }

    
    // 사용자 ID에 대한 채팅방 반환
    public List<ChatRoom> findAllRoomsByUserId(Long userId) {
        return chatRoomRepository.findAllByUserId(userId);
    }
}
