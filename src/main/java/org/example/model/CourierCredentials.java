package org.example.model;

public class CourierCredentials {
    private String login;
    private String password;

    public CourierCredentials() {
    }

    public CourierCredentials(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    // сеттеры по желанию
    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}