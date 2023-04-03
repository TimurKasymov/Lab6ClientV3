package commands;

import container.CommandsContainer;
import exceptions.CommandInterruptionException;
import exceptions.InterruptionCause;
import interfaces.Command;
import interfaces.CommandManagerCustom;
import src.network.requests.ExecuteScriptRequest;
import src.network.responses.Response;
import java.util.LinkedList;

public class ExecuteScriptCommand extends CommandBase implements Command {
    private int recDepth = -1;

    public ExecuteScriptCommand(CommandManagerCustom commandManager) {
        super(commandManager);
    }

    @Override
    public boolean execute(String[] args) {
        var commandMessageHandler = commandManager.getMessageHandler();
        try {
            for(;;){
                commandMessageHandler.displayToUser("specify recursion depth");
                recDepth = commandManager.getInputService().getInt();
                if(recDepth > 0)
                    break;
                commandMessageHandler.displayToUser("recursion depth must be > 0");
            }
            var request = new ExecuteScriptRequest(args[0], recDepth);
            sendToServer(request);

        } catch (CommandInterruptionException e) {
            if (e.getInterruptionCause() == InterruptionCause.EXIT)
                commandMessageHandler.displayToUser("adding product was successfully canceled");
            else {
                commandMessageHandler.displayToUser("adding product was canceled by entered command");
                commandManager.executeCommand(e.getEnteredCommand());
            }
        }
        return true;
    }

    @Override
    public void handleResponse(Response response) {
        handleResponseByDefault(response);
    }

}