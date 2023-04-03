package commands;

import exceptions.CommandInterruptionException;
import exceptions.InterruptionCause;
import interfaces.Command;
import interfaces.CommandManagerCustom;
import src.network.requests.UndoRequest;
import src.network.responses.Response;

public class UndoCommand extends CommandBase implements Command {


    public UndoCommand(CommandManagerCustom commandManager) {
        super(commandManager);
    }

    @Override
    public boolean execute(String[] args) {
        var console = commandManager.getMessageHandler();
        try{
            var number = 0;
            for(;;){
                number = commandManager.getInputService().getInt();
                if(number > 0)
                    break;
                console.displayToUser("number of commands must by > 0");
            }
            var request = new UndoRequest(number);
            sendToServer(request);
        }
        catch (CommandInterruptionException e){
            if(e.getInterruptionCause() == InterruptionCause.EXIT)
                console.displayToUser("adding product was successfully canceled");
            else{
                console.displayToUser("adding product was canceled by entered command");
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
