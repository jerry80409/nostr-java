package com.heidi.nostrpoc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heidi.nostrpoc.client.AggWebSocketClient;
import com.heidi.nostrpoc.client.SimpleWebSocketClient;
import com.heidi.nostrpoc.constant.client.ClientEventType;
import com.heidi.nostrpoc.constant.client.Request;
import com.heidi.nostrpoc.util.NostrUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@EnableAsync
@RestController
@Slf4j
@RequiredArgsConstructor
public class AggController {

    private static final String PUBLIC_KEY = "e0131db0689078f518710ad970a2b37f7e1af28769238a447cf95455df72eb4a";

    private final AggWebSocketClient aggWebSocketClient;

    private final ObjectMapper objectMapper;

    @GetMapping("/agg/connect")
    public String connect() {
        aggWebSocketClient.connect("wss://relay.nekolicio.us");
        aggWebSocketClient.connect();
        return "connect success";
    }

    @ResponseStatus(code = HttpStatus.ACCEPTED)
    @GetMapping("/agg/subscribe")
    public String subscribe() throws JsonProcessingException {
        List<Object> list = new ArrayList<>();
        list.add(ClientEventType.REQ);
        list.add("12j312n31knkajsndaksndas");
        Request request = Request.builder().limit(5).build();
        list.add(request);
        aggWebSocketClient.syncSendMessage(NostrUtils.serializeEvent(list));
        return "send aggregation request success";
    }

}
