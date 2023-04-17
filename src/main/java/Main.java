import com.fasterxml.jackson.databind.ObjectMapper;
import container.SettingsContainer;
import network_utils.TCPServer;
import service.CommandManager;
import service.InputService;
import service.MessageHandler;
import settings.SettingsModel;
import src.network.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedDeque;
import multithreading.HandlingServerResponseThread;


public class Main {

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, IOException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        var inputStream = classloader.getResourceAsStream("settings.json");
        if (inputStream == null){
            System.out.println("settings file not found");
            System.exit(-1);
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        var str = sb.toString();
        ObjectMapper mapper = new ObjectMapper();
        var settings = mapper.readValue(str, SettingsModel.class);
        SettingsContainer.settingsModel = settings;
        var PORT = settings.serverPort;
        var server = new TCPServer(new InetSocketAddress("localhost", PORT));
        try(var serverSocket = server.start()){
            var messageHandler = new MessageHandler();
            var concurrentDequeue = new ConcurrentLinkedDeque<Response>();
            var responseHandlingThread = new HandlingServerResponseThread(server, concurrentDequeue);
            var commandManager = new CommandManager(messageHandler, new InputService(messageHandler), serverSocket, server);
            responseHandlingThread.start();
            commandManager.loadCollection();
            commandManager.setGatewayToResponseThread(concurrentDequeue);
            var scanner = new Scanner(System.in);
            messageHandler.displayToUser("Trying to connect to the server");
            while (true){
                if((long) concurrentDequeue.size() == 0)
                    continue;
                messageHandler.displayToUser("Enter a command: ");
                commandManager.executeCommand(scanner.nextLine());
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}