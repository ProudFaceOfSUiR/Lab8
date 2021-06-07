package com.company.network;

import com.company.Login.User;
import com.company.PostgreSQL.Check;
import com.company.classes.Worker;
import com.company.database.DataBase;
import com.company.database.PostgresqlParser;
import com.company.enums.Commands;
import com.company.exceptions.NotConnectedException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class Server {
    DataBase dataBase;

    private ServerSocket server;
    private Socket client;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Messages input;
    private Messages output;
    private String response;
    private User user;
    private User loggedUser;

    public ServerSocket getServer() {
        return server;
    }

    public Socket getClient() {
        return client;
    }

    public void setClient(Socket client) {
        this.client = client;
    }

    public void setUser(User user){
        this.user = user;
    }

    public boolean initialize(DataBase dataBase){
        this.dataBase = dataBase;
        this.output = new Messages();
        return true;
    }

    public boolean connectSocket(){
        //connecting socket
        //getting streams
        try {
            this.out = new ObjectOutputStream(client.getOutputStream());
            this.in = new ObjectInputStream(client.getInputStream());
        } catch (IOException e) {
            System.out.println("Error while getting streams: " + e.getMessage());
            return false;
        }

        return true;
    }

    protected void sendFeedback(){
        this.output.addObject(response);
        try {
            System.out.println("output: " + this.output.getObject(0));
            this.out.writeObject(this.output);
            this.out.flush();
            this.out.reset();
        } catch (Exception e) {
            System.out.println("Error while sending response: " + e.getMessage());
        }
        this.output.clear();
    }

    public User readCommand() throws NotConnectedException {
        //getting message
        try {
            this.input = (Messages) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error while reading from client: " + e.getMessage());
            throw new NotConnectedException();
        }

        User sendler = (User) input.getObject(0);

        //getting command from messages
        Commands command;
        try {
            command = (Commands) input.getObject(1);
        } catch (IndexOutOfBoundsException e){
            System.out.println("Empty input error");
            return null;
        }
        System.out.println("Command is " + command.toString());

        switch (command) {
            case SIGN_IN:
                if(Check.check()){
                    System.out.println("Database was initialised, all servers working nominally");
                    Check.create();
                    //User user = new User();
                    System.out.println(sendler.getLogin());
                    sendler.setPassword(User.encryptThisString(sendler.getPassword()));

                    if (sendler.isValid() && Check.checkForMatch(sendler.getLogin(), sendler.getPassword())){

                        //System.out.println(Check.checkForMatch(sendler.getLogin(), sendler.getPassword()));

                        this.output.addObject(Commands.SIGN_IN);
                        this.output.addObject(Check.signIn(sendler));
                        this.setUser(sendler);
                        this.loggedUser = new User(sendler);
                        System.out.println(this.user.getLogin());
                    } else {
                        this.output.addObject(Commands.SIGN_IN);
                        this.output.addObject(false);
                        this.output.addObject("Login or password is not valid");
                    }
                }
                break;
            case SIGN_UP:
                System.out.println("sign_up");


                Check.create();

                sendler.setPassword(User.encryptThisString(sendler.getPassword()));

                if(!Check.checkForMatch(sendler.getLogin(), sendler.getPassword())){
                    Check.signUp(sendler);

                    this.output.addObject(Commands.SIGN_UP);
                    this.output.addObject(true);

                    this.setUser(sendler);
                    this.loggedUser = new User(sendler);

                    System.out.println(this.user.getLogin());
                } else {
                    this.output.addObject(Commands.SIGN_UP);
                    this.output.addObject(false);
                    this.output.addObject("Login or password is not valid");
                }
                break;
            case ADD:
                this.output.addObject(Commands.NO_FEEDBACK);
                this.response = this.dataBase.add((Worker) input.getObject(2));
                break;
            case UPDATE:
                System.out.println("THIS IS UPDATE ");
                this.output.addObject(Commands.NO_FEEDBACK);
                long id = (long) input.getObject(2);
                //check if worker exists
                int num = -1;
                for (int i = 0; i< this.dataBase.database.size();i++){
                    if (this.dataBase.database.get(i).getId()==id){
                        num = i;
                    }
                }
                try {
                    if (num != -1) {
                        //sedning message if not ours
                        if (!dataBase.getWorkerByIndex(num)
                                .getUser().getLogin().equals(this.user.getLogin())){
                            System.out.println("UPDATE");
                            System.out.println(dataBase.getWorkerByIndex(num)
                                    .getUser().getLogin());
                            System.out.println(this.user.getLogin());
                            this.response = "It's not your worker.";
                            break;
                        }
                        System.out.println("WE ARE HERE");
                        //sending the worker
                        Messages workerToUpdate = new Messages();
                        workerToUpdate.addObject(Commands.NO_FEEDBACK);
                        workerToUpdate.addObject(dataBase.getWorkerByIndex(num));
                        String log = dataBase.getWorkerByIndex(num).getUser().getLogin();
                        System.out.println(log);
                        try {
                            this.out.writeObject(workerToUpdate);
                            this.out.flush();
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                            return sendler;
                        }
                        //getting worker
                        try {
                            this.input = (Messages) this.in.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            System.out.println(e.getMessage());
                        }

                        System.out.println("---------");
                        System.out.println("check");
                        System.out.println(this.user.getLogin());
                        System.out.println(this.dataBase.getWorkerByIndex(num).getUser().getLogin());
                        System.out.println(this.dataBase.getWorkerByIndex(num).getName());
                        System.out.println("---------");

                        if (this.dataBase.getWorkerByIndex(num).getUser().getLogin().equals(this.user.getLogin())) {
                            System.out.println("UPDATING");
                            this.dataBase.remove(String.valueOf(id));
                            this.dataBase.add((Worker) this.input.getObject(0));
                            this.response = "Worker has been successfully updated (server)";
                        } else {
                            System.out.println("It's not yours");
                        }
                    } else {
                        this.response = "invalid id";
                    }
                } catch (NullPointerException e){
                    this.response = "invalid id (catch)";
                }
                break;
            case REMOVE_BY_ID:
                this.output.addObject(Commands.NO_FEEDBACK);
                this.response = this.dataBase.remove((String)input.getObject(2));
                break;
            case CLEAR:
                this.output.addObject(Commands.NO_FEEDBACK);
                this.dataBase.clear();
                this.response = "Database was successfully cleared";
                break;
            case EXIT:
                System.out.println(this.user.getLogin());
                String s = PostgresqlParser.dataBaseToString(this.dataBase.getDatabase(),this.loggedUser.getLogin());
                System.out.println(s);
                Check.save(s, this.loggedUser.getLogin());
                break;
            case ADD_IF_MAX:
                this.output.addObject(Commands.NO_FEEDBACK);
                this.response = this.dataBase.addIfMax((Worker) input.getObject(2));
                break;
            case REMOVE_GREATER:
                this.output.addObject(Commands.NO_FEEDBACK);
                this.response = this.dataBase.removeGreater((String) input.getObject(2));
                break;
            case REMOVE_LOWER:
                this.output.addObject(Commands.NO_FEEDBACK);
                this.response = this.dataBase.removeLower((String) input.getObject(2));
                break;
            case GROUP_COUNTING_BY_POSITION:
                this.output.addObject(Commands.NO_FEEDBACK);
                this.response = this.dataBase.groupCountingByPosition();
                break;
            case COUNT_LESS_THAN_START_DATE:
                this.output.addObject(Commands.NO_FEEDBACK);
                this.response = this.dataBase.countLessThanStartDate((String) input.getObject(2));
                break;
            case FILTER_GREATER_THAN_START_DATE:
                this.output.addObject(Commands.NO_FEEDBACK);
                this.response = this.dataBase.filterGreaterThanStartDate((String) input.getObject(2));
                break;
            case FILL_FROM_FILE:
                this.output.addObject(Commands.NO_FEEDBACK);
                this.dataBase.setDatabase((LinkedList<Worker>) input.getObject(2));
                this.response = "Server database has been successfully replaced by client's";
                break;
            case INFO:
                this.output.addObject(Commands.NO_FEEDBACK);
                this.response = dataBase.info();
                break;
            case SHOW:
                this.output.addObject(Commands.NO_FEEDBACK);
                this.response = "Database";
                this.output.addObject(dataBase.show());
                break;
            case HELP:
                this.output.addObject(Commands.NO_FEEDBACK);
                this.response = dataBase.help();
                break;
            case GET_DATABASE:
                this.output.addObject(Commands.NO_FEEDBACK);
                this.response = "Database";
                this.output.addObject(this.dataBase.database);
                break;
            default:
                this.output.addObject(Commands.NO_FEEDBACK);
                this.response = ("Unexpected value: " + command);
                break;
        }

        sendFeedback();
        return sendler;
    }
}
