import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

class Main {
    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        StringBuilder line = new StringBuilder();

        int charAsNumber = reader.read();
        while(charAsNumber != -1) {
            char character = (char) charAsNumber;
            line.append(character);
            charAsNumber = reader.read();
        }

        ArrayList<String> words = new ArrayList<>();
        String[] splittedLine = line.toString().split(" ");
        for (String word : splittedLine) {
            word = word.replace(" ", "");
            if (word.equals("")){
                continue;
            }
            words.add(word);
        }

        reader.close();
        System.out.println(words.size());
    }
}