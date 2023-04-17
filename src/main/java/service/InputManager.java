package service;


import exceptions.CommandInterruptionException;
import src.utils.Argument;

public class InputManager {

    private ScriptFilePacker filePacker;
    private InputService inputService;


    public InputManager() {
        filePacker = new ScriptFilePacker();
        inputService = new InputService(new MessageHandler());
    }

    public Object getReadyArgument(Argument argument, String enteredCommand) throws CommandInterruptionException {
        switch (argument) {
            case ID -> {
                return inputService.getID(enteredCommand);
            }
            case COST -> {
                return inputService.inputManufactureCost(enteredCommand);
            }
            case SCRIPT_HASH_MAP -> {
                filePacker.pack(inputService.validateScriptName(enteredCommand));
                return filePacker.getPackedScripts();
            }
            case PRICE -> {
                return inputService.inputPrice(enteredCommand);
            }
            case PRODUCT -> {
                return inputService.inputProduct(enteredCommand);
            }
            case NUMBER -> {
                if(enteredCommand.split(" ").length == 2){
                    try {
                        return Integer.parseInt(enteredCommand.split(" ")[1]);
                    }
                    catch (Exception e){
                        System.err.println(e.getMessage());
                    }
                }
                return inputService.getInt();
            }
        }
        return null;
    }
}
