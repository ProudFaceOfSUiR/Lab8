package com.company.database;

import com.company.Login.User;
import com.company.classes.Coordinates;
import com.company.classes.Person;
import com.company.classes.Worker;
import com.company.enums.Position;
import com.company.exceptions.OperationCanceledException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.jws.soap.SOAPBinding;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;

public class PostgresqlParser {

    /**
     * Checks if file already exists
     * @param filePath
     * @return
     */
    public static boolean alreadyExistCheck(String filePath) {
        File f = new File(filePath);
        return f.exists() && !f.isDirectory();
    }

    /**
     * Makes a big string with the whole database
     * @param database
     * @return
     */
    public static String dataBaseToString(LinkedList<Worker> database, String user) {
        StringBuilder sb = new StringBuilder();

        //writing preamble

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        //writing workers
        String sql;
        for (Worker w : database) {
            System.out.println("trying to login");
            System.out.println(w.getUser().getLogin());
            System.out.println(user);
            System.out.println();
            if (w.getUser().getLogin().equals(user)) {
                //sb.append("INSERT INTO DATABASE (NAME,SALARY,COORDINATES,STARTDAY,ENDDAY)");
                sb.append("INSERT INTO DATABASE (NAME,SALARY,POSITION,COORDINATES,PERSON,STARTDATE,ENDDATE,LOGIN) ")
                        .append("VALUES ('")
                        .append(w.getName()).append("',")
                        .append(w.getSalary()).append(",'")
                        .append(w.getPosition().toString()).append("','")
                        .append(w.getCoordinates().getX()).append(",").append(w.getCoordinates().getY()).append("','")
                        .append(w.getPerson().getHeight()).append(",").append(w.getPerson().getWeight()).append("','")
                        .append(w.getStartDate().format(formatter)).append("','")
                        .append(w.getStartDate().format(formatter)).append("','")
                        .append(user)
                        //.append(w.getPosition().toString()).append(",")
                        .append("');");

            }
        }
        return sb.toString();
    }

    public static LinkedList<Worker> stringToDatabase(){
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/postgres",
                            "postgres", "12345678");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            //fields of a worker
            String name;
            double salary;

            //position
            String positionString;
            Position position;

            //personality
            Person person = null;

            //coordinates
            Coordinates coordinates;

            //dates
            ZonedDateTime startdate;
            ZonedDateTime endDate = null;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            //counter of successfully added workers
            int successfullyAddedWorkers = 0;

            //User user = new User();

            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM DATABASE;" );
            LinkedList<Worker> collection = new LinkedList<>();
            Worker worker = new Worker();
            long ID;
            while ( rs.next() ) {
                User user = new User();
                ID = (rs.getLong    ("id"));
                name = rs.getString("name");
                salary = rs.getDouble("salary");
                position = Position.findEnum(rs.getString("position"));
                String[] heightWeight = rs.getString("person").split(",");
                person = new Person(Long.valueOf(Terminal.removeSpaces(heightWeight[0])), Integer.valueOf(Terminal.removeSpaces(heightWeight[1])));
                String[] xy = rs.getString("coordinates").split(",");
                coordinates = new Coordinates(Long.parseLong(Terminal.removeSpaces(xy[0])), Integer.valueOf(Terminal.removeSpaces(xy[1])));
                LocalDate date = LocalDate.parse(
                        Terminal.removeSpaces(
                                rs.getString("startdate")),
                        formatter);
                startdate = date.atStartOfDay(ZoneId.systemDefault());
                LocalDate date1 = LocalDate.parse(
                        Terminal.removeSpaces(
                                rs.getString("enddate")),
                        formatter);
                endDate = date.atStartOfDay(ZoneId.systemDefault());

                user.setLogin(Terminal.removeSpaces(rs.getString("login")));
                user.setPassword("");
                collection.add(new Worker(ID,name, salary, position, person, coordinates, startdate, endDate,user));
                successfullyAddedWorkers++;
            }
            rs.close();
            stmt.close();
            c.close();
            return collection;
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            //System.exit(0);
            return null;
        }
    }
}
