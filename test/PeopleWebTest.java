import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class PeopleWebTest {

    public Connection startConnection() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:test");
        PeopleWeb.createTables(conn);
        return conn;
    }

    @Test
    public void testPerson() throws SQLException {
        Connection conn = startConnection();
        PeopleWeb.insertPerson(conn, "Eugene", "Stone", "estone6@nature.com", "Colombia", "55.107.194.185");
        PeopleWeb.insertPerson(conn, "Nicholas", "Oliver", "noliver7@slate.com", "China", "159.45.204.128");

        Person eugene = PeopleWeb.selectPerson(conn, 1);
        Person nicholas = PeopleWeb.selectPerson(conn, 2);
        Person matt = PeopleWeb.selectPerson(conn, 3);

        assertTrue(eugene != null);
        assertTrue(nicholas != null);
        assertTrue(matt == null);

        assertTrue(eugene.ip.equals("55.107.194.185"));
    }

    @Test
    public void testPeople() throws SQLException {
        Connection conn = startConnection();
        PeopleWeb.insertPerson(conn, "Eugene", "Stone", "estone6@nature.com", "Colombia", "55.107.194.185");
        PeopleWeb.insertPerson(conn, "Nicholas", "Oliver", "noliver7@slate.com", "China", "159.45.204.128");

        ArrayList<Person> people = PeopleWeb.selectPeople(conn, 0);

        assertTrue(people.size() == 2);
        assertTrue(people.get(0).firstname.equals("Eugene"));
    }

}