import org.junit.Test;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by lee on 10/3/16.
 */
public class MainTest {

    public Connection startConnection() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:test");
        PeopleWeb.createTables(conn);
        return conn;
    }

    @Test
    public void testPerson() throws SQLException {
        Connection conn = startConnection();
        PeopleWeb.createTables(conn);
        PeopleWeb.insertPerson(conn, "Lee", "Flatt", "email", "USA", "1.442.5125.63");

        Person me = PeopleWeb.selectPerson(conn, 1);

        assertTrue(me.getFirstName().equalsIgnoreCase("Lee"));
        assertTrue(me.getIpAddress().equalsIgnoreCase("1.442.5125.63"));
        conn.close();
    }

    @Test
    public void testSelectPeople() throws FileNotFoundException, SQLException {
        Connection conn = startConnection();
        PeopleWeb.createTables(conn);
        PeopleWeb.populateDatabase(conn);

        ArrayList<Person> people = PeopleWeb.selectPeople(conn, 0);

        assertTrue(people.size() == 20);

        assertTrue(people.get(0).getFirstName().equalsIgnoreCase("Martha"));
    }

}
