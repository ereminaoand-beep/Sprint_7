package org.example.model;

public class Courier {
    private String login;
    private String password;
    private String firstName;

    // Пустой конструктор (нужен для некоторых JSON-библиотек)
    public Courier() {
    }

    // Конструктор со всеми полями
    public Courier(String login, String password, String firstName) {
        this.login = login;
        this.password = password;
        this.firstName = firstName;
    }

    // Геттеры
    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    // Сеттеры (если нужны – для полноты)
    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
}