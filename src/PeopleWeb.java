import spark.ModelAndView;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class PeopleWeb {
    static ArrayList<Person> peopleList = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        Spark.init();
        File f = new File("people.csv");
        Scanner fileScanner = new Scanner(f);

        String line;
        while (fileScanner.hasNext()) {
            line = fileScanner.nextLine();
            while (line.startsWith("id,first_name")) {
                line = fileScanner.nextLine();
            }
            String[] columns = line.split(",");
            Person person = new Person(Integer.parseInt(columns[0]), columns[1], columns[2], columns[3], columns[4], columns[5]);
            peopleList.add(person);
        }
        Spark.get("/",
                ((request, response) -> {
                    ArrayList<Person> peopleList20 = new ArrayList<>();
                    HashMap m = new HashMap();
                    String offset = request.queryParams("offset");
                    int offsetNum = 0;
                    Integer previous = null;
                    Integer next = null;

                    if (offset != null) {
                        offsetNum = Integer.parseInt(offset);
                    }
                    for (int i = offsetNum; i < offsetNum + 20; i++) {
                        peopleList20.add(peopleList.get(i));
                    }
                    if (offsetNum >= 20) {
                        previous = offsetNum - 20;
                    }
                    if (offsetNum < peopleList.size() - 20) {
                        next = offsetNum + 20;
                    }
                    m.put("names", peopleList20);
                    m.put("previous", previous);
                    m.put("next", next);
                    return new ModelAndView(m, "people.html");
                }),
                new MustacheTemplateEngine()
        );
        Spark.get(
                "/person",
                ((request, response) -> {
                    String id = request.queryParams("id");
                    HashMap m = new HashMap();

                    m.put("nameDetail", peopleList.get(Integer.parseInt(id) - 1));
                    return new ModelAndView(m, "people.html");
                }),
                new MustacheTemplateEngine()
        );
    }
}
