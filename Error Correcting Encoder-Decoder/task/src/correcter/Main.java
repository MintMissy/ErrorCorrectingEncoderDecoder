package correcter;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String message = scanner.nextLine();
        System.out.println(message);

        String encodedMessage = encode(message);
        System.out.println(encodedMessage);

        String messageWithErrors = simulateErrors(encodedMessage);
        System.out.println(messageWithErrors);

        String decodedMessage = decode(messageWithErrors);
        System.out.println(decodedMessage);
    }

    private static String decode(String message) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < message.length(); i += 3) {
            ArrayList<Character> chars = new ArrayList<>();
            chars.add(message.charAt(i));
            chars.add(message.charAt(i + 1));
            chars.add(message.charAt(i + 2));

            if (chars.get(0) == chars.get(1)){
                sb.append(chars.get(0));
            } else if (chars.get(1) == chars.get(2)){
                sb.append(chars.get(1));
            } else {
                sb.append(chars.get(2));
            }
        }
        return sb.toString();
    }

    private static String encode(String message) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < message.length(); i++) {
            char character = message.charAt(i);
            sb.append(String.valueOf(character).repeat(3));
        }
        return sb.toString();
    }

    public static String simulateErrors(String string) {
        StringBuilder sb = new StringBuilder(string);
        for (int i = 0; i < sb.length(); i++) {
            if (i % 3 == 0) {
                Character newCharacter = getRandomCharacter();
                while (sb.charAt(i) == newCharacter) {
                    newCharacter = getRandomCharacter();
                }
                sb.setCharAt(i, newCharacter);
            }
        }
        return sb.toString();
    }

    private static char getRandomCharacter() {
        Random random = new Random();
        double chance = Math.random();

        // return uppercase letter
        if (chance < 0.33) {
            return (char) (random.nextInt(90 - 65 + 1) + 65);
            // return lowercase letter
        } else if (chance < 0.66) {
            return (char) (random.nextInt(122 - 97 + 1) + 97);
        }
        // return idgit
        return (char) (random.nextInt(57 - 48 + 1) + 48);
    }
}
