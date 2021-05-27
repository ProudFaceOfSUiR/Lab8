package com.company.PostgreSQL;

import com.company.Login.User;
//import com.company.exceptions.OperationCanceledException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Check {
    public static boolean check() {
        Connection c = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/",
                    "postgres", "12345678");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            //System.err.println(e.getClass().getName()+": "+e.getMessage());
            //System.exit(0);
            return false;
        }
        //System.out.println("Opened database successfully");

    }

    public static void create() {
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/postgres",
                            "postgres", "12345678");
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            String sql = new StringBuilder()
                    .append("CREATE TABLE USER_BASE ")
                    .append("(ID int GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY     ,")
                    .append(" LOGIN           TEXT    NOT NULL, ")
                    .append(" PASSWORD            TEXT     NOT NULL) ")
                    .toString();
            stmt.executeUpdate(sql);
            stmt.close();
            c.close();
            System.out.println("I created db");
        } catch (Exception e) {
            //System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            //System.exit(0);
            System.out.println("Table already exists, all right");
        }
        System.out.println("Table created successfully");
    }

    public static void save(String s,String login){
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/postgres",
                            "postgres", "12345678");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");
            stmt = c.createStatement();
            stmt.executeUpdate("DELETE FROM DATABASE WHERE LOGIN='"+login+"'");
            stmt.close();
            c.commit();
            stmt = c.createStatement();
            stmt.executeUpdate(s);
            stmt.close();
            c.commit();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            //System.exit(0);
        }
    }

    public static void signUp(User user) {
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/postgres",
                            "postgres", "12345678");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();

            String sql = new StringBuilder().append("INSERT INTO USER_BASE (LOGIN,PASSWORD) ")
                    .append("VALUES ('")
                    .append(user.getLogin()).append("','")
                    .append(user.getPassword()).append("');").toString();
            stmt.executeUpdate(sql);
            System.out.println("SUCKsess");
            stmt.close();
            c.commit();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            //System.exit(0);
        }
        System.out.println("Records created successfully");
    }

    public static void delete(int id) {
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/postgres",
                            "postgres", "12345678");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();

            String sql = "DELETE Products WHERE ID=" + String.valueOf(id) + "";
            stmt.executeUpdate(sql);

            stmt.close();
            c.commit();
            c.close();
        } catch (Exception e) {
            System.out.println("sorry");
        }
    }

    public static boolean signIn(User user){
        if (checkForMatch(user.getLogin(), user.getPassword())){
            return true;
        } else return false;
    }

    public static boolean checkForMatch(String login, String password) {
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/postgres",
                            "postgres", "12345678");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM USER_BASE;");
            boolean match = false;
            while (rs.next()) {
                int id = rs.getInt("id");
                String loginDB = rs.getString("login");
                String passwordDB = rs.getString("password");
                if(password.equals(passwordDB)&&login.equals(loginDB)){
                    System.out.println("match");
                    match = true;
                }
                System.out.println("ID = " + id);
                System.out.println("NAME = " + loginDB);
                System.out.println("AGE = " + passwordDB);
                System.out.println();
            }
            rs.close();
            stmt.close();
            c.close();
            System.out.println(match);
            return match;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            //System.exit(0);
            return false;
        }

        //System.out.println("Operation done successfully");
    }
}
