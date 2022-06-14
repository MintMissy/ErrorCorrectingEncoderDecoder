package correcter;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Write a mode: ");
        String mode = scanner.next();

        switch (mode) {
            case "encode":
                encode();
                break;
            case "send":
                send();
                break;
            case "decode":
                decode();
                break;
        }
    }

    private static void encode() throws IOException {
        FileReader reader = new FileReader("send.txt");
//        FileReader reader = new FileReader("C:\\Users\\Mint Missy\\IdeaProjects\\Error Correcting Encoder-Decoder\\Error Correcting Encoder-Decoder\\task\\src\\correcter\\send.txt");

        StringBuilder textView = new StringBuilder();
        StringBuilder hexView = new StringBuilder();
        StringBuilder binView = new StringBuilder();
        StringBuilder expandView = new StringBuilder();
        StringBuilder parityView = new StringBuilder();
        StringBuilder parityHexView = new StringBuilder();

        int charAsNumber = reader.read();
        while (charAsNumber != -1) {
            char character = (char) charAsNumber;
            byte characterAsByte = (byte) character;
            String characterAsBinary = addLeadingZerosToBin(Integer.toBinaryString(characterAsByte));
            String characterAsHex = Integer.toHexString(characterAsByte);
            textView.append(character);
            hexView.append(characterAsHex).append(" ");
            binView.append(characterAsBinary).append(" ");

            charAsNumber = reader.read();
        }
        reader.close();

        String rawBinary = binView.toString().replace(" ", "");
        StringBuilder section = new StringBuilder();
        ArrayList<Byte> bits = new ArrayList<>();

        for (int i = 0; i < rawBinary.length(); i++) {
            char c = rawBinary.charAt(i);
            section.append(c).append(c);

            bits.add(Byte.valueOf(Character.toString(c)));
            if (bits.size() == 3) {
                int parity = bits.get(0) ^ bits.get(1) ^ bits.get(2);

                expandView.append(section).append(".").append(".").append(" ");
                section.append(parity).append(parity);
                parityView.append(section).append(" ");

                int number = Integer.parseInt(section.toString(), 2);
                String hexCode = addLeadingZerosToHex(Integer.toHexString(number));
                parityHexView.append(hexCode.toUpperCase()).append(" ");

                section.setLength(0);
                bits.clear();
            }
        }

        int missingBits = 3 - bits.size();
        while (bits.size() > 0 && bits.size() < 3) {
            section.append(".").append(".");
            bits.add((byte) 1);
        }

        if (bits.size() == 3) {
            int parity = bits.get(0);
            for (int i = 1; i <= missingBits; i++) {
                parity ^= bits.get(i);
            }

            section.append(parity).append(parity);

            int number = Integer.parseInt(section.toString().replace(".", ""), 2);
            String hexCode = addLeadingZerosToHex(Integer.toHexString(number));
            parityHexView.append(hexCode).append(" ");

            expandView.append(".").append(".");
            parityView.append(section.toString().replace(".", "0"));
        }

        System.out.println("send.txt:");
        System.out.println("text view: " + textView);
        System.out.println("hex view: " + hexView);
        System.out.println("bin view: " + binView);
        System.out.println();
        System.out.println("encoded.txt");
        System.out.println("expand: " + expandView);
        System.out.println("parity: " + parityView);
        System.out.println("hex view: " + parityHexView);

        OutputStream outputStream = new FileOutputStream("encoded.txt");
//        OutputStream outputStream = new FileOutputStream("C:\\Users\\Mint Missy\\IdeaProjects\\Error Correcting Encoder-Decoder\\Error Correcting Encoder-Decoder\\task\\src\\correcter\\encoded.txt");

        String[] binaryCodes = parityView.toString().split(" ");
        for (String binaryCode : binaryCodes) {
            outputStream.write((char) ((byte) Integer.parseInt(binaryCode, 2)));
        }
        outputStream.close();
    }

    private static void send() throws IOException {
        InputStream inputStream = new FileInputStream("encoded.txt");
//        InputStream inputStream = new FileInputStream("C:\\Users\\Mint Missy\\IdeaProjects\\Error Correcting Encoder-Decoder\\Error Correcting Encoder-Decoder\\task\\src\\correcter\\encoded.txt");

        ArrayList<Character> charsToWrite = new ArrayList<>();

        int charAsNumber = inputStream.read();
        while (charAsNumber != -1) {
            char character = (char) ((byte) charAsNumber);
            byte characterAsByte = (byte) character;
            String characterAsBinary = addLeadingZerosToBin(Integer.toBinaryString(characterAsByte));
            String corruptedByte = corruptBinaryByte(characterAsBinary);

//        characterAsByte ^= 1 << 1;
            charsToWrite.add((char) ((byte) Integer.parseInt(corruptedByte, 2)));

            charAsNumber = inputStream.read();
        }
        inputStream.close();

        OutputStream outputStream = new FileOutputStream("received.txt");
//        OutputStream outputStream = new FileOutputStream("C:\\Users\\Mint Missy\\IdeaProjects\\Error Correcting Encoder-Decoder\\Error Correcting Encoder-Decoder\\task\\src\\correcter\\received.txt");
        for (Character character : charsToWrite) {
            outputStream.write(character);
        }
        outputStream.close();
    }

    private static void decode() throws IOException {
        InputStream inputStream = new FileInputStream("received.txt");

        ArrayList<Character> charactersToWrite = new ArrayList<>();
        StringBuilder decodedBinaryString = new StringBuilder();

        int charAsNumber = inputStream.read();
        while (charAsNumber != -1) {
            char character = (char) ((byte) charAsNumber);
            byte characterAsByte = (byte) character;
            String characterAsBinary = addLeadingZerosToBin(Integer.toBinaryString(characterAsByte));

            ArrayList<Integer> pairs = new ArrayList<>();
            int corruptedPairIndex = 0;

            for (int i = 0; i < characterAsBinary.length(); i += 2) {
                if (characterAsBinary.charAt(i) == characterAsBinary.charAt(i + 1)) {
                    pairs.add(Integer.parseInt(String.valueOf(characterAsBinary.charAt(i))));
                } else {
                    corruptedPairIndex = i / 2;
                    pairs.add(0);
                }
            }

            if ((pairs.get(0) ^ pairs.get(1) ^ pairs.get(2)) != pairs.get(3)) {
                pairs.set(corruptedPairIndex, 1);
            }

            decodedBinaryString.append(pairs.get(0));
            decodedBinaryString.append(pairs.get(1));
            decodedBinaryString.append(pairs.get(2));

            charAsNumber = inputStream.read();
        }
        inputStream.close();

        for (int i = 0; i < decodedBinaryString.length(); i += 8) {
            int endIndex = Math.min(i + 8, decodedBinaryString.length());
            String binaryByte = decodedBinaryString.substring(i, endIndex);
            binaryByte = addLeadingZerosToBin(binaryByte);

            if (binaryByte.equals("00000000") && endIndex == decodedBinaryString.length()){
                break;
            }

            byte decodedCharAsNumber = (byte) (Integer.parseInt(binaryByte, 2));
            char decodedChar = (char) decodedCharAsNumber;
            charactersToWrite.add(decodedChar);
        }

        OutputStream outputStream = new FileOutputStream("decoded.txt");
        for (Character character : charactersToWrite) {
            outputStream.write(character);
        }
        outputStream.close();
    }

    private static String corruptBinaryByte(String characterAsBinary) {
        int randomIndex = (int) (Math.random() * characterAsBinary.length());
        StringBuilder sb = new StringBuilder(characterAsBinary);
        char newChar = sb.charAt(randomIndex) == '1' ? '0' : '1';
        sb.setCharAt(randomIndex, newChar);
        return sb.toString();
    }

    private static String addLeadingZerosToBin(String binary) {
        return addLeadingZeros(binary, 8);
    }

    private static String addLeadingZerosToHex(String hex) {
        return addLeadingZeros(hex, 2);
    }

    private static String addLeadingZeros(String str, int wantedStrLength) {
        StringBuilder binaryBuilder = new StringBuilder(str);
        while (binaryBuilder.length() < wantedStrLength) {
            binaryBuilder.insert(0, "0");
        }
        str = binaryBuilder.substring(binaryBuilder.length() - wantedStrLength, binaryBuilder.length());
        return str;
    }
}
