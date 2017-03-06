import org.h2.tools.Server;
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
    static int amountOfPeople;

    public static void main(String[] args) throws SQLException, FileNotFoundException {
        Server.createWebServer().start();
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        createTables(conn);
        // populateDatabase(conn);
        amountOfPeople = getAmountOfPeople(conn);

        //<editor-fold desc="get '/'">
        Spark.get("/", (request, response) -> {
            int offset = getOffset(request.queryParams("offset"));

            ArrayList<Person> people = selectPeople(conn, offset);

            HashMap m = buildModel(people, offset);

            return new ModelAndView(m, "people.html");
        }, new MustacheTemplateEngine());
        //</editor-fold>

        //<editor-fold desc="get '/person'">
        Spark.get("/person", (request, response) -> {
            String idStr = request.queryParams("id");
            int id;
            Person person = null;
            if (idStr != null && !idStr.isEmpty()) {
                id = Integer.parseInt(idStr);
                person = selectPerson(conn, id);
            }
            if (person == null){
                response.redirect("/");
                return new ModelAndView(person, "people.html");
            }

            return new ModelAndView(person, "person.html");
        }, new MustacheTemplateEngine());
        //</editor-fold>
    }

    private static HashMap buildModel(ArrayList<Person> people, int offset) {
        HashMap m = new HashMap();
        m.put("people", people);

        int next = offset + 20;
        int prev = offset - 20;

        if (next < amountOfPeople) {
            m.put("nextOffset", next);
        }
        if (prev >= 0) {
            m.put("prevOffset", prev);
        }

        return m;
    }

    private static int getAmountOfPeople(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet results = stmt.executeQuery( "SELECT COUNT(*) FROM people" );

        if (results.next()) {
            return results.getInt(1);
        }
        return 0;
    }

    private static int getOffset(String offset) {
        int o = (offset != null)
                ? Integer.parseInt(offset)
                : 0;
        if (o < amountOfPeople && o >= 0) return o;
        else return 0;
    }

    private static void populateDatabase(Connection conn) throws FileNotFoundException, SQLException {
        File f = new File("people.csv");
        Scanner s = new Scanner(f);
        String[] personArr;
        while (s.hasNext()) {
            personArr = s.nextLine().split(",");
            if (personArr[0].equals("id")) continue;
            insertPerson(conn, personArr[1], personArr[2], personArr[3], personArr[4], personArr[5]);
        }
        s.close();
    }

    public static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute(
                "CREATE TABLE IF NOT EXISTS people " +
                "(id IDENTITY , firstname VARCHAR , lastname VARCHAR , email VARCHAR , country VARCHAR , ip VARCHAR )"
        );
    }

    public static void insertPerson(Connection conn, String firstname, String lastname, String email, String country, String ip) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO people VALUES (NULL, ?, ?, ?, ?, ?)"
        );
        stmt.setString(1, firstname);
        stmt.setString(2, lastname);
        stmt.setString(3, email);
        stmt.setString(4, country);
        stmt.setString(5, ip);
        stmt.execute();
    }

    public static Person selectPerson(Connection conn, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM people WHERE id = ?"
        );
        stmt.setInt(1, id);
        ResultSet results = stmt.executeQuery();

        if (results.next()) {
            String firstname = results.getString("firstname");
            String lastname = results.getString("lastname");
            String email = results.getString("email");
            String country = results.getString("country");
            String ip = results.getString("ip");
            return new Person(id, firstname, lastname, email, country, ip);
        }
        return null;
    }

    public static ArrayList<Person> selectPeople(Connection conn, int offset) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM people " +
                "LIMIT 20 OFFSET ?"
        );
        stmt.setInt(1, offset);
        ResultSet results = stmt.executeQuery();

        ArrayList<Person> people = new ArrayList<>();
        while(results.next()) {
            int id = results.getInt("id");
            String firstname = results.getString("firstname");
            String lastname = results.getString("lastname");
            String email = results.getString("email");
            String country = results.getString("country");
            String ip = results.getString("ip");
            people.add(new Person(id, firstname, lastname, email, country, ip));
        }
        return people;
    }
}
