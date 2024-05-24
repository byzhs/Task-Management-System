import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {
        Main system = new Main();
        system.run();
    }

    private Map<String, Task> tasks = new HashMap<>();
    private Scanner scanner = new Scanner(System.in);

    public void run() {
        while (true) {
            System.out.println("\nTask Management System");
            System.out.println("1. Add Task");
            System.out.println("2. Save Task");
            System.out.println("3. Load Task");
            System.out.println("4. Edit Task");
            System.out.println("5. Link Task to Solution Folder");
            System.out.println("6. Count Solutions");
            System.out.println("7. View Task Content");
            System.out.println("8. Count Student Submissions");
            System.out.println("9. Preview Student Solutions");
            System.out.println("10. Exit");
            System.out.print("Select an option: ");

            try {
                int option = Integer.parseInt(scanner.nextLine());

                switch (option) {
                    case 1:
                        addTask();
                        break;
                    case 2:
                        saveTask();
                        break;
                    case 3:
                        loadTask();
                        break;
                    case 4:
                        editTask();
                        break;
                    case 5:
                        linkTaskToSolutionFolder();
                        break;
                    case 6:
                        countSolutions();
                        break;
                    case 7:
                        viewTaskContent();
                        break;
                    case 8:
                        countTasksSubmittedByStudent();
                        break;
                    case 9:
                        previewStudentSolutions();
                        break;
                    case 10:
                        System.out.println("Exiting...");
                        return;
                    default:
                        System.out.println("Invalid option, please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input, please enter a number.");
            }
        }
    }


    private void addTask() {
        System.out.print("Enter task name: ");
        String taskName = scanner.nextLine();
        Task task = new Task(taskName);

        System.out.println("Enter task content (type 'end' to finish):");
        StringBuilder contentBuilder = new StringBuilder();
        while (true) {
            String content = scanner.nextLine();
            if ("end".equalsIgnoreCase(content)) break;
            contentBuilder.append(content).append("\n");
        }

        String originalContent = contentBuilder.toString();
        List<Component> components = extractComponents(originalContent);
        task.addComponents(components, originalContent);

        tasks.put(taskName, task);
        System.out.println("Task added successfully.");
    }

    private void saveTask() {
        System.out.print("Enter task name to save: ");
        String taskName = scanner.nextLine();
        saveTask(taskName);
    }

    private void saveTask(String taskName) {
        Task task = tasks.get(taskName);

        if (task == null) {
            System.out.println("Task not found.");
            return;
        }

        String fileName = taskName + ".txt";
        String folderPath = System.getProperty("user.dir") + "\\content";
        Path dirPath = Paths.get(folderPath);
        if (!Files.exists(dirPath)) {
            try {
                Files.createDirectories(dirPath);
            } catch (IOException e) {
                System.out.println("Error creating directory: " + e.getMessage());
                return;
            }
        }

        Path filePath = dirPath.resolve(fileName);

        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            writer.write(task.getOriginalContent().replace("\n", "\\n"));
            System.out.println("Task saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving task: " + e.getMessage());
        }
    }

    private void loadTask() {
        System.out.print("Enter task name to load: ");
        String taskName = scanner.nextLine();
        String folderPath = System.getProperty("user.dir") + "\\content";
        Path filePath = Paths.get(folderPath, taskName + ".txt");

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            StringBuilder originalContentBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                originalContentBuilder.append(line).append("\n");
            }

            String originalContent = originalContentBuilder.toString().trim();
            Task task = new Task(taskName);
            task.setComponents(new ArrayList<>(), originalContent);

            tasks.put(taskName, task);
            System.out.println("Task loaded successfully.");
        } catch (IOException e) {
            System.out.println("Error loading task: " + e.getMessage());
        }
    }

    private void editTask() {
        System.out.print("Enter task name to edit: ");
        String taskName = scanner.nextLine();
        Task task = tasks.get(taskName);

        if (task == null) {
            System.out.println("Task not found.");
            return;
        }

        System.out.println("Current task content:");
        System.out.println(task.getOriginalContent());

        System.out.print("Enter new task name (or press Enter to keep the current name): ");
        String newTaskName = scanner.nextLine();
        if (!newTaskName.trim().isEmpty()) {
            taskName = newTaskName.trim();
            task.setName(taskName);
        }

        System.out.println("Enter new task content (type 'end' to finish):");
        StringBuilder contentBuilder = new StringBuilder();
        while (true) {
            String content = scanner.nextLine();
            if ("end".equalsIgnoreCase(content)) break;
            contentBuilder.append(content).append("\n");
        }

        String newOriginalContent = contentBuilder.toString();
        List<Component> newComponents = extractComponents(newOriginalContent);
        task.setComponents(newComponents, newOriginalContent.trim());

        tasks.put(taskName, task);
        System.out.println("Task edited successfully.");

        saveTask(taskName);
    }

    private void linkTaskToSolutionFolder() {
        System.out.print("Enter task name to link: ");
        String taskName = scanner.nextLine();
        Task task = tasks.get(taskName);

        if (task == null) {
            System.out.println("Task not found.");
            return;
        }

        System.out.print("Enter path to the solutions folder: ");
        String solutionsFolderPath = scanner.nextLine();

        task.setSolutionFolder(solutionsFolderPath);
        System.out.println("Task " + taskName + " linked to solution folder " + solutionsFolderPath + " successfully.");
    }

    private void countSolutions() {
        System.out.print("Enter task name to count solutions for: ");
        String taskName = scanner.nextLine();
        Task task = tasks.get(taskName);

        if (task == null) {
            System.out.println("Task not found.");
            return;
        }

        String solutionsFolderPath = task.getSolutionFolder();
        if (solutionsFolderPath == null) {
            System.out.println("Task is not linked to any solutions folder.");
            return;
        }

        try {
            File solutionsDir = new File(solutionsFolderPath);
            if (!solutionsDir.exists() || !solutionsDir.isDirectory()) {
                System.out.println("Solutions directory not found.");
                return;
            }

            File[] studentDirs = solutionsDir.listFiles(File::isDirectory);
            if (studentDirs == null) {
                System.out.println("No student directories found.");
                return;
            }

            int taskCount = (int) Arrays.stream(studentDirs)
                    .flatMap(studentDir -> Arrays.stream(Objects.requireNonNull(studentDir.listFiles(File::isDirectory))))
                    .filter(this::isNonEmptyDirectory)
                    .count();

            System.out.println("Number of solution files: " + taskCount);
        } catch (Exception e) {
            System.out.println("Error counting solution files: " + e.getMessage());
        }
    }



    private void viewTaskContent() {
        List<String> taskNames = new ArrayList<>(tasks.keySet());

        if (taskNames.isEmpty()) {
            System.out.println("No tasks available.");
            return;
        }

        int currentTaskIndex = 0;
        Iterator<String> taskIterator = taskNames.iterator();

        tasks.entrySet().stream()
                .sorted(new Comparator<Map.Entry<String, Task>>() {
                    @Override
                    public int compare(Map.Entry<String, Task> o1, Map.Entry<String, Task> o2) {
                        return o1.getKey().compareTo(o2.getKey());
                    }
                }).forEach(entry -> System.out.println(entry.getKey()));

        while (taskIterator.hasNext()) {
            Task currentTask = tasks.get(taskNames.get(currentTaskIndex));

            if (currentTask.getSolutionFolder() != null) {
                System.out.println("Task " + currentTask.getName() + " linked to solution folder " + currentTask.getSolutionFolder() + " successfully.");
            }

            System.out.println("Viewing task: " + currentTask.getName());
            System.out.println(currentTask.getOriginalContent());

            System.out.println("\nOptions: ");
            System.out.println("1. Previous task");
            System.out.println("2. Next task");
            System.out.println("3. Back to menu");
            System.out.println("4. View components");
            System.out.print("Select an option: ");

            int option = scanner.nextInt();
            scanner.nextLine();

            if (option == 1) {
                if (currentTaskIndex > 0) {
                    currentTaskIndex--;
                } else {
                    System.out.println("Already at the first task.");
                }
            } else if (option == 2) {
                if (currentTaskIndex < taskNames.size() - 1) {
                    currentTaskIndex++;
                } else {
                    System.out.println("Already at the last task.");
                }
            } else if (option == 3) {
                return;
            } else if (option == 4) {
                showTaskComponents(currentTask);
            } else {
                System.out.println("Invalid option, please try again.");
            }
        }

        tasks.entrySet().stream()
                .sorted((o1, o2) -> o1.getKey().compareTo(o2.getKey()))
                .forEach(entry -> System.out.println(entry.getKey()));
    }


    private void showTaskComponents(Task task) {
        String originalContent = task.getOriginalContent();
        List<Component> components = extractComponents(originalContent);

        components.forEach(component -> {
            switch (component.getType()) {
                case LATEX:
                    System.out.println("LaTeX Formula: " + component.getContent().trim());
                    break;
                case IMAGE:
                    System.out.println("Image: " + component.getContent().trim());
                    break;
                case STRING:
                default:
                    System.out.println("String: " + component.getContent().trim());
                    break;
            }
        });
    }

    private boolean isNonEmptyDirectory(File dir) {
        return Optional.ofNullable(dir.listFiles())
                .map(files -> files.length > 0)
                .orElse(false);
    }

    private void countTasksSubmittedByStudent() {
        System.out.print("Enter student's first name: ");
        String firstName = scanner.nextLine();
        System.out.print("Enter student's last name: ");
        String lastName = scanner.nextLine();

        String studentDirName = firstName + " " + lastName;
        String solutionsFolderPath = System.getProperty("user.dir") + "\\Solution";

        File solutionsDir = new File(solutionsFolderPath);
        if (!solutionsDir.exists() || !solutionsDir.isDirectory()) {
            System.out.println("Solutions directory not found.");
            return;
        }

        File[] solutionDirs = solutionsDir.listFiles(File::isDirectory);
        if (solutionDirs == null) {
            System.out.println("No solutions directories found.");
            return;
        }

        boolean studentFound = false;
        int taskCount = 0;
        for (File solutionDir : solutionDirs) {
            File studentDir = new File(solutionDir, studentDirName);
            if (studentDir.exists() && studentDir.isDirectory()) {
                studentFound = true;
                File[] tasks = studentDir.listFiles(File::isDirectory);
                if (tasks != null) {
                    taskCount += Arrays.stream(tasks)
                            .filter(this::isNonEmptyDirectory)
                            .count();
                }
            }
        }

        if (!studentFound) {
            System.out.println("No student found with name: " + firstName + " " + lastName);
        } else {
            System.out.println("Student " + firstName + " " + lastName + " has submitted " + taskCount + " tasks.");
        }
    }

    private void previewStudentSolutions() {
        System.out.print("Enter student's first name: ");
        String firstName = scanner.nextLine();
        System.out.print("Enter student's last name: ");
        String lastName = scanner.nextLine();

        String studentDirName = firstName + " " + lastName;
        String solutionsFolderPath = System.getProperty("user.dir") + "\\Solution";

        File solutionsDir = new File(solutionsFolderPath);
        if (!solutionsDir.exists() || !solutionsDir.isDirectory()) {
            System.out.println("Solutions directory not found.");
            return;
        }

        File[] solutionDirs = solutionsDir.listFiles(File::isDirectory);
        if (solutionDirs == null) {
            System.out.println("No solutions directories found.");
            return;
        }

        List<File> studentTaskFiles = Arrays.stream(solutionDirs)
                .flatMap(solutionDir -> {
                    File studentDir = new File(solutionDir, studentDirName);
                    if (studentDir.exists() && studentDir.isDirectory()) {
                        return Arrays.stream(studentDir.listFiles(File::isDirectory))
                                .flatMap(taskDir -> Arrays.stream(taskDir.listFiles()));
                    }
                    return Stream.empty();
                })
                .collect(Collectors.toList());

        if (studentTaskFiles.isEmpty()) {
            System.out.println("No solution files found for student: " + firstName + " " + lastName);
            return;
        }

        int currentIndex = 0;
        while (true) {
            File currentFile = studentTaskFiles.get(currentIndex);
            System.out.println("Viewing file: " + currentFile.getName());
            displayFileContent(currentFile);

            System.out.println("\nOptions: ");
            System.out.println("1. Previous file");
            System.out.println("2. Next file");
            System.out.println("3. Back to menu");
            System.out.print("Select an option: ");

            int option = scanner.nextInt();
            scanner.nextLine();

            if (option == 1) {
                if (currentIndex > 0) {
                    currentIndex--;
                } else {
                    System.out.println("Already at the first file.");
                }
            } else if (option == 2) {
                if (currentIndex < studentTaskFiles.size() - 1) {
                    currentIndex++;
                } else {
                    System.out.println("Already at the last file.");
                }
            } else if (option == 3) {
                return;
            } else {
                System.out.println("Invalid option, please try again.");
            }
        }
    }

    private void displayFileContent(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    public static List<Component> extractComponents(String input) {
        List<Component> components = new ArrayList<>();
        int length = input.length();
        StringBuilder currentString = new StringBuilder();

        for (int i = 0; i < length; i++) {
            char c = input.charAt(i);

            if (c == '$') {
                if (currentString.length() > 0) {
                    components.add(new Component(ComponentType.STRING, currentString.toString().trim()));
                    currentString.setLength(0);
                }
                int latexEnd = input.indexOf('$', i + 1);
                if (latexEnd == -1) {
                    throw new IllegalArgumentException("Unmatched $ in LaTeX expression");
                }
                components.add(new Component(ComponentType.LATEX, input.substring(i, latexEnd + 1).trim()));
                i = latexEnd;
            } else if (input.startsWith("\\href{", i)) {
                if (currentString.length() > 0) {
                    components.add(new Component(ComponentType.STRING, currentString.toString().trim()));
                    currentString.setLength(0);
                }
                int hrefEnd = input.indexOf('}', i + 6);
                if (hrefEnd == -1) {
                    throw new IllegalArgumentException("Unmatched { in \\href expression");
                }
                components.add(new Component(ComponentType.IMAGE, input.substring(i, hrefEnd + 1).trim()));
                i = hrefEnd;
            } else {
                currentString.append(c);
            }
        }

        if (currentString.length() > 0) {
            components.add(new Component(ComponentType.STRING, currentString.toString().trim()));
        }

        return components;
    }
}

interface TaskOperations {
    void addComponents(List<Component> components, String originalContent);
    void setComponents(List<Component> components, String originalContent);
}

abstract class AbstractTask implements TaskOperations {
    protected String name;
    protected String originalContent;
    protected List<Component> components = new ArrayList<>();

    public AbstractTask(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOriginalContent() {
        return originalContent;
    }

    public List<Component> getComponents() {
        return components;
    }
}

enum ComponentType {
    STRING, LATEX, IMAGE
}

class Task extends AbstractTask {

    private String solutionFolder;

    public Task(String name) {
        super(name);
    }

    @Override
    public void addComponents(List<Component> components, String originalContent) {
        this.components.addAll(components);
        this.originalContent = originalContent;
    }

    @Override
    public void setComponents(List<Component> components, String originalContent) {
        this.components = components;
        this.originalContent = originalContent;
    }

    public String getSolutionFolder() {
        return solutionFolder;
    }

    public void setSolutionFolder(String solutionFolder) {
        this.solutionFolder = solutionFolder;
    }
}

class Component {
    private ComponentType type;
    private String content;

    public Component(ComponentType type, String content) {
        this.type = type;
        this.content = content;
    }

    public ComponentType getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "Component{" +
                "type=" + type +
                ", content='" + content + '\'' +
                '}';
    }
}


