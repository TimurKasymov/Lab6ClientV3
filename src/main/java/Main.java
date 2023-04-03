import multithreading.HandlingServerResponseThread;
import network_utils.TCPServer;
import service.CommandManager;
import service.InputService;
import service.MessageHandler;
import src.converters.SerializationManager;

import java.net.InetSocketAddress;
import java.util.Scanner;


public class Main {

    private static final int PORT = 23586;

    public static void main(String[] args){
        var server = new TCPServer(new InetSocketAddress("localhost", PORT));
        try(var serverSocket = server.start()){
            var messageHandler = new MessageHandler();
            var responseHandlingThread = new HandlingServerResponseThread(server);
            var commandManager = new CommandManager(messageHandler, new InputService(messageHandler), serverSocket, server);
            responseHandlingThread.start();
            commandManager.loadCollection();
            var scanner = new Scanner(System.in);
            while (true){
                messageHandler.displayToUser("Enter a command: ");
                commandManager.executeCommand(scanner.nextLine());
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

}