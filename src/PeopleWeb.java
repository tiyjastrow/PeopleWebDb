import org.h2.tools.Server;
import spark.ModelAndView;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Scanner;

public class PeopleWeb {

    public static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("DROP TABLE IF EXISTS people");
        stmt.execute("CREATE TABLE people (id IDENTITY, first_name VARCHAR, last_name VARCHAR, email VARCHAR, country VARCHAR, ip VARCHAR)");
    }

    public static void insertPerson(Connection conn, String firstName, String lastName, String email, String country, String ip) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO people VALUES (null, ?, ?, ?, ?, ?)");
        stmt.setString(1, firstName);
        stmt.setString(2, lastName);
        stmt.setString(3, email);
        stmt.setString(4, country);
        stmt.setString(5, ip);
        stmt.execute();
    }

    public static Person selectPerson(Connection conn, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM people WHERE id = ?");
        stmt.setInt(1, id);
        Person person = new Person();
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            String firstName = rs.getString("first_name");
            String lastName = rs.getString("last_name");
            String email = rs.getString("email");
            String country = rs.getString("country");
            String ip = rs.getString("ip");
            person = new Person(id, firstName, lastName, email, country, ip);
        }
        return person;
    }

    public static ArrayList<Person> selectPeople(Connection conn, int offset) throws SQLException {
        ArrayList<Person> people = new ArrayList<>();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM people LIMIT 20 OFFSET ?");
        stmt.setInt(1, offset);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            int id = rs.getInt("id");
            String firstName = rs.getString("first_name");
            String lastName = rs.getString("last_name");
            String email = rs.getString("email");
            String country = rs.getString("country");
            String ip = rs.getString("ip");
            people.add(new Person(id, firstName, lastName, email, country, ip));
        }

        return people;
    }

    public static void populateDatabase(Connection conn) throws FileNotFoundException, SQLException {
        File f = new File("people.csv");
        Scanner fileScanner = new Scanner(f);

        while (fileScanner.hasNext()) {
            String line = fileScanner.nextLine();
            String[] column = line.split(",");
            if (!column[0].equalsIgnoreCase("id"))
            insertPerson(conn, column[1], column[2], column[3], column[4], column[5]);
        }
    }

    public static void main(String[] args) throws FileNotFoundException, SQLException {
        Server.createWebServer().start();
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        createTables(conn);
        populateDatabase(conn);

        Spark.init();

        Spark.get(
                "/",
                ((request, response) -> {
                    String offset = request.queryParams("offset");
                    int offsetNum = 0;

                    Integer next1 = null;
                    Integer next2 = null;
                    Integer prev1 = null;
                    Integer prev2 = null;

                    Integer last = null;

                    if (offset != null) {
                        offsetNum = Integer.parseInt(offset);
                    }

                    ArrayList<Person> people = selectPeople(conn, offsetNum);

                    Integer prev = null;
                    if (offsetNum >= 20) {
                        prev = (offsetNum - 20);
                        prev1 = prev/20 + 1;
                    }
                    Integer next = null;
                    if (offsetNum < 980) {
                        next = (offsetNum + 20);
                        next1 = next/20 + 1;
                        last = 980;
                    }

                    Integer muchLess = null;
                    if (prev != null && prev >= 20) {
                        muchLess = (prev - 20);
                        prev2 = muchLess/20 + 1;
                    }
                    Integer muchMore = null;
                    if (next != null && next < 980) {
                        muchMore = (next + 20);
                        next2 = muchMore/20 + 1;
                    }

                    int current = offsetNum/20 + 1;

                    HashMap m = new HashMap();
                    m.put("people", people);
                    m.put("next1", next1);
                    m.put("prev1", prev1);
                    m.put("next2", next2);
                    m.put("prev2", prev2);
                    m.put("muchMore", muchMore);
                    m.put("muchLess", muchLess);
                    m.put("current", current);
                    m.put("prev", prev);
                    m.put("next", next);
                    m.put("last", last);
                    return new ModelAndView(m, "people.html");
                }),
                new MustacheTemplateEngine()
        );

        Spark.get(
                "/person",
                ((request, response) -> {
                    int id = Integer.parseInt(request.queryParams("id"));
                    Person person = selectPerson(conn, id);
                    HashMap m = new HashMap();

                    m.put("person", person);
                    return new ModelAndView(m, "person.html");
                }),
                new MustacheTemplateEngine()
        );
    }
}
