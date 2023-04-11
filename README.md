# spring-chatgpt
* 스프링으로 챗GPT와 연동하는 방법에 대한 샘플 코드

## 개발 환경
* Java 8 이상
* sts4 

## 기능
* open-ai chat-gpt 연동  
  연동 방법 https://platform.openai.com/docs/api-reference/authentication 참조
* gpt에게 메시지 전송 시 기존 대화에 메시지 concat 후 전송


## 사용 예시
* 입력 json:  
{  
    "receivedMsg" : "머신러닝에 대해 20글자 이내로 답해줘."  
}  

* 출력 Json:  
{  
    "responseMsg": "데이터를 통해 모델을 생성하는 인공지능 분야."  
}  


