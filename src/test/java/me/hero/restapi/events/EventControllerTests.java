package me.hero.restapi.events;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest
public class EventControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    EventRepository eventRepository;

    @Test
    void createEvent() throws Exception{
        Event event = Event.builder()
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

        event.setId(10);
        Mockito.when(eventRepository.save(event))
                .thenReturn(event);


        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)         // 미디어 타입 JSON
                        .characterEncoding(StandardCharsets.UTF_8)       // 인코딩 방식
                        .accept(MediaTypes.HAL_JSON)                     // 어떤 허가 값을 원하는 가
                        .content(objectMapper.writeValueAsString(event)) // 본문
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists());
    }
}
