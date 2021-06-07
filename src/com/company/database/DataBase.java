package com.company.database;


import com.company.Login.User;
import com.company.classes.Worker;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.stream.Collectors;

import com.company.enums.Commands;
import com.company.enums.Position;

public class DataBase implements Serializable {

    public LinkedList<Worker> getDatabase() {
        return database;
    }

    public LinkedList<Worker> database;
    private Scanner terminal;
    private String scriptName;
    private int recursionCounter;

    private User user;

    //check booleans
    private boolean isInitialized;

    private ZonedDateTime initializationTime;

    public DataBase(){}

    //public methods

    /**
     * Initializing database (like constructor), but without a file (if it's not given)
     */
    public void initialize(){
        //initializing variables
        this.database = new LinkedList<>();
        this.terminal = new Scanner(System.in);
        this.initializationTime = ZonedDateTime.now();
        this.recursionCounter = 0;
        this.scriptName = "";
        this.isInitialized = true;

        this.user = new User();

        System.out.println("Server database has been initialized");
        System.out.println("------------------------------------");
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setDatabase(LinkedList<Worker> database){
        //filtering null workers from the good ones
        this.database = (LinkedList<Worker>) Terminal.getInstance(
                database.stream()
                .filter(worker -> !Objects.isNull(worker)).collect(Collectors.toList())
        );
        for (Worker w:this.database) {
            //w.setId();
        }
        sortBySize();
    }

    //protected methods

    /**
     * Returns index in database by id
     * @param id
     * @return
     */
    public int returnIndexById(long id){
        int index = -1;
        for (int i = 0; i < database.size(); i++) {
            if (database.get(i).getId() == id){
                index = i;
                break;
            }
        }
        return index;
    }

    public Worker getWorkerByIndex(int index){
        return this.database.get(index);
    }

    //terminal commands

    public String help(){
        StringBuilder sb = new StringBuilder();

        String[] commands = Commands.getCommandsWithDescriptions();
        sb.append("Commands: ");sb.append("\n");
        for (int i = 0; i < Commands.values().length - 1; i++) {
            if (commands[i] != null) {
                sb.append(" ").append(commands[i]).append("\n");
            }
        }

        return sb.toString();
    }

    public String add(Worker worker) {
        this.database.add(worker);
        sortBySize();
        return "New worker was successfully added!";
    }

    public List<Vector<String>> show(){
        List<Vector<String>> rows = new ArrayList<>();

        //checking if database is empty
        if (database.isEmpty()){
            return rows;
        }

        StringBuilder coord = new StringBuilder();
        Vector<String> sb = new Vector<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (Worker worker : database) {
            sb.add(worker.getName());sb.add(String.valueOf(worker.getId()));sb.add(String.valueOf(worker.getSalary()));

            //adding position
            if (worker.getPosition() != null){
                sb.add(worker.getPosition().toString());
            } else {
                sb.add("null");
            }

            //adding personality
            if (worker.getPerson() != null) {
                sb.add(worker.getPerson().getHeight() + ", " + worker.getPerson().getWeight());
            } else {
                sb.add("null");
            }

            coord.append(worker.getCoordinates().getX()).append(", ").append(worker.getCoordinates().getY());
            sb.add(coord.toString());
            coord.delete(0, coord.length());

            sb.add(worker.getStartDate().format(formatter));
            if (worker.getEndDate() != null){
                sb.add(worker.getEndDate().format(formatter));
            } else {
                sb.add("null");
            }

            sb.add(worker.getUser().getLogin());

            rows.add((Vector<String>) sb.clone());
            sb.clear();
        }
        //stringBuilder.append(Terminal.formatAsTable(rows));

        return rows;
    }

    public String info(){
        StringBuilder sb = new StringBuilder();

        sb.append(("Type: Linked List")); sb.append("\n");
        sb.append(("Initialization date: " + initializationTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.MEDIUM)))); sb.append("\n");
        sb.append(("Number of Workers: " + this.database.size())); sb.append("\n");

        return sb.toString();
    }

    public void clear(){
        System.out.println(this.user.getLogin());
        for(int i =0; i<this.database.size();i++) {
            if (database.get(i).getUser().getLogin().equals(this.user.getLogin())) {
                this.database.remove(i);
                sortBySize();
            }

        }
    }

    public void save(){
        //making new name which is "database" + date of initialization
        String newFilename = "database_" + this.initializationTime.format(
                DateTimeFormatter.ISO_DATE_TIME
        ).replace(" ", "_").replace(":", "-");

        newFilename = newFilename.substring(0, newFilename.indexOf("."));
        newFilename = newFilename.concat(".xml");

        //saving
        FileParser.dataBasetoXML(FileParser.dataBaseToString(this.database), newFilename);
    }

    public String remove(String commandWithID) {
        //removing spaces and "remove" word to turn into long
        commandWithID = Terminal.removeString(commandWithID, "remove_by_id");
        if (commandWithID.isEmpty()) {
            return "Invalid id. Operation canceled";
        }
        int id = Integer.parseInt(commandWithID);

        //trying to find element
        int num = -1;
        if ((returnIndexById(id) != -1)) {
            for (int i = 0; i < database.size(); i++) {
                if (database.get(i).getId() == id) {
                    num = i;
                }
            }
            if (num != -1) {
                if (database.get(num).getUser().getLogin().equals(this.user.getLogin())) {
                    this.database.remove(returnIndexById(id));
                    sortBySize();
                    return "Worker was successfully deleted from the database";
                } else {
                    return "It's not yours";
                }
            }
            else {return "Element not found";}
        } else {
            return "Element not found";
        }
    }

    public String removeGreater(String commandWithSalary){
        //removing spaces and "update" word to turn into long
        commandWithSalary = Terminal.removeString(commandWithSalary, "remove_greater");
        double salary = Double.parseDouble(commandWithSalary);

        int toRemoveCounter = 0;
        long toRemoveID[] = new long[this.database.size()];
        for (int i = 0; i < this.database.size(); i++) {
            if ((this.database.get(i).getSalary() > salary)
                    && database.get(i).getUser().getLogin().equals(this.user.getLogin())){
                toRemoveID[toRemoveCounter] = this.database.get(i).getId();
                toRemoveCounter++;
            }
        }

        if (toRemoveCounter < 0){
            return "There was no such your workers";
        }

        for (int i = 0; i < toRemoveCounter; i++) {
            this.database.remove(returnIndexById(toRemoveID[i]));
        }

        sortBySize();
        return "Workers with salary greater " + salary + " were successfully removed!";
    }

    public String removeLower(String commandWithSalary){
        //removing spaces and "update" word to turn into long
        commandWithSalary = Terminal.removeString(commandWithSalary, "remove_lower");
        double salary = Double.parseDouble(commandWithSalary);

        int toRemoveCounter = 0;
        long toRemoveID[] = new long[this.database.size()];
        for (int i = 0; i < this.database.size(); i++) {
            if (this.database.get(i).getSalary() < salary&&database.get(i).getUser().getLogin().equals(this.user.getLogin())){
                if (database.get(i).getUser().getLogin().equals(this.user.getLogin())) {
                    toRemoveID[toRemoveCounter] = this.database.get(i).getId();
                    toRemoveCounter++;
                }
            }
        }

        for (int i = 0; i < toRemoveCounter; i++) {
            this.database.remove(returnIndexById(toRemoveID[i]));
        }
        sortBySize();
        return "Workers with salary lower " + salary + " were successfully removed!";
    }

    public String groupCountingByPosition(){
        StringBuilder out = new StringBuilder();
        StringBuilder sb = new StringBuilder();
        for (Position p: Position.values()) {
            out.append("-----------" + p.toString() + "-----------");out.append("\n");
            for (Worker worker : this.database) {
                if (worker.getPosition() != null) {
                    if (worker.getPosition().equals(p)){
                        sb.append(worker.getName()).append(" ").append(worker.getId());
                    }
                }
                out.append(sb.toString());out.append("\n");
                sb.delete(0, sb.length());
            }
        }
        return out.toString();
    }

    public String countLessThanStartDate(String commandWithStartDate){
        //removing spaces and "count_less_than_start_date" word to turn into date
        commandWithStartDate = Terminal.removeString(commandWithStartDate, "count_less_than_start_date");
        if (!commandWithStartDate.matches("\\s*(?!0000)(\\d{4})-(0[1-9]|1[0-2])-[0-3]\\d\\s*")){
            return "Invalid date!";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(commandWithStartDate, formatter);
        ZonedDateTime z = date.atStartOfDay(ZoneId.systemDefault());

        int counter = 0;
        for (Worker w:this.database) {
            if (w.getStartDate().isBefore(z)){
                counter++;
            }
        }
        return "There are " + counter + " workers with StartDate less than " + commandWithStartDate;
    }

    public String filterGreaterThanStartDate(String commandWithStartDate){
        //removing spaces and "count_less_than_start_date" word to turn into date
        commandWithStartDate = Terminal.removeString(commandWithStartDate, "filter_greater_than_start_date");
        if (!commandWithStartDate.matches("\\s*(?!0000)(\\d{4})-(0[1-9]|1[0-2])-[0-3]\\d\\s*")){
            return "Invalid date!";
        }

        StringBuilder out = new StringBuilder();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(commandWithStartDate, formatter);
        ZonedDateTime z = date.atStartOfDay(ZoneId.systemDefault());

        out.append("-----Workers with date after " + commandWithStartDate + "-----");out.append("\n");
        for (Worker w:this.database) {
            if (w.getStartDate().isAfter(z)){
                out.append(w.getName() + " " + w.getId());out.append("\n");
            }
        }
        return out.toString();
    }

    public String addIfMax(Worker newWorker){
        for (Worker w: this.database) {
            if (w.getSalary() > newWorker.getSalary()){
                return "New worker hasn't got the max salary!";
            }
        }

        //newWorker.setId();
        this.database.add(newWorker);
        sortBySize();
        return "Worker has been successfully added!";
    }

    public void sortBySize(){
        Collections.sort(this.database,new Comparator<Worker>() {
            @Override
            public int compare(Worker a, Worker b){
                return Double.compare(a.getSalary(), b.getSalary());
            }
        });
    }
}
