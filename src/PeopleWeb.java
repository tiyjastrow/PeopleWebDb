import spark.ModelAndView;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


public class PeopleWeb {
    private static int offset = 0;
    private static int counter = 0;

    public static void main(String[] args) throws FileNotFoundException, SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        createTable(conn);
        populateDataBase(conn);

        Spark.init();
        Spark.get("/person", ((request, response) -> {
                    int id = Integer.parseInt(request.queryParams("id"));
                    HashMap m = new HashMap();
                    Person person = selectPerson(conn, id);
                    m.put("person", person);
                    return new ModelAndView(m, "personal.html");

                }),
                new MustacheTemplateEngine()
        );
        Spark.get("/", ((request, response) -> {
                    String previous = "Previous";
                    String next = "Next";

                    HashMap m = new HashMap();
                    String offset1 = request.queryParams("offset");
                    if (offset1 == null) {
                        offset1 = "0";
                    }
                    counter = counter + offset;
                    offset = offset + Integer.parseInt(offset1);

                    ArrayList twenty = selectPeople(conn, offset);

                    m.put("previous", previous);

                    m.put("next", next);

                    m.put("people", twenty);
                    return new ModelAndView(m, "people.html");
                }),
                new MustacheTemplateEngine()
        );
    }

    private static void populateDataBase(Connection conn) throws FileNotFoundException, SQLException {

        File f = new File("people.csv");
        Scanner fileScanner = new Scanner(f);
        fileScanner.nextLine();
        while (fileScanner.hasNext()) {
            String line = fileScanner.nextLine();
            String[] columns = line.split(",");
            insertPerson(conn, columns[1], columns[2], columns[3], columns[4], columns[5]);


        }

    }

    public static void createTable(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS people (id IDENTITY, first_name VARCHAR, last_name VARCHAR, email VARCHAR, country VARCHAR, ip VARCHAR)");

    }

    public static void insertPerson(Connection conn, String firstName, String lastName, String email, String country, String ip) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO people VALUES (NULL ,?,?,?,?,?)");
        stmt.setString(1, firstName);
        stmt.setString(2, lastName);
        stmt.setString(3, email);
        stmt.setString(4, country);
        stmt.setString(5, ip);
        stmt.execute();
    }

    public static Person selectPerson(Connection conn, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM people WHERE id= ?");
        stmt.setInt(1, id);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            String firstName = results.getString("first_name");
            String lastName = results.getString("last_name");
            String email = results.getString("email");
            String country = results.getString("country");
            String ip = results.getString("ip");
            return new Person(id, firstName, lastName, email, country, ip);
        }
        stmt.execute();
        return null;

    }

    public static ArrayList<Person> selectPeople(Connection conn, int offset) throws SQLException {
        ArrayList<Person> people = new ArrayList<>();
        PreparedStatement stmt = conn.prepareStatement("SELECT  id, first_name, last_name, email, country, ip FROM people" + " LIMIT 20 OFFSET ? ");
        stmt.setInt(1, offset);
        ResultSet results = stmt.executeQuery();
        while (results.next()) {
            int id = results.getInt("id");
            String firstName = results.getString("first_name");
            String lastName = results.getString("last_name");
            String email = results.getString("email");
            String country = results.getString("country");
            String ip = results.getString("ip");
            people.add(new Person(id, firstName, lastName, email, country, ip));
        }
        return people;
    }


}

