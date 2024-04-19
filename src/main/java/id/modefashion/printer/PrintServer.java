package id.modefashion.printer;

import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import id.modefashion.printer.dto.ReceiptLineData;
import id.modefashion.printer.worker.ReceiptWorker;
import id.modefashion.printer.worker.ReceiptWorkerString;

public class PrintServer extends WebSocketServer {
  private Set<WebSocket> connections;
  private PropertiesConfiguration config;
  private static final Logger logger = LoggerFactory.getLogger(PrintServer.class);

  public PrintServer(PropertiesConfiguration config) {
    super(new InetSocketAddress(config.getInt("printer.port")));
    connections = Collections.synchronizedSet(new HashSet<WebSocket>());
    this.config = config;
  }

  @Override
  public void onOpen(WebSocket conn, ClientHandshake handshake) {
    logger.info("Connection open");
  }

  @Override
  public void onClose(WebSocket conn, int code, String reason, boolean remote) {
    logger.info("Connection close");
  }

  @Override
  public void onMessage(WebSocket conn, String message) {
    logger.info("Received message");
    Type listType = new TypeToken<List<ReceiptLineData>>() {
    }.getType();
    if (message.contains("type")) {
      List<ReceiptLineData> data = new Gson().fromJson(message, listType);
      ReceiptWorker worker = new ReceiptWorker(data, this.config);
      worker.proceed();
    } else {
      String dataString = message;
      ReceiptWorkerString worker = new ReceiptWorkerString(dataString, this.config);
      worker.proceed();
    }
    sendResponse("Success");
  }

  private void sendResponse(String message) {
    for (WebSocket conn : connections) {
      conn.send(message);
    }
  }

  @Override
  public void onError(WebSocket conn, Exception ex) {
    logger.error(ex.getMessage(), ex);
  }

  @Override
  public void onStart() {
    logger.info("Websocket onStart...");
  }
}
