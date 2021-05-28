package com.company.enums;

public enum Commands {
    //args should be in {} for better formatting
    NO_FEEDBACK("","special"),
    SIGN_IN("","special"),
    SIGN_UP("","special"),
    HELP ("","Available commands"),
    INFO ("","Information about current stat of Collection"),
    SHOW ("","Prints all the elements"),
    ADD ("","Add a new worker"),
    UPDATE("{id}","Update worker's fields"),
    REMOVE_BY_ID("{id}","Removes worker"), //it's not me shit-naming, it's the tech task
    CLEAR("","Clears the database"),
    EXECUTE_SCRIPT("{file_name}","execute commands from file"),
    EXIT("","Exit database"),
    ADD_IF_MAX("","Add element, if its SALARY is max"),
    REMOVE_GREATER("{salary}","Remove all elements, greater than given"),
    REMOVE_LOWER("{salary}","Remove all elements, lower than given"),
    GROUP_COUNTING_BY_POSITION("","Print groups made by position"),
    COUNT_LESS_THAN_START_DATE("{start date (yyyy-mm-dd)}","Print number of elements with START DATE lesser than given"),
    FILTER_GREATER_THAN_START_DATE("{start date (yyyy-mm-dd)}","Print elements with START DATE greater than given"),
    FILL_FROM_FILE("","special");

    private String argument;
    private String description;


    Commands(String argument, String description) {
        this.argument = argument;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getArgument(){
        return argument;
    }

    public static String[] getCommands() {
        String[] commands = new String[Commands.values().length];
        Commands[] commandsEnum = values();
        for (int i = 0; i < Commands.values().length; i++) {
            commands[i] = commandsEnum[i].toString();
        }
        return commands;
    }

    public static String[] getCommandsWithDescriptions() {
        String[] commands = new String[Commands.values().length];
        Commands[] commandsEnum = values();
        for (int i = 0; i < Commands.values().length; i++) {
            //not including special ones
            if (commandsEnum[i].getDescription().equals("special")){
                continue;
            }

            if (!commandsEnum[i].getArgument().equals("")){
                commands[i] = commandsEnum[i].toString() + " " + commandsEnum[i].getArgument() + ": " + commandsEnum[i].getDescription();
            } else {
                commands[i] = commandsEnum[i].toString() + ": " + commandsEnum[i].getDescription();
            }
        }
        return commands;
    }

    /**
     * Finds enum from string
     * @param s
     * @return
     */
    public static Commands findEnum(String s){
        s = s.toUpperCase();
        Commands command = null;
        for (int i = 0; i < values().length; i++) {
            if (s.equals(Commands.values()[i].toString())){
                command = Commands.values()[i];
            }
        }
        return command;
    }

    /**
     * Checks if string is similar to enum
     * @param s
     * @return
     */
    public static boolean isEnum(String s){
        s = s.toUpperCase();
        for (int i = 0; i < values().length; i++) {
            if (s.equals(Commands.values()[i].toString())){
                return true;
            }
        }
        return false;
    }
}
