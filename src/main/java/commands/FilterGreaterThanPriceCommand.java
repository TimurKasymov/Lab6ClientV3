package commands;

import interfaces.Command;
import interfaces.CommandManagerCustom;
import src.network.requests.FilterGreaterThanPriceRequest;
import src.network.responses.FilterByManufactureCostResponse;
import src.network.responses.Response;

public class FilterGreaterThanPriceCommand extends CommandBase implements Command {
    public FilterGreaterThanPriceCommand(CommandManagerCustom commandManager){
        super(commandManager);
    }

    @Override
    public boolean execute(String[] args) {
        var commandMessageHandler = commandManager.getMessageHandler();
        try{
            var price = Float.parseFloat(args[0]);
            var request = new FilterGreaterThanPriceRequest(price);
            sendToServer(request);
            return true;
        }
        catch (NumberFormatException e){
            commandMessageHandler.displayToUser("ID must be provided and it must be a number. Try typing this command again");
            return false;
        }
    }

    @Override
    public void handleResponse(Response response) {
        var commandMessageHandler = commandManager.getMessageHandler();
        var filterResponse = (FilterByManufactureCostResponse) response;
        for (var prod : filterResponse.getProducts()) {
            commandMessageHandler.displayToUser(prod.toString() + "\n" + "\n");
        }
    }
}
