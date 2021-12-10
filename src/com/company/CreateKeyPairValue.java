package com.company;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.Callable;


import org.json.simple.JSONArray;

import org.json.simple.parser.JSONParser;

@Command(name = "CreateKeyValuePair", mixinStandardHelpOptions = true, version = "getRules 1.0",
        description = "")

class CreateKeyPairValue implements Runnable {
    @Parameters(index = "0", description = "The arraylist containing the strings of the rules in the format k:<regex>\" OR \"v:<regex>")
    private ArrayList<String> rulesList = new ArrayList<>();

    public ArrayList<String> getRulesList() {
        return rulesList;
    }

    @Command(name = "getRules", mixinStandardHelpOptions = true, version = "getRules 1.0",
            description = "Gets the first Json file and Returns an array that has the list of the rules in them")
    public  ArrayList<String> getRules() throws IOException {
        String pathOfJsonArrayFileInstruction = "";
        Object obj = new Object();
        //Get the user to input the file path of the json file that has the rules for the k or v values.
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            //Getting the rules for the files.
            JSONParser parser = new JSONParser();
            System.out.println("Enter the name of the file containing the json array of strings in the format of k:<regex>\" OR \"v:<regex> ");
            pathOfJsonArrayFileInstruction = reader.readLine();
            //Read  the json file given from the path and parse as an object named object
            obj = parser.parse(new FileReader(pathOfJsonArrayFileInstruction));
            //System.out.println(obj.getClass().getName());
            JSONArray instructionJsonArray = (JSONArray) obj;
            instructionJsonArray.forEach(object -> {
                //Place the instruction into the arraylist
                rulesList.add((String) object);
            });
        } catch (Exception e) {
            if (pathOfJsonArrayFileInstruction != null) {
                System.out.println(e.toString());
                System.out.println("Enter the name of the file containing the json array of strings in the format of k:<regex>\" OR \"v:<regex> ");
                pathOfJsonArrayFileInstruction = reader.readLine();
            }
            //If the the file does not contain a json array
            if (!obj.getClass().getName().contains("Array")) {
                System.out.println(e.toString());
                System.out.println("Enter the name of the file containing the json array of strings in the format of k:<regex>\" OR \"v:<regex> ");
                pathOfJsonArrayFileInstruction = reader.readLine();
            }

        }
        return rulesList;

    }
    @Override
    public void run() {
        ArrayList<String> instructions = new ArrayList<>();

        try {
            instructions = getRules();
        } catch (IOException e) {
            e.toString();
        }
        System.out.println("Get rules list:" + instructions);

    }
    public static void main(String[] args) {
        // write your code here
        int exitCode = new CommandLine(new CreateKeyPairValue()).execute(args);
        System.exit(exitCode);
    }
}


