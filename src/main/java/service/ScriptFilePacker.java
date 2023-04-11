package service;

import src.utils.Commands;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;


public class ScriptFilePacker {
    private final HashMap<String, List<String>> packedScripts = new HashMap<>();
    private boolean containsRecursion;
    public void pack(String fileName)  {
        try{
            Scanner reader = new Scanner(new FileReader(fileName));
            packedScripts.put(fileName, new LinkedList<>());
            String command;
            while (reader.hasNext() && (command = reader.nextLine()) != null) {
                packedScripts.get(fileName).add(command);
                var split = command.split(" ");
                if(!command.contains(Commands.EXECUTE_SCRIPT) || split.length != 2)
                    continue;
                var passageName = getFileNameIfValidated(command);
                var contains = packedScripts.containsKey(passageName);
                containsRecursion = containsRecursion || contains;
                if(passageName == null || contains)
                    continue;
                pack(passageName);
            }
        }
        catch (FileNotFoundException e){
            System.out.println(e.getMessage());
        }
    }

    public HashMap<String, List<String>>  getPackedScripts(){
        return packedScripts;
    }

    private String getFileNameIfValidated(String command){
        var split = command.split(" ");
        if(!command.contains(Commands.EXECUTE_SCRIPT) || split.length != 2)
            return null;
        if(split[1].split("\\.").length == 2 && split[1].split("\\.")[1].equals("txt"))
            return split[1];
        return null;
    }

    public boolean containsRecursion(){
        return containsRecursion;
    }
}
