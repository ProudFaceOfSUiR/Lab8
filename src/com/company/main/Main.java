package com.company.main;

import com.company.Login.User;
import com.company.database.DataBase;
import com.company.database.PostgresqlDatabase;
import com.company.database.PostgresqlParser;
import com.company.exceptions.NotConnectedException;
import com.company.network.Server;

//variant 331122

public class Main {

    public static void main(String[] args) {

        DataBase dataBase = new DataBase();
        dataBase.initialize();
        PostgresqlDatabase postgresqlDatabase = new PostgresqlDatabase();
        postgresqlDatabase.initialize();
        //dataBase.initialize();

        dataBase.setDatabase(PostgresqlParser.stringToDatabase());


        dataBase.show();

        Server server = new Server();
        boolean isInitialized = false;
        while (!isInitialized){
            isInitialized = server.initialize(dataBase);
            if (!isInitialized) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        boolean isConnected = server.connectSocket();
        User user;
        while (true){
            //connecting socket
            if (!isConnected){
                System.out.println("Reconnecting...");
                isConnected = server.connectSocket();
                continue;
            }
            //reading commands from socket
            try {
                user = server.readCommand();
                server.setUser(user);
                dataBase.setUser(user);
            } catch (NotConnectedException e) {
                System.out.println(e.getMessage());
                isConnected = false;
            }
        }
    }
}
