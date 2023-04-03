package network_utils;

import com.google.common.primitives.Bytes;
import com.sun.jdi.connect.spi.ClosedConnectionException;
import src.loggerUtils.LoggerManager;

import java.io.IOException;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Objects;

public class SendingManager {

    private final int PACKET_SIZE = 1024;
    private final int DATA_SIZE = PACKET_SIZE - 1;

    private final SocketChannel socketToWrite;
    private final int DATA_CHUNK = 1024;
    private final TCPServer tcpServer;

    public SendingManager(SocketChannel socketToWrite, TCPServer tcpServer) {
        this.socketToWrite = socketToWrite;
        this.tcpServer = tcpServer;
    }

    public void send(byte[] data)  {
        var logger = LoggerManager.getLogger(SendingManager.class);
        for(;;) {
            try {
                while (!tcpServer.getSocketChannel().isConnected()){
                    logger.info("not connected, waiting..");
                    Thread.sleep(4000);
                }

                byte[][] ret = new byte[(int) Math.ceil(data.length / (double) DATA_SIZE)][DATA_SIZE];

                int start = 0;
                for (int i = 0; i < ret.length; i++) {
                    ret[i] = Arrays.copyOfRange(data, start, start + DATA_SIZE);
                    start += DATA_SIZE;
                }

                logger.info("Отправляется " + ret.length + " чанков...");

                for (int i = 0; i < ret.length; i++) {
                    var chunk = ret[i];
                    if (i == ret.length - 1) {
                        var lastChunk = Bytes.concat(chunk, new byte[]{1});
                        tcpServer.getSocketChannel().write(ByteBuffer.wrap(lastChunk));
                        logger.info("Последний чанк размером " + chunk.length + " отправлен на сервер.");
                    } else {
                        tcpServer.getSocketChannel().write(ByteBuffer.wrap(chunk));
                        logger.info("Чанк размером " + chunk.length + " отправлен на сервер.");
                    }
                }
                return;
            }
            catch (IOException e) {
                logger.error(e.getMessage());

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
