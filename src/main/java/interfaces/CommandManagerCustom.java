package interfaces;

import network_utils.SendingManager;
import service.InputService;
import service.MessageHandler;
import src.converters.SerializationManager;
import src.models.Product;
import src.network.requests.Request;
import src.network.responses.Response;

import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Scanner;


public interface CommandManagerCustom {
    /** executes given command */
    boolean executeCommand(String userInput);
    /** gets undoManager */
    public InputService getInputService();
    SendingManager getSendingManager();
    SerializationManager getSerializationManager();
    void handleResponse(Response response);
    MessageHandler getMessageHandler();
    void loadCollection();
}
