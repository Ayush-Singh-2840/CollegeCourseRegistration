import java.util.*;
import java.io.*;

class Course {
    String id;
    LinkedList<String> students = new LinkedList<>();
    Queue<String> waiting = new LinkedList<>();

    Course(String id) {
        this.id = id;
    }
}

public class CollegeCourseRegistrationSystem {

    static HashMap<String, Course> courses = new HashMap<>();
    static HashMap<String, LinkedList<String>> graph = new HashMap<>();
    static HashMap<String, LinkedList<String>> completed = new HashMap<>();

    static int capacity = 2;

    public static void main(String[] args) {

        loadData();

        Scanner sc = new Scanner(System.in);

        while (true) {

            System.out.println("\n1. Register Student");
            System.out.println("2. Exit");
            System.out.print("Enter choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            if (choice == 1) {

                System.out.print("Enter student name: ");
                String student = sc.nextLine();

                System.out.print("Enter course ID: ");
                String id = sc.nextLine();

                register(student, id);

            } else if (choice == 2) {

                saveData();
                System.out.println("Exiting system...");
                break;

            } else {
                System.out.println("Invalid choice.");
            }
        }

        sc.close();
    }

    // Register student
    static void register(String student, String id) {

        Course c = courses.get(id);

        if (c == null) {
            System.out.println("Course " + id + " not found.");
            return;
        }

        if (c.students.contains(student) ||
            c.waiting.contains(student)) {

            System.out.println("Duplicate registration not allowed.");
            return;
        }

        LinkedList<String> required = graph.get(id);
        LinkedList<String> done = completed.get(student);

        if (required != null) {

            for (String pre : required) {

                if (done == null || !done.contains(pre)) {
                    System.out.println("Prerequisites not completed.");
                    return;
                }
            }
        }

        if (c.students.size() < capacity) {

            c.students.add(student);

            System.out.println("Registration successful: "
                    + student + " -> " + id);

            System.out.println("Available seats: "
                    + (capacity - c.students.size()));

        } else {

            c.waiting.add(student);

            System.out.println("Course full. Added to waiting queue.");
        }
    }

    // Load data from file
    static void loadData() {

        try {

            File file = new File("registrations.txt");

            if (!file.exists()) {

                PrintWriter p = new PrintWriter(file);

                p.println("COURSE|CS101");
                p.println("COURSE|CS102");
                p.println("PREREQUISITE|CS102|CS101");

                p.close();
            }

            Scanner fs = new Scanner(file);

            while (fs.hasNextLine()) {

                String line = fs.nextLine();

                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] data = line.split("\\|");

                if (data[0].equals("COURSE")) {

                    courses.put(data[1], new Course(data[1]));

                } else if (data[0].equals("PREREQUISITE")) {

                    if (!graph.containsKey(data[1])) {
                        graph.put(data[1], new LinkedList<String>());
                    }

                    graph.get(data[1]).add(data[2]);

                } else if (data[0].equals("COMPLETED")) {

                    if (!completed.containsKey(data[1])) {
                        completed.put(data[1], new LinkedList<String>());
                    }

                    completed.get(data[1]).add(data[2]);

                } else if (data[0].equals("ENROLLED")) {

                    if (courses.containsKey(data[1])) {
                        courses.get(data[1]).students.add(data[2]);
                    }

                } else if (data[0].equals("WAITING")) {

                    if (courses.containsKey(data[1])) {
                        courses.get(data[1]).waiting.add(data[2]);
                    }
                }
            }

            fs.close();

            System.out.println("Data loaded successfully.");

        } catch (Exception e) {

            System.out.println("Error loading file: " + e.getMessage());
        }
    }

    // Save data into file
    static void saveData() {

        try {

            File file = new File("registrations.txt");

            PrintWriter p = new PrintWriter(new FileWriter(file));

            for (String id : courses.keySet()) {
                p.println("COURSE|" + id);
            }

            for (String id : graph.keySet()) {

                for (String pre : graph.get(id)) {
                    p.println("PREREQUISITE|" + id + "|" + pre);
                }
            }

            for (String student : completed.keySet()) {

                for (String id : completed.get(student)) {
                    p.println("COMPLETED|" + student + "|" + id);
                }
            }

            for (String id : courses.keySet()) {

                Course c = courses.get(id);

                for (String student : c.students) {
                    p.println("ENROLLED|" + id + "|" + student);
                }

                for (String student : c.waiting) {
                    p.println("WAITING|" + id + "|" + student);
                }
            }

            p.close();

            System.out.println("Data saved successfully!");
            System.out.println("File location: "
                    + file.getAbsolutePath());

        } catch (Exception e) {

            System.out.println("Error saving file: " + e.getMessage());
        }
    }
}
