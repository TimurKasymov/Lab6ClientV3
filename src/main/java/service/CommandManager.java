package service;

import container.CommandsContainer;
import exceptions.CommandInterruptionException;
import exceptions.InterruptionCause;
import interfaces.Command;
import interfaces.CommandManagerCustom;
import network_utils.SendingManager;
import network_utils.TCPServer;
import service.InputService;
import service.MessageHandler;
import src.converters.SerializationManager;
import src.network.MessageType;
import src.network.Request;
import src.network.Response;
import src.utils.Commands;

import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

public class CommandManager implements CommandManagerCustom {

    private final InputService inputService;
    private final LinkedList<String> commandHistory;
    private final MessageHandler messageHandler;
    private SendingManager sendingManager;
    private SerializationManager serializationManager;
    private String collectionFileName;
    private ConcurrentLinkedDeque<Response> gatewayToResponseThread;
    private InputManager inputManager;

    /**
     * Constructor for making a CommandManager
     *
     * @param messageHandler - service for message handling
     */
    public CommandManager(MessageHandler messageHandler, InputService inputService, SocketChannel socketChannel, TCPServer tcpServer) {
        this.sendingManager = new SendingManager(socketChannel, tcpServer);
        this.messageHandler = messageHandler;
        this.inputService = inputService;
        this.serializationManager = new SerializationManager();
        commandHistory = new LinkedList<>();
        this.inputManager = new InputManager();

    }

    public void setGatewayToResponseThread(ConcurrentLinkedDeque<Response> gatewayToResponseThread){
        this.gatewayToResponseThread = gatewayToResponseThread;
    }

    public void loadCollection() {
        var collectionFileName = getCollectionFileName();
        var request = new Request(MessageType.LOAD_COLLECTION);
        request.requiredArguments.add(collectionFileName);
        var data = serializationManager.serialize(request);
        sendingManager.send(data);
    }

    private String getCollectionFileName() {
        for (; ; ) {
            try {
                Scanner scanner = new Scanner(System.in);
                collectionFileName = "";
                Map<String, String> env = System.getenv();
                if (env != null && env.get("pathToXMLCollection") != null)
                    collectionFileName = env.get("pathToXMLCollection");
                else {
                    messageHandler.displayToUser("Enter a full path to XML file with collection or of the file where collection elements are " +
                            "going to be stored to while being saved: ");
                    collectionFileName = scanner.nextLine();
                    var extension = collectionFileName.split("\\.")[1];
                    if (!extension.equals("xml")) {
                        messageHandler.displayToUser("the extension of the file must be .xml, try again");
                        continue;
                    }
                }
                return collectionFileName;
            } catch (Exception e) {
                messageHandler.displayToUser(e.getMessage());
            }
        }
    }

    /**
     * executes the command in userInput
     *
     * @param userInput the string that user entered as a command
     * @return the execution was successful
     */
    public boolean executeCommand(String userInput) {
        var commandUnits = userInput.trim().toLowerCase().split(" ", 2);

        var enteredCommand = commandUnits[0].trim().toLowerCase();
        if (enteredCommand.contains(Commands.LOAD_COLLECTION)) {
            loadCollection();
            return true;
        }
        if (!gatewayToResponseThread.getFirst().commandRequirements.containsKey(enteredCommand)) {
            messageHandler.displayToUser("Unknown command. Write help for help.");
            return false;
        }
        var commandParams = gatewayToResponseThread.getFirst().commandRequirements.get(enteredCommand);
        var request = new Request((MessageType) Arrays
                .stream(MessageType.values())
                .filter(t-> Objects.equals(t.getCommandDesc(), enteredCommand))
                .toArray()[0]);
        for (var pair : commandParams) {
            for (int i = 0; i < pair.getRight(); i++) {
                try {
                    var arg = inputManager.getReadyArgument(pair.getLeft(), userInput);
                    request.requiredArguments.add(arg);
                } catch (CommandInterruptionException e) {
                    if (e.getInterruptionCause() == InterruptionCause.EXIT)
                        messageHandler.displayToUser("adding product was successfully canceled");
                    else {
                        messageHandler.displayToUser("adding product was canceled by entered command");
                        executeCommand(e.getEnteredCommand());
                    }
                }
            }
        }
        var data = serializationManager.serialize(request);
        sendingManager.send(data);
        return true;
    }


    @Override
    public InputService getInputService() {
        return inputService;
    }

    @Override
    public SendingManager getSendingManager() {
        return sendingManager;
    }

    @Override
    public SerializationManager getSerializationManager() {
        return serializationManager;
    }

    @Override
    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

}
