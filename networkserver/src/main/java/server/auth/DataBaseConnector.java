package server.auth;

import java.sql.*;

public class DataBaseConnector {

    private Connection connection;
    private Statement statement;

    public void connect() throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:users.db");
        statement = connection.createStatement();
    }

    public void disconnect() throws SQLException {
        connection.close();
    }

    public void addUser(String newNick, String newLogin, String newPass) throws SQLException, ClassNotFoundException {
        connect();
        statement.executeUpdate("INSERT INTO users (nick, login, pass)\n" +
                "  VALUES ('" + newNick + "', '" + newLogin + "', '" + newPass + "');");
        disconnect();
    }

    public String getNickByLoginPass(String login, String pass) throws SQLException, ClassNotFoundException {
        connect();
        ResultSet rs = statement.executeQuery("SELECT * FROM users WHERE login = '" + login + "' AND pass = '" + pass + "';");
        String nick;

        if (!rs.next()) {
            nick = "Nick not found";
        } else {
            System.out.println(rs.getString("nick"));
            nick = rs.getString("nick");
        }

        disconnect();
        return nick;
    }

    public void changeNick(String login, String newNick) throws SQLException, ClassNotFoundException {
        connect();
        statement.executeUpdate("UPDATE users SET nick = '" + newNick +
                "'  WHERE login = '" + login + "';");
        disconnect();
    }

    public static void main(String[] args) {
        DataBaseConnector dbc = new DataBaseConnector();
        try {

            dbc.changeNick("login1", "Kolyan");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
