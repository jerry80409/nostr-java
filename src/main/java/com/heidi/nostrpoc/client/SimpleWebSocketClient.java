package com.heidi.nostrpoc.client;

import jakarta.websocket.*;
import java.net.URI;
import java.util.concurrent.CompletableFuture;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Follow this sample
 * https://github.com/rxcats/spring-boot-demo/blob/master/src/main/java/com/example/demo/ws/WebSocketClient.java
 */
@Slf4j
@ClientEndpoint
@RequiredArgsConstructor
public class SimpleWebSocketClient {

  private final WebSocketContainer container;
  private Session userSession;

  private Object lock;

  private String response;

  public SimpleWebSocketClient() {
    container = ContainerProvider.getWebSocketContainer();
  }

  @OnError
  public void onError(Session session, Throwable error) {
    log.info("ws-client socket on error, session id: {}. {}", session.getId(), error.getMessage());
  }

  @OnOpen
  public void onOpen(Session session) {
    log.info("ws-client socket on open, session id: {}", session.getId());
  }

  @OnClose
  public void onClose(Session session, CloseReason closeReason) {
    log.info("ws-client socket on close, session id: {}", session.getId());
  }

  @OnMessage
  public void onMessage(Session session, String msg) {
    try {
      log.info("ws-client socket on message, session id: {}, msg: {}", session.getId(), msg);
      response = msg;

      // i not really understand the lock just follow the example
      synchronized (lock) {
        lock.notify();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void connect(String serverUri) {
    try {
      userSession = container.connectToServer(this, new URI(serverUri));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Async
  public CompletableFuture<String> syncSendMessage(String msg) {
    try {
      long startTime = System.currentTimeMillis();
      lock = new Object();
      userSession.getBasicRemote().sendText(msg);
      synchronized (lock) {
        lock.wait();
      }

      log.info("estimatedTime: {}", System.currentTimeMillis() - startTime);

      return CompletableFuture.completedFuture(response);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }

  public void disconnect() {
    try {
      if (userSession != null && userSession.isOpen()) {
        userSession.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}