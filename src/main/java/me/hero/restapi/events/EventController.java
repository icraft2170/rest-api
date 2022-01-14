package me.hero.restapi.events;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController @RequiredArgsConstructor
@RequestMapping(value = "/api/events", produces = {MediaTypes.HAL_JSON_VALUE+";charset=utf8"})
public class EventController {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;

    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto
                                                        , Errors errors){
        if (errors.hasErrors())
            return ResponseEntity.badRequest().build();

        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors())
            return ResponseEntity.badRequest().build();




        Event newEvent = this.eventRepository.save(modelMapper.map(eventDto,Event.class));
        URI createUri =
                linkTo(EventController.class).slash(newEvent.getId()).toUri();

        return ResponseEntity
                .created(createUri)
                .body(newEvent);
    }
}
