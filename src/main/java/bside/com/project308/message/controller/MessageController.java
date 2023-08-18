package bside.com.project308.message.controller;

import bside.com.project308.common.constant.ResponseCode;
import bside.com.project308.common.response.Response;
import bside.com.project308.common.response.ResponseWithUser;
import bside.com.project308.match.service.MatchService;
import bside.com.project308.message.controller.usecase.GetAllMessageRoom;
import bside.com.project308.message.controller.usecase.ReadAllMessageAndGetMessageRoom;
import bside.com.project308.message.controller.usecase.WriteMessageAndUpdateLastMessage;
import bside.com.project308.message.dto.MessageDto;
import bside.com.project308.message.dto.MessageRoomDto;
import bside.com.project308.message.dto.MessageRoomWithNewMessageCheck;
import bside.com.project308.message.dto.request.MessageRequest;
import bside.com.project308.message.dto.response.*;
import bside.com.project308.message.service.MessageRoomService;
import bside.com.project308.security.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageRoomService messageRoomService;
    private final GetAllMessageRoom getAllMessageRoom;
    private final WriteMessageAndUpdateLastMessage writeMessageAndUpdateLastMessage;
    private final ReadAllMessageAndGetMessageRoom readAllMessageAndGetMessageRoom;

    @GetMapping
    public ResponseEntity<Response> getAllMessageRoom(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<MessageRoomWithNewMessageCheck> allMessageRoomList = getAllMessageRoom.execute(userPrincipal.id());

        //front 요청으로 응답값 조정
        List<SimpleMessageRoomResponse> messageRoomResponses = allMessageRoomList.stream().map(SimpleMessageRoomResponse::from).toList();
        MessageRoomListResponse messageRoomListResponse = new MessageRoomListResponse(userPrincipal.id(), messageRoomResponses);

        return ResponseEntity.status(HttpStatus.OK).body(Response.success(ResponseCode.SUCCESS.getCode(), messageRoomListResponse));
    }

    @PostMapping("/{messageRoomId}")
    public ResponseEntity<Response> writeMessage(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody MessageRequest messageRequest) {
        writeMessageAndUpdateLastMessage.execute(userPrincipal.id(), messageRequest);
        MessageReadResponse messageInRoom = readAllMessageAndGetMessageRoom.execute(userPrincipal.id(), messageRequest.messageRoomId());
        Response responseBody = Response.success(ResponseCode.SUCCESS.getCode(),messageInRoom);

        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    @GetMapping("/{messageRoomId}")
    public ResponseEntity<Response> readMessageInRoom(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long messageRoomId) {
        MessageReadResponse messageInRoom = readAllMessageAndGetMessageRoom.execute(userPrincipal.id(), messageRoomId);
        Response responseBody = Response.success(ResponseCode.SUCCESS.getCode(),messageInRoom);

        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }
}
