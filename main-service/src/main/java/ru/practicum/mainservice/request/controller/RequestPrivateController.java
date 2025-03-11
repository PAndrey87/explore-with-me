package ru.practicum.mainservice.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.request.dto.ParticipationRequestDto;
import ru.practicum.mainservice.request.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{user-id}/requests")
@RequiredArgsConstructor
public class RequestPrivateController {
    public static final String USER_ID = "user-id";
    public static final String REQUEST_ID = "request-id";
    private final RequestService requestService;

    @GetMapping
    public List<ParticipationRequestDto> getRequests(@PathVariable(USER_ID) Long userId) {
        return requestService.getRequests(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto create(@PathVariable(USER_ID) Long userId, @RequestParam Long eventId) {
        return requestService.create(userId, eventId);
    }

    @PatchMapping("/{" + REQUEST_ID + "}/cancel")
    public ParticipationRequestDto cancel(@PathVariable(USER_ID) Long userId, @PathVariable(REQUEST_ID) Long requestId) {
        return requestService.cancel(userId, requestId);
    }
}
