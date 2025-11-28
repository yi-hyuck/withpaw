package com.mysite.test.Chatbot;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mysite.test.member.Member;
import com.mysite.test.member.MemberService;
import com.mysite.test.pet.Pet;
import com.mysite.test.pet.PetService;

@Controller
public class ChatbotController {

    @Autowired
    private OpenAiChatModel chatModel;
    
    @Autowired 
    private MemberService memberService;
    @Autowired
    private PetService petService;

    // 챗봇 인터페이스 페이지로 이동 
//    @GetMapping("/chatbot")
//    public String getChatbotPage() {
//        return "chatbot-page"; 
//    }

    // 챗봇 경로 JSON 요청 처리 및 응답
    @PostMapping("/api/chatbot/ask")
    @ResponseBody
    public String askChatbot(
//    	@RequestParam String question,
    	@RequestBody ChatbotRequest request,
        @AuthenticationPrincipal UserDetails userDetails // 현재 로그인된 사용자 정보
    ) {
    	if (userDetails == null) {
    		return "{\"error\": \"로그인이 필요합니다.\"}";
    	}
    	
//    	String userMessage = question;
        // DTO에서 사용자 메시지 문자열 추출
        String userMessage = request.getUserMessage(); 
        
        // 로그인된 사용자 정보 가져오기 (loginId로 Member 객체 조회)
        String loginId = userDetails.getUsername();
        Member member = memberService.getMember(loginId);
        
        // 반려동물 정보 가져오기 (Member 객체로 Pet 목록 조회)
        List<Pet> pets = petService.getPetsByMember(member); 
        
        // 반려동물 정보를 프롬프트에 활용할 수 있도록 문자열로 변환
        String petInfo = pets.stream()
            .map(pet -> {
                // 생년월일을 이용해 한국식 나이 계산 (만 나이 기준)
                int age = Period.between(pet.getBirthdate(), LocalDate.now()).getYears();
                
                // Pet 엔티티 필드명에 맞게 정보 구성
                return MessageFormat.format(
                    "이름: {0}, 종류: {1}, 나이: 만 {2}세, 성별: {3}, 몸무게: {4}kg, 중성화 여부: {5}", 
                    pet.getPetname(), 
                    pet.getBreed().getBreedname(), 
                    age, 
                    pet.getGender(), 
                    pet.getWeight(), 
                    pet.getNeuter() ? "완료" : "미완료" 
                );
            })
            .reduce("", (a, b) -> a + "\n- " + b);
        
        if (petInfo.isEmpty()) {
            petInfo = "등록된 반려동물 정보가 없습니다.";
        }

        // SystemMessage (반려동물 전문가 설정 및 반려동물 정보 주입)
        var systemMessage = new SystemMessage(
            MessageFormat.format("""
                너는 전문적인 반려동물 증상 상담 챗봇이야. 
                현재 상담을 요청한 사용자({0})가 등록한 반려동물 정보는 다음과 같아:
                {1}
                
                사용자가 자신의 반려동물에 대한 증상을 묻는다면, 정보를 참고하여 친절하고 전문적으로 답변해 줘.
                답변 시, 이모지(Emoji)를 사용하여 친근함을 더해.
                
                하지만 **절대 진단이나 처방을 내리지 말고**, 반드시 다음과 같은 면책 문구를 답변 마지막에 추가해:
                
                ---
                ⚠️ 이 답변은 참고용 정보이며, 정확한 진단과 치료는 반드시 동물병원 수의사에게 받아보셔야 합니다.
                """, member.getLoginId(), petInfo) 
        );

        // UserMessage (사용자 질문)
        var message = new UserMessage(userMessage);

        // OpenAI 호출 및 결과 반환
        return chatModel.call(message, systemMessage);
    }
}