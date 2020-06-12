package flashcards;

import org.w3c.dom.ls.LSOutput;

import java.io.*;
import java.sql.SQLOutput;
import java.util.*;
import java.util.stream.Collectors;


public class Main {
    public static void main(String[] args) {
        Menu menu = new Menu(args);
        while(true) {
            menu.Menu();
        }
    }
}
class Menu {
    Scanner scanner = new Scanner(System.in);
    protected ArrayList<String> log = new ArrayList<>();
    protected LinkedHashMap<String, String> dict = new LinkedHashMap<> ();
    protected HashMap<String, Integer> stats = new HashMap<> ();
    protected LinkedHashMap<String, ArrayList<String>> exportList = new LinkedHashMap<>();
    protected ArrayList<String> valStatList = new ArrayList<>();
    protected static String[] stringArray = {"add", "remove", "import", "export", "ask", "exit", "log", "hardest card", "reset stats"};
    protected static String str = Arrays.stream(stringArray).collect(Collectors.joining(", "));
    String[] args;
    boolean flag;

    public Menu(String[] args){
        this.args = args;
        if(Arrays.asList(args).contains("-import")) {
            importCard(args[(Arrays.asList(args).indexOf("-import") + 1)]);
        }
        if(Arrays.asList(args).contains("-export")) {
            flag = true;
        }
    }


    public void Menu(){
        System.out.format("Input the action (%s):%n", str);
        log.add("Input the action " + str);
        String input = scanner.nextLine().toLowerCase();
        log.add(input);

        if (str.contains(input)) {
            switch (input) {
                case "add":
                    addCard();
                    break;
                case "remove":
                    removeCard();
                    break;
                case "import":
                    importCard();
                    break;
                case "export":
                    exportCard();
                    break;
                case "ask":
                    askCard();
                    break;
                case "exit":
                    System.out.println("Bye bye!");
                    if(flag == true) {
                        exportCard(args[(Arrays.asList(args).indexOf("-export") + 1)]);
                    }
                    System.exit(0);
                case "log":
                    log();
                    break;
                case "hardest card":
                    getHardest();
                    break;
                case "reset stats":
                    resetStat();
                    break;
            }
        }
    }
    public void addCard(){
        System.out.format("The card:%n");
        log.add("The card:");
        String term = scanner.nextLine();
        log.add(term);
        if(dict.containsKey(term)){
            System.out.format("The card \"%s\" already exists.%n", term);
            log.add(String.format("The card \"%s\" already exists.%n", term));
        } else {
            System.out.format("The definition of the card:%n");
            log.add(String.format("The definition of the card:%n"));
            String def = scanner.nextLine();
            log.add(def);
            if(dict.values().contains(def)) {
                System.out.format("The definition \"%s\" already exists:%n", def);
                log.add(String.format("The definition \"%s\" already exists:%n", def));
            } else {
                dict.put(term,def);
                System.out.printf("The pair (\"%s\":\"%s\") has been added.%n", term, def);
                log.add(String.format("The pair (\"%s\":\"%s\") has been added.%n", term, def));
            }
        }
    }
    public void removeCard(){
        System.out.println("The card:");
        log.add("The card:");
        String card = scanner.nextLine();
        log.add(card);
        if(dict.containsKey(card)) {
            dict.remove(card);
            System.out.println("The card has been removed.");
            log.add("The card has been removed.");
            stats.remove(card);
        } else {
            System.out.printf("Can't remove \"%s\": there is no such card.%n", card);
            log.add(String.format("Can't remove \"%s\": there is no such card.%n", card));
        }
    }
    public void importCard(){
        System.out.println("File name:");
        log.add("File name:");
        String filePath = scanner.nextLine();
        log.add(filePath);
        File file = new File(filePath);
        try(Scanner scanner1 = new Scanner(file)) {
            int counter = 0;
            while(scanner1.hasNext()) {
                String[] line = scanner1.nextLine().split(", ");
                if(dict.containsKey(line[0])) {
                    dict.put(line[0],line[1].strip());
                    stats.put(line[0], Integer.parseInt(line[2]));
                    counter++;
                } else {
                    dict.put(line[0],line[1]);
                    stats.put(line[0], Integer.parseInt(line[2]));
                    counter++;
                }
            }
            System.out.printf("%d %s have been loaded.%n", counter, (counter == 1? "cards" : "cards"));
            log.add(String.format("%d %s have been loaded.%n", counter, (counter == 1? "cards" : "cards")));
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            log.add("File not found.");
        }
    }
    public void importCard(String fileName){
        File file = new File(fileName);
        try(Scanner scanner1 = new Scanner(file)) {
            int counter = 0;
            while(scanner1.hasNext()) {
                String[] line = scanner1.nextLine().split(", ");
                if(dict.containsKey(line[0])) {
                    dict.put(line[0],line[1].strip());
                    stats.put(line[0], Integer.parseInt(line[2]));
                    counter++;
                } else {
                    dict.put(line[0],line[1]);
                    if(line[2].equals("null")) {
                        stats.put(line[0], 0);
                    } else {
                        stats.put(line[0], Integer.parseInt(line[2]));
                    }
                    counter++;
                }
            }
            System.out.printf("%d %s have been loaded.%n", counter, (counter == 1? "cards" : "cards"));
            log.add(String.format("%d %s have been loaded.%n", counter, (counter == 1? "cards" : "cards")));
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            log.add("File not found.");
        }
    }

    public void exportCard(){
        for(String str: dict.keySet()) {
            valStatList.add(dict.get(str));
            valStatList.add(stats.get(str).toString());
            exportList.put(str, valStatList);
            valStatList.clear();
        }
        System.out.println("File name:");
        log.add("File name:");
        String fileName = scanner.nextLine();
        log.add(fileName);
        File file = new File(fileName);
        try(PrintWriter printwrite = new PrintWriter(file)) {
            int counter = 0;
            for(var i: dict.entrySet()) {
                printwrite.printf("%s, %s, %s%n", i.getKey(), i.getValue(),stats.get(i.getKey()));
                counter++;
            }
            System.out.printf("%d %s have been saved%n", counter, (counter == 1? "cards" : "cards"));
            log.add(String.format("%d %s have been saved%n", counter, (counter == 1? "cards" : "cards")));
        } catch(IOException e) {
            System.out.println("Can't export");
            log.add("Can't export");
        }
    }
    public void exportCard(String fileName){
        for(String key: dict.keySet()) {
            valStatList.add(dict.get(key));
            //System.out.print(str + " " +stats.get(str).toString());
            //valStatList.add(stats.get(key).toString());
            //System.out.println(stats.get(key).toString());

            exportList.put(key, valStatList);
            valStatList.clear();
        }
        File file = new File(fileName);
        try(PrintWriter printwrite = new PrintWriter(file)) {
            int counter = 0;
            for(var i: dict.entrySet()) {
                printwrite.printf("%s, %s, %s%n", i.getKey(), i.getValue(),stats.get(i.getKey()));
                counter++;
            }
            System.out.printf("%d %s have been saved%n", counter, (counter == 1? "cards" : "cards"));
            log.add(String.format("%d %s have been saved%n", counter, (counter == 1? "cards" : "cards")));
        } catch(IOException e) {
            System.out.println("Can't export");
            log.add("Can't export");
        }
    }
    public void askCard(){
        System.out.println("How many times to ask?");
        log.add("How many times to ask?");
        int num = Integer.parseInt(scanner.nextLine());
        log.add(Integer.toString(num));
        int count = 1;
        while(count <= num) {
            for (String str : dict.keySet()) {
                if (count <= num) {
                    System.out.format("Print the definition of \"%s\":%n", str);
                    log.add(String.format("Print the definition of \"%s\":%n", str));
                    String ans = scanner.nextLine();
                    log.add(ans);
                    String key = "";
                    for (var i : dict.entrySet()) {
                        if (i.getValue().equals(ans)) {
                            key = i.getKey();
                        }
                    }
                    if (dict.get(str).equals(ans)) {
                        System.out.println("Correct answer.");
                        log.add("Correct answer.");
                    } else if (dict.values().contains(ans)) {
                        stats.put(str, (stats.get(str) != null? stats.get(str) + 1: 1));
                        System.out.format("Wrong answer. The correct one is \"%s\", you've just written the definition of \"%s\".%n",
                                dict.get(str), key);
                        log.add(String.format("Wrong answer. The correct one is \"%s\", you've just written the definition of \"%s\".%n",
                                dict.get(str), key));
                    } else {
                        stats.put(str, (stats.get(str) != null? stats.get(str) + 1: 1));
                        System.out.format("Wrong answer. The correct one is \"%s\".%n", dict.get(str));
                        log.add(String.format("Wrong answer. The correct one is \"%s\".%n", dict.get(str)));
                    }
                    count++;
                }
            }
        }
    }
    public void log(){
        System.out.println("File name:");
        log.add("File name:");
        String fileName = scanner.nextLine();
        log.add(fileName);
        File file = new File(fileName);
        try(PrintWriter printwrite = new PrintWriter(file)) {
            for(String str : log) {
                printwrite.printf(str + "%n");
            }
            System.out.println("The log has been saved.");
            log.add("The log has been saved.");
        } catch(IOException e) {
            System.out.println("Can't export");
            log.add("Can't export");
        }
    }
    public void getHardest() {
        if (stats.isEmpty()) {
            System.out.println("There are no cards with errors");
            log.add("There are no cards with errors");
        } else {
            ArrayList<String> maxVals = new ArrayList<>();
            int max = Collections.max(stats.values());
            for(var v: stats.entrySet()) {
                if (v.getValue() == max) {
                    maxVals.add(v.getKey());
                }
            }
            String str1 = "";
            for(int i = 0; i < maxVals.size(); i++) {
                if(i == (maxVals.size() -1)) {
                    str1 += "\"" + maxVals.get(i) + "\"";

                } else {
                    str1 += "\"" + maxVals.get(i) + "\", ";
                    //Collections.reverse(maxVals);
                    //str1 += maxVals.toString();

                }
            }
            System.out.format("The hardest %s %s. You have %d errors answering them.%n",(maxVals.size() > 1? "cards are": "card is"), str1, max);
            log.add(String.format("The hardest cards is \"%s\", attempts: %d", maxVals.toString(), max));
        }
    }
    public void resetStat() {
        stats.clear();
        System.out.println("Card statistics has been reset");
        log.add("Card statistics has been reset");

    }
    


}

