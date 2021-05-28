package com.company.classes;

import com.company.Login.User;
import com.company.database.Terminal;
import com.company.enums.Position;
import com.company.exceptions.InvalidDataException;
import com.company.exceptions.OperationCanceledException;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Random;

public class Worker implements Serializable {

    private static final long serialVersionUID = 66L;

    private long id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private java.time.ZonedDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private double salary; //Значение поля должно быть больше 0
    private java.time.ZonedDateTime startDate; //Поле не может быть null
    private java.time.ZonedDateTime endDate; //Поле может быть null
    private Position position; //Поле может быть null
    private Person person; //Поле может быть null
    private User user;
    private String login;

    public void setLogin(String login){
        this.login = login;
    }

    public String getLogin(){
        return this.login;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public Worker(long ID, String name, double salary, Position position, Person person, Coordinates coordinates,
                  ZonedDateTime startDate, ZonedDateTime endDate, User user) throws InvalidDataException{
        setName(name);
        setSalary(salary);

        this.coordinates = coordinates;
        this.startDate = startDate;
        this.endDate = endDate;
        this.position = position;
        this.person = person;

        this.creationDate = ZonedDateTime.now();

        this.user = user;

        //Random random = new Random();
        this.id = ID;//Math.abs(Long.parseLong(String.valueOf(random.nextLong() + creationDate.getSecond() + creationDate.getMinute() + creationDate.getHour()).substring(0,10)));

    }


    //private constructor for WorkerBuilder
    public Worker(){
        //automatically generated values
        this.creationDate = ZonedDateTime.now();
        /*Random random = new Random();
        this.id = Math.abs(Long.parseLong(String.valueOf(
                random.nextLong() + creationDate.getSecond() + creationDate.getMinute() + creationDate.getHour())
                .substring(0,10)));*/
    }
/*
    public void setId(long id){
        //Random random = new Random();
        this.id = id;/*Math.abs(Long.parseLong(String.valueOf(
                random.nextLong() + creationDate.getSecond() + creationDate.getMinute() + creationDate.getHour())
                .substring(0,10)));*/


    //getters and setters

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws InvalidDataException{
        if (name == null || name.equals("")){
            throw new InvalidDataException("name", "It can't be empty");
        } else {
            this.name = name;
        }
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) throws InvalidDataException{
        if (salary <= 0){
            throw new InvalidDataException("salary","It must be a positive number");
        } else {
            this.salary = salary;
        }
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public ZonedDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
