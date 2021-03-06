package com.company.main;

import com.company.Login.User;
import com.company.database.DataBase;
import com.company.database.PostgresqlDatabase;
import com.company.database.PostgresqlParser;
import com.company.exceptions.NotConnectedException;
import com.company.network.Server;
import com.company.network.ServerThread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//variant 331122

public class Main {
    static ExecutorService executeIt = Executors.newFixedThreadPool(2);

    public static void main(String[] args) {
        DataBase dataBase = new DataBase();
        PostgresqlDatabase dataBase1 = new PostgresqlDatabase();
        dataBase1.initialize();
        dataBase.initialize();

        try {
            dataBase.setDatabase(PostgresqlParser.stringToDatabase());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // стартуем сервер на порту 3345 и инициализируем переменную для обработки консольных команд с самого сервера
        try (ServerSocket server = new ServerSocket(1488);) {
            System.out.println("Server socket created");

            // стартуем цикл при условии что серверный сокет не закрыт
            while (!server.isClosed()) {
                // если комманд от сервера нет то становимся в ожидание
                // подключения к сокету общения под именем - "clientDialog" на
                // серверной стороне
                Socket client = server.accept();
                // после получения запроса на подключение сервер создаёт сокет
                // для общения с клиентом и отправляет его в отдельную нить
                // в Runnable(при необходимости можно создать Callable)
                // монопоточную нить = сервер - MonoThreadClientHandler и тот
                // продолжает общение от лица сервера
                executeIt.execute(new ServerThread(client,dataBase));
                System.out.print("Connection accepted.");
            }

            // закрытие пула нитей после завершения работы всех нитей
            executeIt.shutdown();
        } catch (IOException e) {
            System.out.println("check2");
            e.printStackTrace();
        }
    }
}

