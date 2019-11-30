package server.auth;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class BaseAuthService implements AuthService {

    private final DataBaseConnector dbc = new DataBaseConnector();

    @Override
    public String getNickByLoginPass(String login, String pass) throws SQLException, ClassNotFoundException {
        return dbc.getNickByLoginPass(login, pass);
    }

    @Override
    public void changeNick(String login, String newNick) throws SQLException, ClassNotFoundException {
        dbc.changeNick(login, newNick);
    }
}
