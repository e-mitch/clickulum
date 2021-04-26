package sample;
import java.sql.*;

public class SqliteConnection {
    public static Connection Connector(){
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn =DriverManager.getConnection("jdbc:sqlite:/Users/EllieMitchell/clickulumTest/alabama.sl3");
            return conn;
        } catch (Exception e){
            return null;
        }
    }
}
