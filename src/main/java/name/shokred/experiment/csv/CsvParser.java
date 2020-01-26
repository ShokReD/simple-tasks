package name.shokred.experiment.csv;

import org.junit.*;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class CsvParser {
    public static void main(String[] args) {
        Assert.assertEquals(parseCsv("quq,pups\npup,vups"), new ArrayList<List>() {{
            add(new ArrayList<String>() {{
                add("quq");
                add("pups");
            }});
            add(new ArrayList<String>() {{
                add("pup");
                add("vups");
            }});
        }});

        Assert.assertEquals(parseCsv("следующая ячейка содержит перевод строки,\"до перевода строки\n" +
                        "после перевода строки\",а это третий столбец\n" +
                        "следующая ячейка содержит кавычку и запятую,\"вот они: \"\",\",\n" +
                        "в этой строке вторая и третья ячейка содержат по одной кавычке,\"\"\"\",\"\"\"\"")
                , new ArrayList<List>() {{
                    add(new ArrayList<String>() {{
                        add("следующая ячейка содержит перевод строки");
                        add("до перевода строки\n" +
                                "после перевода строки");
                        add("а это третий столбец");
                    }});
                    add(new ArrayList<String>() {{
                        add("следующая ячейка содержит кавычку и запятую");
                        add("вот они: \",");
                        add("");
                    }});
                    add(new ArrayList<String>() {{
                        add("в этой строке вторая и третья ячейка содержат по одной кавычке");
                        add("\"");
                        add("\"");
                    }});
                }});
    }

    public static List<List<String>> parseCsv(String csv) {
        List<List<String>> output = new ArrayList<>();

        List<String> line = new ArrayList<>();

        Deque<Character> deque = new LinkedList<>(charArrayToList(csv.toCharArray()));

        boolean inside = false;

        StringBuilder nextField = new StringBuilder();

        Character c;
        try {
            while (!deque.isEmpty()) {
                c = deque.pollFirst();
                switch (c) {
                    case '"': {
                        if (inside) {
                            c = deque.pollFirst();
                            if (c == null) {
                                continue;
                            }
                            switch (c) {
                                case '"': {
                                    nextField.append("\"");
                                    break;
                                }
                                case ',': {
                                    line.add(nextField.toString());
                                    nextField = new StringBuilder();
                                    inside = false;
                                    break;
                                }
                                default:
                                    inside = false;
                            }
                        } else {
                            inside = true;
                        }
                        break;
                    }
                    case '\n': {
                        if (inside) {
                            nextField.append(c);
                        } else {
                            line.add(nextField.toString());
                            nextField = new StringBuilder();

                            output.add(line);
                            line = new ArrayList<>();
                        }
                        break;
                    }
                    case ',': {
                        if (inside) {
                            nextField.append(c);
                        } else {
                            line.add(nextField.toString());
                            nextField = new StringBuilder();
                        }
                        break;
                    }
                    default: {
                        nextField.append(c);
                    }
                }
            }
            line.add(nextField.toString());
            output.add(line);
        } catch (Exception e) {
            throw new IllegalArgumentException("The CSV is invalid");
        }

        return output;
    }

    private static List<Character> charArrayToList(char[] array) {
        List<Character> output = new ArrayList<>();

        for (char c : array) {
            output.add(c);
        }

        return output;
    }
}
