package commands;

import interfaces.Command;
import interfaces.CommandManagerCustom;
import src.network.requests.FilterByManufactureCostRequest;
import src.network.responses.FilterByManufactureCostResponse;
import src.network.responses.Response;


public class FilterByManufactureCostCommand extends CommandBase implements Command {
    public FilterByManufactureCostCommand(CommandManagerCustom commandManager) {
        super(commandManager);
    }

    @Override
    public boolean execute(String[] args) {
        var commandMessageHandler = commandManager.getMessageHandler();
        try {
            var manufactureCost = Double.valueOf(args[0]);
            var request = new FilterByManufactureCostRequest(manufactureCost);
            sendToServer(request);
        } catch (Exception exception) {
            commandMessageHandler.displayToUser(String.format("Manufacture cost must be from %s to %s. Try typing this command again", 0, Double.MAX_VALUE));
            return false;
        }
        return true;
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
