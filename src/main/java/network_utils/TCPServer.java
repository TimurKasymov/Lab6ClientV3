package network_utils;

import org.slf4j.Logger;
import src.loggerUtils.LoggerManager;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class TCPServer {
    private final InetSocketAddress host;
    private SocketChannel socketChannel;
    private Logger logger;

    public TCPServer(InetSocketAddress host) {
        this.host = host;
        this.logger = LoggerManager.getLogger(TCPServer.class);
    }


    synchronized public SocketChannel start() {
        for(;;){
            try {
                if(socketChannel != null)
                    socketChannel.close();
                this.socketChannel = SocketChannel.open();
                var socketAddress = new InetSocketAddress("localhost", 23588);
                socketChannel.bind(socketAddress);
                socketChannel.configureBlocking(false);
                socketChannel.connect(host);
                return socketChannel;
            } catch (Exception e) {
                logger.info("TCP server: " + e.getMessage());
                try{
                    socketChannel.close();
                    Thread.sleep(3000);
                }
                catch (Exception e1){
                    logger.info("TCP server: " + e1.getMessage());
                }
            }
        }
    }

        public SocketChannel getSocketChannel () {
            return this.socketChannel;
        }
    }
