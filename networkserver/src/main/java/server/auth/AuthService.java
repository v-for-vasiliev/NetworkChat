package server.auth;


import java.sql.SQLException;

public interface AuthService {


    /**
     *
     * @param login
     * @param pass
     * @return nick or null
     */

    String getNickByLoginPass(String login, String pass) throws SQLException, ClassNotFoundException;

    void changeNick(String login, String newNick) throws SQLException, ClassNotFoundException;

}
