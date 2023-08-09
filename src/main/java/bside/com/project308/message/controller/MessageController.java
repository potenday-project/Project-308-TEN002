package bside.com.project308.message.controller;

import bside.com.project308.common.constant.ResponseCode;
import bside.com.project308.common.response.Response;
import bside.com.project308.message.dto.MessageRoomDto;
import bside.com.project308.message.dto.response.MessageRoomResponse;
import bside.com.project308.message.service.MessageRoomService;
import bside.com.project308.security.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageRoomService messageRoomService;

    @GetMapping
    public ResponseEntity<Response> getAllMessageRoom(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<MessageRoomDto> allMessageRoomList = messageRoomService.getAllMessageRoomList(userPrincipal.id());
        List<MessageRoomResponse> messageRoomResponses = allMessageRoomList.stream().map(MessageRoomResponse::from).toList();
        return ResponseEntity.status(HttpStatus.OK).body(Response.success(ResponseCode.SUCCESS.getCode(), messageRoomResponses));
    }
}
