package com.chat.ai.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.chat.ai.model.GPT;


@Controller
public class GPTController {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private GPT gpt = new GPT(GPT.GPT3_5, GPT.SYSTEM_ROLE_SOFTWARE_EXPERT);

	
	@RequestMapping("/gpt/sendMsg")
	public ResponseEntity<Map<String, String>> sendMsg(@RequestBody Map<String, Object> userMsg) {
		
		String receivedMsg = (String)userMsg.get("receivedMsg");
		String responseMsg = "";
		Map <String, String> msgMap = new HashMap<>();
		
		receivedMsg = receivedMsg.trim();
		responseMsg = gpt.chatToGPT(receivedMsg);
		msgMap.put("responseMsg", responseMsg);		
		
		log.debug("receivedMsg: {}", receivedMsg);
		log.debug("responseMsg: {}", responseMsg);
		
		
		return ResponseEntity.ok(msgMap);
	}

}
