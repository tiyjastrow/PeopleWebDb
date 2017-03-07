import spark.ModelAndView;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Main {

    static ArrayList<Person> people = new ArrayList<>();

    public static void main(String[] args) throws FileNotFoundException {

        File f = new File("people.csv");
        Scanner scanner = new Scanner(f);

        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            String[] columns = line.split(",");
            Person person = new Person(columns[0], columns[1], columns[2], columns[3], columns[4], columns[5]);
            people.add(person);  //loads the text file (countries) into the array list
        }

        Spark.init();

        Spark.get("/", (request, response) -> {
                    HashMap m = new HashMap();
                    String offset = request.queryParams("offset");
                    int offsetNumber = 0;
                    if (offset != null) {
                    offsetNumber = Integer.parseInt(offset);
                    }
                    List<Person> p = people.subList(offsetNumber, offsetNumber+20);
                    m.put("p", p);
                    int nextOffset = offsetNumber + 20;
                    if (offsetNumber < 980) { //todo why does this not work?
                        m.put("nextOffset", nextOffset);
                    }
                    int prevOffset = offsetNumber - 20;
                    if (offsetNumber >= 20) { //todo why does this not work?
                        m.put("prevOffset", prevOffset);
                    }
                    return new ModelAndView(m, "people.html");
                },
                new MustacheTemplateEngine()
        );

        Spark.get("/person", (request, response) -> {
                    HashMap m = new HashMap();
                    String idNumber = request.queryParams("id");

                    Person p = null;

                    for (Person person : people) {
                        if (person.idNumber.equals(idNumber)) {
                            p = person;
                        }
                    }
                    m.put("person", p);
                    return new ModelAndView(m, "person.html");
                },
                new MustacheTemplateEngine()
        );

        Spark.post("/view-person", (request, response) -> {
            String idNumber = request.queryParams("idNumber");
            String firstName = request.queryParams("firstName");
            String lastName = request.queryParams("lastName");
            String email = request.queryParams("email");
            String country = request.queryParams("country");
            String ipAddress = request.queryParams("ipAddress");

            Person person = new Person(idNumber, firstName, lastName, email, country, ipAddress);
            HashMap m = new HashMap();
            m.put("person", person);
            response.redirect("/");

            return "";
        });

        Spark.post("/back", (request, response) -> {
            response.redirect("/");
            return "";
        });

    }

}
