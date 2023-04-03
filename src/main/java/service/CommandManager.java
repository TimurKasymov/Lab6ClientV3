package service;

import commands.*;
import container.CommandsContainer;
import interfaces.Command;
import interfaces.CommandManagerCustom;
import network_utils.SendingManager;
import network_utils.TCPServer;
import service.InputService;
import service.MessageHandler;
import src.converters.SerializationManager;
import src.network.requests.LoadFileRequest;
import src.network.responses.LoadFileResponse;
import src.network.responses.Response;
import src.utils.Commands;

import java.nio.channels.SocketChannel;
import java.util.*;

public class CommandManager implements CommandManagerCustom {

    private final InputService inputService;
    private final HashMap<String, Command> commandsMap;

    private final LinkedList<String> commandHistory;
    private final MessageHandler messageHandler;
    private SendingManager sendingManager;
    private SerializationManager serializationManager;
    private String collectionFileName;


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
        commandsMap = new HashMap<>();
        commandsMap.put("add", new AddCommand(this));
        commandsMap.put("clear", new ClearCommand(this));
        commandsMap.put("update_by_id", new UpdateByIdCommand(this));
        commandsMap.put("execute_script", new ExecuteScriptCommand(this));
        commandsMap.put("show", new ShowCommand(this));
        commandsMap.put("filter_greater_than_price", new FilterGreaterThanPriceCommand(this));
        commandsMap.put("print_unique_unit_of_measure", new PrintUniqueUnitOfMeasureCommand(this));
        commandsMap.put("remove_by_id", new RemoveByIdCommand(this));
        commandsMap.put("remove_first", new RemoveFirstCommand(this));
        commandsMap.put("reorder", new ReorderCommand(this));
        commandsMap.put("history", new HistoryCommand(this));
        commandsMap.put("help", new HelpCommand(this));
        commandsMap.put("info", new InfoCommand(this));
        commandsMap.put("filter_by_manufacture_cost", new FilterByManufactureCostCommand(this));
        commandsMap.put("undo_commands", new UndoCommand(this));
        CommandsContainer.setCommands(commandsMap.keySet().stream().toList());
    }

    public void loadCollection(){
        var collectionFileName = getCollectionFileName();
        var request = new LoadFileRequest(collectionFileName);
        sendingManager.send(serializationManager.serialize(request));
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
        if(enteredCommand.contains(Commands.LOAD_COLLECTION)){
            loadCollection();
            return true;
        }
        if (!commandsMap.containsKey(enteredCommand)) {
            messageHandler.displayToUser("Unknown command. Write help for help.");
            return false;
        }
        var command = commandsMap.get(enteredCommand);
        if(command == null)
            return false;
        commandHistory.add(enteredCommand);
        command.execute(Arrays.copyOfRange(commandUnits, 1, commandUnits.length));
        return true;
    }

    @Override
    public void handleResponse(Response response) {
        if (response.getName().equals(Commands.LOAD_COLLECTION)) {
            var loadResp = (LoadFileResponse) response;
            if (loadResp.successfully) {
                messageHandler.displayToUser("Collection was loaded successfully");
            } else {
                messageHandler.displayToUser("Collection was not loaded");
                messageHandler.displayToUser("To try again load the collection enter: load_collection");
            }
            return;
        }
        var command = commandsMap.get(response.getName());
        command.handleResponse(response);
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
