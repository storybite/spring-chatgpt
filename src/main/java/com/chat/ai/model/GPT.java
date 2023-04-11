package com.chat.ai.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GPT {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	private static final String OPEN_API_KEY =  System.getenv("OPEN_API_KEY"); //open-ai에서 발급받은 키값
	private static final String API_END_POINT = "https://api.openai.com/v1/chat/completions";
	public static final String GPT3_5 = "gpt-3.5-turbo";
	public static final String GPT4 = "gpt-4";
	public static final String SYSTEM_ROLE_SOFTWARE_EXPERT = "당신은 최고의 소프트웨어전문가입니다. 질문에 대해 알기 쉽게 답해주세요.";
	private String selectedModel; 
	private URL url;
	private JSONArray msgHistory;  //
	  
	public GPT(String selectedModel, String systemRole) {   
		log.debug("OPEN_API_KEY:{}", OPEN_API_KEY);
		this.selectedModel = selectedModel;
		this.msgHistory = new JSONArray();
		try {
			this.url = new URL(API_END_POINT);			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		this.addMsg("system", systemRole);	
		
	}
	
	public String getSelectedModel() {
		return this.selectedModel;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject addMsg(String role, String msg) {
		JSONObject msgJsonObject = new JSONObject();
		msgJsonObject.put("role", role);
		msgJsonObject.put("content", msg);
        this.msgHistory.add(msgJsonObject);
        return msgJsonObject;
	}
	
	public HttpURLConnection getConnection() {
		HttpURLConnection httpURLConnection = null;
		try {
			httpURLConnection = (HttpURLConnection) url.openConnection();		
	        httpURLConnection.setRequestMethod("POST");
	        httpURLConnection.setRequestProperty("Content-Type", "application/json; utf-8");
	        httpURLConnection.setRequestProperty("Authorization", "Bearer " + OPEN_API_KEY);
	        httpURLConnection.setDoOutput(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
        return httpURLConnection;
	}
	
	@SuppressWarnings("unchecked")
	private String buildApiParam() {
		
        JSONObject apiParam = new JSONObject();
        apiParam.put("messages", msgHistory);
        apiParam.put("model", this.selectedModel);
        
        return apiParam.toJSONString();
		
	}
	
	public String sendMsgToAssistant(String aipParam) throws Exception {
		HttpURLConnection httpURLConnection = this.getConnection();	
		try (OutputStream os = httpURLConnection.getOutputStream()) {
			byte[] inputBytes = aipParam.getBytes("utf-8");
			os.write(inputBytes, 0, inputBytes.length);
		} 
		
		int resCode = httpURLConnection.getResponseCode();
		log.debug("resCode:" + resCode);
	
		try (BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "utf-8"))) {
			StringBuilder res = new StringBuilder();
			String resLine = null;
	
			while ((resLine = br.readLine()) != null) {
	          res.append(resLine.trim());
			}
	
			return (res.toString());
		}
	}
	
	@SuppressWarnings({ "rawtypes"})
	public static String extractAssitantMsg(String respJsonStr) {		
		JSONParser parser = new JSONParser();
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) parser.parse(respJsonStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
		List choices = (List)jsonObject.get("choices");
		Map firstChoice = (Map)choices.get(0); 
		Map msgMap = (Map)firstChoice.get("message");
		String content = (String)msgMap.get("content");
		return content;
	}
	
	public String chatToGPT(String userMsg) {
		this.addMsg("user", userMsg);
		String aipParam = this.buildApiParam();
		log.debug("aipParam : \n " + aipParam);
		try {
			String assistantRespJson = this.sendMsgToAssistant(aipParam);
			String assistantMsg = extractAssitantMsg(assistantRespJson);
			this.addMsg("assistant", assistantMsg);
			return assistantMsg;
		} catch (Exception e) {
			return "CHAT-ERROR";
		}
	}
}
