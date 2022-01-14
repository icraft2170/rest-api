package me.hero.restapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.hero.restapi.common.TestDescription;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;


    @Test
    @TestDescription("정상적인 이벤트 생성하는 테스트")
    void createEvent() throws Exception{
        EventDto event = EventDto.builder()
                .name("spring")
                .description("Rest API Development With Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2022,1,14,2,21))
                .closeEnrollmentDateTime(LocalDateTime.of(2022,1,15, 2,21))
                .beginEventDateTime(LocalDateTime.of(2022,1,20,2,21))
                .endEventDateTime(LocalDateTime.of(2022,1,21,2,21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("뚝섬역 헤이그라운드 서울숲")
                .build();


        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)         // 미디어 타입 JSON
                        .characterEncoding(StandardCharsets.UTF_8)       // 인코딩 방식
                        .accept(MediaTypes.HAL_JSON)                     // 어떤 허가 값을 원하는 가
                        .content(objectMapper.writeValueAsString(event)) // 본문
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE,MediaTypes.HAL_JSON_VALUE+";charset=utf8"))
                .andExpect(jsonPath("id").value(Matchers.not(100)))
                .andExpect(jsonPath("free").value(Matchers.not(true)));
    }


    @Test
    @TestDescription("입력 불가한 값을 사용하여 생성하는 테스트")
    void createEvent_Bad_Request() throws Exception{
        Event event = Event.builder()
                .id(100)
                .name("spring")
                .description("Rest API Development With Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2022,1,14,2,21))
                .closeEnrollmentDateTime(LocalDateTime.of(2022,1,15, 2,21))
                .beginEventDateTime(LocalDateTime.of(2022,1,20,2,21))
                .endEventDateTime(LocalDateTime.of(2022,1,21,2,21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("뚝섬역 헤이그라운드 서울숲")
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.PUBLISHED)
                .build();


        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)         // 미디어 타입 JSON
                        .characterEncoding(StandardCharsets.UTF_8)       // 인코딩 방식
                        .accept(MediaTypes.HAL_JSON)                     // 어떤 허가 값을 원하는 가
                        .content(objectMapper.writeValueAsString(event)) // 본문
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("입력 값이 비어있는 경우에 에러가 발생하는 테스트")
    void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(eventDto))
                )
                .andExpect(status().isBadRequest());
    }


    @Test
    @TestDescription("입력 값이 잘못된 경우에 에러가 발생하는 테스트")
    void createEvent_Bad_Request_Wrong_Input() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("spring")
                .description("Rest API Development With Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2022,1,15,2,21))
                .closeEnrollmentDateTime(LocalDateTime.of(2022,1,14, 2,21))
                .beginEventDateTime(LocalDateTime.of(2022,1,24,2,21))
                .endEventDateTime(LocalDateTime.of(2022,1,21,2,21))
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("뚝섬역 헤이그라운드 서울숲")
                .build();

        this.mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(eventDto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].objectName").exists())
                .andExpect(jsonPath("$[0].defaultMessage").exists())
                .andExpect(jsonPath("$[0].code").exists());
    }
}
