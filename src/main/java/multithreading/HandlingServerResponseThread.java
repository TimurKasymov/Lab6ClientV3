package multithreading;

import interfaces.CommandManagerCustom;
import network_utils.ReceivingManager;
import network_utils.TCPServer;
import service.CommandManager;
import service.InputService;
import service.MessageHandler;
import src.converters.SerializationManager;
import src.network.responses.Response;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public class HandlingServerResponseThread extends Thread {

    private SocketChannel socketToListen;
    private final TCPServer tcpServer;
    private final int DATA_CHUNK = 1024;
    private final ReceivingManager receivingManager;
    private final CommandManagerCustom commandManagerCustom;

    public HandlingServerResponseThread(TCPServer tcpServer) {
        this.socketToListen = tcpServer.getSocketChannel();
        this.tcpServer = tcpServer;
        this.receivingManager = new ReceivingManager(socketToListen, tcpServer);
        var messageHandler = new MessageHandler();
        this.commandManagerCustom = new CommandManager(messageHandler, new InputService(messageHandler), socketToListen, tcpServer);
    }

    @Override
    public void run() {
        while (true) {
            var data = receivingManager.receive();
            if(data == null)
                continue;
            var serializer = new SerializationManager();
            var response = (Response) serializer.deserialize(data);
            commandManagerCustom.handleResponse(response);
        }
    }
}
