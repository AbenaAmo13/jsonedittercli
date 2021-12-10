package com.company;

import org.json.simple.JSONObject;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.json.simple.JSONArray;

import org.json.simple.parser.JSONParser;

@Command(name = "CreateKeyValuePair", mixinStandardHelpOptions = true, version = "getRules 1.0",
        description = "This method is to get the JSON Objects from the people.json file or similar file\n" +
                "    and create an arraylist of HashMaps containing the key value pair information")

class CreateKeyPairValue implements Runnable {
    @Option(names = {"-s", "-start"}, description = "press ")
    boolean start;

    private ArrayList<String> rulesList = new ArrayList<>();
    public ArrayList<String> getRulesList() {
        return rulesList;
    }

    private HashMap<String, String> jsonKeyValuePair = new HashMap<>();
    private ArrayList<HashMap> finalKeyValuePairObjects = new ArrayList<>();
    private String pathOfJsonPeopleArrayFile = "";
    private Object obj = new Object();
    private int count = 0;

    @Command(name = "getRules", mixinStandardHelpOptions = true, version = "getRules 1.0",
            description = "Gets the first Json file and Returns an array that has the list of the rules in them")
    public ArrayList<String> getRules() throws IOException {
        boolean completeList = false;
        do{
            String pathOfJsonArrayFileInstruction = "";
            Object obj = new Object();
            File dataJsonFile = new File("");
            //Get the user to input the file path of the json file that has the rules for the k or v values.
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            try {
                //Getting the rules for the files.
                JSONParser parser = new JSONParser();
                System.out.println("Enter the name of the file containing the json array of strings in the format of k:<regex>\" OR \"v:<regex> ");
                pathOfJsonArrayFileInstruction = reader.readLine();
                dataJsonFile = new File(pathOfJsonArrayFileInstruction);
                //Read  the json file given from the path and parse as an object named object
                obj = parser.parse(new FileReader(dataJsonFile));
                //System.out.println(obj.getClass().getName());
                JSONArray instructionJsonArray = (JSONArray) obj;
                Iterator iterator = instructionJsonArray.iterator();
                while(iterator.hasNext()){
                    rulesList.add((String) iterator.next());
                    if(!iterator.hasNext()){
                        completeList = true;
                    }
                }


            } catch (Exception e) {
                if (!dataJsonFile.exists()) {
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
        }while(!completeList);
        return rulesList;
    }

    @Command(name = "getJSONObjects", mixinStandardHelpOptions = true, version = "getJSONObjects 1.0",
            description = "A helper method to get the Json Objects from the files that contain the people infomration.")
    private  JSONObject getJsonObjects(JSONObject person) {
        return person;
    }

    @Command(name = "rulesOnObjects", mixinStandardHelpOptions = true, version = "rulesOnObjects 1.0",
            description = "This is a method to confirm that the objects go along with the rules in the json array file.")
    private  boolean rulesOnObjects(ArrayList<String> rulesInstruction, String pairKey, String pairValue) {
        boolean followsRules = false;
        String k_Value = "";
        String v_Value = "";
        boolean matchFound = false;
        //Get the instruction in the rules json files.(the "k" and "v" instructions)
        for (String instructions : rulesInstruction) {
            int BeforeColonIndex = instructions.indexOf(":") - 1;
            String instructionsLetter = Character.toString(instructions.charAt(BeforeColonIndex));
            //System.out.println(instructionsLetter);
            switch (instructionsLetter) {
                case "k":
                    k_Value = instructions.substring(instructions.lastIndexOf(":") + 1);

                    break;
                case "v":
                    v_Value = instructions.substring(instructions.lastIndexOf(":") + 1);
                    //Check if the k_Value or the v_value  is equals to the keys of the Hashmap
                    Pattern pattern = Pattern.compile(v_Value, Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(pairValue);
                    matchFound = matcher.find();
                    if (matchFound) {
                        //System.out.println(matcher.group());
                    }
            }

        }
        System.out.println(k_Value);
        System.out.println(v_Value);
        if (k_Value.equalsIgnoreCase(pairKey) || matchFound) {
            followsRules = true;
        }
        System.out.println(followsRules);


        return followsRules;

    }






    @Command(name = "CreateKeyValuePairArray", mixinStandardHelpOptions = true, version = "createKeyValuePairArray 1.0",
            description = "This method is to get the JSON Objects from the people.json file which has a Json Array Structure " +
                    "and create an arraylist of HashMaps containing the key value pair information")
    public boolean createKeyValuePairArray() throws IOException {
        boolean writtenFile = false;
        JSONArray peopleJsonArray = (JSONArray) obj;
        System.out.println(peopleJsonArray);
        JSONObject currentObject = new JSONObject();
        String keyName = "";
        String valueName = "";
        for (Object person : peopleJsonArray) {
            currentObject = getJsonObjects((JSONObject) person);
            Set<String> keys = currentObject.keySet();
            System.out.println(keys.size());
            for (String key : keys) {
                keyName = key;
                valueName = (String) currentObject.get(keyName);
                System.out.println("Key is: " + keyName + " value is : " + valueName);
                if (rulesOnObjects(rulesList, keyName, valueName)) {
                    //Star the keyName and valueName and replace the current pair with the starred version
                    String valueNameStarred = valueName.replaceAll(".", "*");
                    HashMap<String, String> tempJsonKeyValuePair = new HashMap<>();
                    //tempJsonKeyValuePair.put(keyName, valueNameStarred);
                    jsonKeyValuePair.put(keyName, valueNameStarred);
                    count = count + 1;
                    System.out.println(jsonKeyValuePair);
                    System.out.println(count);
                }else{
                    jsonKeyValuePair.put(keyName, valueName);
                    count = count + 1;
                    System.out.println(jsonKeyValuePair);
                }
            }
            if (count == keys.size()) {
                finalKeyValuePairObjects.add(jsonKeyValuePair);
                jsonKeyValuePair = new LinkedHashMap<>();
                count = 0;
            } else {
                System.out.println("More key value pairs to go");
            }
        }
        if(writeJSONFile(finalKeyValuePairObjects,pathOfJsonPeopleArrayFile)){
            writtenFile = true;
        };
    return writtenFile;

    }

    @Command(name="writeJSONFILE", mixinStandardHelpOptions = true, version = "writeJSONFile 1.0",
            description = "This command will finally write the starred JSON objects into the current file.")
    public static boolean writeJSONFile(ArrayList<HashMap> starredJsonObjects, String filename) throws IOException {

        File updatedFile = new File("");
        boolean isFileUnique = false;
        do {
            System.out.println("Enter the name of the new file that will contain the updated json data file:");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String newFileName = reader.readLine();
            int count = 1;
            updatedFile = new File(newFileName);

            if (updatedFile.exists()) {
                System.out.println("Your file name already exists, please type a new file name");
            }else{
                updatedFile = new File(newFileName + ".json");
                updatedFile.createNewFile();
                isFileUnique= true;
            }
        }while(!isFileUnique);

        boolean doneWriting = false;
        JSONArray starredJsonArray = new JSONArray();
        starredJsonObjects.forEach(object -> {
            starredJsonArray.add(object);
        });
        System.out.println(starredJsonArray);
        //Write JSON file
        try (FileWriter file = new FileWriter(updatedFile)) {
            //We can write any JSONArray or JSONObject instance to the file
            file.write(starredJsonArray.toJSONString());
            file.flush();
            doneWriting = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doneWriting;
    }




    @Override
    public void run(){
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        //boolean fileExist = false;
        File dataJsonFile = new File("");
        boolean finishedExecuting = false;
            try {
                rulesList = getRules();
                do {
                    System.out.println("Enter the name of the file containing the json files with the information of people:  ");
                    pathOfJsonPeopleArrayFile = reader.readLine();
                    dataJsonFile = new File(pathOfJsonPeopleArrayFile);

                    if (!dataJsonFile.exists()) {

                        System.out.println("The directory " + pathOfJsonPeopleArrayFile + " doesn't exist.");
                    }else{
                        JSONParser parser = new JSONParser();
                        obj = parser.parse(new FileReader(dataJsonFile));
                        if (obj.getClass().getName().contains("Array")) {
                            if (createKeyValuePairArray()) {
                                finishedExecuting = true;
                            }
                            System.out.println("The final pairs are: " + finalKeyValuePairObjects);
                        } else {
                            System.out.println("Not an array");
                        }
                    }

                } while (!finishedExecuting);
            }  catch (Exception e) {
            }
        }
    public static void main(String[] args) {
        // write your code here
        int exitCode = new CommandLine(new CreateKeyPairValue()).execute(args);
        System.exit(exitCode);
    }
}


