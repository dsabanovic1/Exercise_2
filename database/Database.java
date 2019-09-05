package database;
import org.apache.commons.lang3.RandomStringUtils;

import java.sql.*;
import java.util.Properties;

public class Database {

    private Connection con;
    private Statement st;
    private ResultSet rs;

    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("io.crate.client.jdbc.CrateDriver");
        Properties properties = new Properties();
        properties.put("user","crate");

        Connection con =DriverManager.getConnection("crate://127.0.0.1:5432/",properties);
        con.setSchema("excercise");
        return con;
    }

    public static PreparedStatement prepareStatement(String SQLquery) throws SQLException, ClassNotFoundException {
        Connection connection = getConnection();

        return connection.prepareStatement(SQLquery);
    }

    public static String generatorId(int length)
    {
        return RandomStringUtils.randomNumeric(5);
    }
}

