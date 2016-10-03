
import spark.ModelAndView;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import static java.lang.Integer.parseInt;

public class PeopleWeb {
    //changed something !
    static ArrayList<Person> peopleList = new ArrayList<>();
    public static void main(String[] args) throws FileNotFoundException {

        File f = new File("people.csv");
        Scanner fileScanner = new Scanner(f);
        fileScanner.nextLine();         // skips the first line in file
        while (fileScanner.hasNext()) {
            String line = fileScanner.nextLine();
            String[] columns = line.split("\\,");
            Person person = new Person(columns[0], columns[1], columns[2], columns[3], columns[4], columns[5]);
            peopleList.add(person);
        }
        fileScanner.close();

        Spark.init();
        Spark.get("/",
                ((request, response) -> {
                    HashMap m = new HashMap();
                    String offset = request.queryParams("offset");
                    int offsetNumber = 0;
                    if (offset != null){
                        offsetNumber = parseInt(offset);
                    }
                    ArrayList<Person> listOf20People = new ArrayList<>();
                    for (int i=offsetNumber; i<offsetNumber+20; i++){
                        listOf20People.add(peopleList.get(i));
                    }
                    Integer next = null;
                    Integer previous = null;

                    if (offsetNumber < (peopleList.size()-20)){
                        next = offsetNumber + 20;
                    }
                    if (offsetNumber >= 20){
                        previous = offsetNumber - 20;
                    }
                    m.put("people", listOf20People);
                    m.put("next", next);
                    m.put("previous", previous);

                    return new ModelAndView(m, "people.html");
                }),
                new MustacheTemplateEngine()
        );

        // trying to change something
        Spark.get("/person",
                ((request, response) -> {
                    HashMap m = new HashMap();
                    String id = request.queryParams("id");
                    int personId = 0;
                    if (id != null){
                        personId = parseInt(id);
                    }
                    //id,first_name,last_name,email,country,ip_address
                    Person person = peopleList.get(personId-1);
                    m.put("id", person.id);
                    m.put("firstName", person.firstName);
                    m.put("lastName", person.lastName);
                    m.put("email", person.email);
                    m.put("country", person.country);
                    m.put("ipAddress", person.ipAddress);
                    return new ModelAndView(m, "person.html");
                }),
                new MustacheTemplateEngine()
        );
    }
}