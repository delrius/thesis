package reference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Reference {
    private List<String> authors;
    private String title;

    public Reference(List<String> authors, String title) {
        this.authors = filter(authors);
        this.title = title;
    }

    private List<String> filter(List<String> strings) {
        List<String> res = new ArrayList<>();
        for (String s : strings) {
            if (!contains(res, s)) {
                res.add(s);
            }
        }
        return res;
    }

    private boolean contains(List<String> list, String str) {
        for (String s : list) {
            if (compareTwoAuthors(s, str)) {
                return true;
            }
        }
        return false;
    }


    public static boolean compareTwoAuthors(String a1, String a2) {
        String[] split1 = a1.replaceAll(".", "").split(" ");
        String[] split2 = a2.replaceAll(".", "").split(" ");
        for (String s : split1) {
            if (!Arrays.asList(split2).contains(s)) {
                return false;
            }
        }
        return true;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[Persons: ");
        for (String author : authors) {
            builder.append(author).append(";");
        }

        builder.append("Title:");
        builder.append(title);
        builder.append("]");
        return builder.toString();
    }
}
