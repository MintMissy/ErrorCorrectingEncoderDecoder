import java.util.ArrayList;
import java.util.Scanner;

import static java.lang.Integer.parseInt;

class Main {
    public static void main(String[] args) {
        ArrayList<String> numbers = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        String input;
        do {
            input = scanner.next();
            if (!input.equals("0")) {
                numbers.add(input);
            }
        } while (!input.equals("0"));

        for (String number : numbers) {
            try {
                System.out.println(parseInt(number) * 10);
            } catch (Exception e) {
                System.out.println("Invalid user input: " + number);
            }
        }
    }
}