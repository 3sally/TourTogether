package com.ssafy.tourtogether.memo.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.tourtogether.db.entity.MemoMessage;
import com.ssafy.tourtogether.db.entity.ScheduleMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisSubscriber implements MessageListener {

	private final ObjectMapper objectMapper;
	private final RedisTemplate redisTemplate;
	private final SimpMessageSendingOperations messagingTemplate;

	/**
	 * Redis에서 메시지가 발행(publish)되면 대기하고 있던 onMessage가 해당 메시지를 받아 처리한다.
	 */
	@Override
	public void onMessage(Message message, byte[] pattern) {
		try {
			// redis에서 발행된 데이터를 받아 deserialize
			String publishMessage = (String) redisTemplate.getStringSerializer().deserialize(message.getBody());
			// MemoMessage 객채로 맵핑
			MemoMessage roomMessage = objectMapper.readValue(publishMessage, MemoMessage.class);
			if (roomMessage.getDelta() == null) {
				ScheduleMessage schduleMessage = objectMapper.readValue(publishMessage, ScheduleMessage.class);
				System.out.println("SchedulSubscriber onMessage: " + schduleMessage.toString());
				messagingTemplate.convertAndSend("/api/sub/schedule/" + schduleMessage.getRoomId(), schduleMessage);
			} else {
				// Websocket 구독자에게 채팅 메시지 Send
				System.out.println("MemoSubscriber onMessage: " + roomMessage.toString());
				messagingTemplate.convertAndSend("/api/sub/memo/" + roomMessage.getRoomId(), roomMessage);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
}
