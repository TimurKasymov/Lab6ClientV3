package multithreading;

import container.CommandsContainer;
import network_utils.ReceivingManager;
import network_utils.TCPServer;
import service.MessageHandler;
import src.converters.SerializationManager;
import src.network.MessageType;
import src.network.Response;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedDeque;

public class HandlingServerResponseThread extends Thread {

    private SocketChannel socketToListen;
    private final ReceivingManager receivingManager;
    private final ConcurrentLinkedDeque<Response> concurrentDequeue;
    private final MessageHandler messageHandler;

    public HandlingServerResponseThread(TCPServer tcpServer, ConcurrentLinkedDeque<Response> concurrentDequeue) {
        this.socketToListen = tcpServer.getSocketChannel();
        this.concurrentDequeue = concurrentDequeue;
        this.receivingManager = new ReceivingManager(socketToListen, tcpServer, concurrentDequeue);
        messageHandler = new MessageHandler();
    }

    @Override
    public void run() {
        while (true) {
            var data = receivingManager.receive();
            if(data == null)
                continue;
            var serializer = new SerializationManager();
            var response = (Response) serializer.deserialize(data);
            if(response == null)
                continue;
            if (response.messageType == MessageType.ALL_AVAILABLE_COMMANDS) {
                concurrentDequeue.add(response);
                CommandsContainer.setCommands(response.commandRequirements.keySet().stream().toList());
                continue;
            }
            messageHandler.displayToUser(response.serverResponseToCommand);
        }
    }
}
