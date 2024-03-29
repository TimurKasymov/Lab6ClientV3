package network_utils;

import com.google.common.primitives.Bytes;
import src.converters.SerializationManager;
import src.network.MessageType;
import src.network.Request;
import src.network.Response;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ReceivingManager {

    private byte[] receivedData;
    private final SocketChannel socketToListen;
    private final int DATA_CHUNK = 1024;
    private final TCPServer tcpServer;
    private final ConcurrentLinkedDeque<Response> concurrentDequeue;
    private boolean requestForCommandsSent = false;


    public ReceivingManager(SocketChannel socketToListen, TCPServer tcpServer,
                            ConcurrentLinkedDeque<Response> concurrentDequeue) {
        this.socketToListen = socketToListen;
        this.tcpServer = tcpServer;
        this.concurrentDequeue = concurrentDequeue;
        this.receivedData = new byte[0];
    }

    public byte[] receive() {
        receivedData = new byte[0];
        for (; ; ) {
            try {
                if (tcpServer.getSocketChannel() == null)
                    continue;
                // true - when client is trying to connect to the server in non-blocking mode
                // and if specified host does not exist, finishConnect() will throw error when first time called and close the connection
                while (tcpServer.getSocketChannel().isConnectionPending()) {
                    requestForCommandsSent = false;
                    var finished = tcpServer.getSocketChannel().finishConnect();
                }
                // if because of called finishConnect the connection got closed we are trying to make another connection
                if (!tcpServer.getSocketChannel().isOpen()) {
                    tcpServer.start();
                    Thread.sleep(3000);
                    continue;
                }
                ByteBuffer byteBuffer = ByteBuffer.allocate(DATA_CHUNK);
                var readBytes = tcpServer.getSocketChannel().read(byteBuffer);
                if(concurrentDequeue.size() == 0 && !requestForCommandsSent){
                    var sendingManager = new SendingManager(socketToListen, tcpServer);
                    var serManager = new SerializationManager();
                    sendingManager.send(serManager.serialize(new Request(MessageType.ALL_AVAILABLE_COMMANDS)));
                    requestForCommandsSent = true;
                    continue;
                }
                if (readBytes == 0)
                    continue;
                if (readBytes == -1)
                    tcpServer.getSocketChannel().close();
                receivedData = Bytes.concat(receivedData, Arrays.copyOf(byteBuffer.array(), byteBuffer.array().length - 1));
                // reached the end of the object being sent
                if (byteBuffer.array()[readBytes - 1] == 1) {
                    return receivedData;
                }
                byteBuffer.clear();
            } catch (Exception e) {
                // server has crashed, and we got isConnectionPending - true and isOpen - true as well
                // and so we close the connection, because this channel is left by the server and create another one
                if (Objects.equals(e.getMessage(), "Connection reset")) {
                    try {
                        Thread.sleep(3000);
                        tcpServer.getSocketChannel().close();
                        tcpServer.start();
                    } catch (Exception e1) {
                    }
                }
            }
        }
    }
}
