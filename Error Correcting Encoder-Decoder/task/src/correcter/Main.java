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
        ArrayList<Character> bits = new ArrayList<>();

        for (int i = 0; i < rawBinary.length(); i++) {
            char c = rawBinary.charAt(i);

            bits.add(c);
            if (bits.size() == 4) {
                String bitsParityView = getParityView(bits);

                expandView.append(getExpandView(bits)).append(" ");
                parityView.append(bitsParityView).append(" ");

                int number = Integer.parseInt(bitsParityView.replace(".", "0"), 2);
                String hexCode = addLeadingZerosToHex(Integer.toHexString(number));
                parityHexView.append(hexCode.toUpperCase()).append(" ");

                bits.clear();
            }
        }

        while (bits.size() > 0 && bits.size() < 4) {
            bits.add('0');
        }

        if (bits.size() == 4) {
            String bitsParityView = getParityView(bits);

            expandView.append(getExpandView(bits));
            parityView.append(bitsParityView);

            int number = Integer.parseInt(bitsParityView.replace(".", "0"), 2);
            String hexCode = addLeadingZerosToHex(Integer.toHexString(number));
            parityHexView.append(hexCode.toUpperCase());
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

            int corruptedBitIndex = getCorruptedBitIndex(characterAsBinary);

            // Fix corrupted character
            StringBuilder fixedCharacterAsBinary = new StringBuilder(characterAsBinary);
            fixedCharacterAsBinary.setCharAt(corruptedBitIndex, fixedCharacterAsBinary.charAt(corruptedBitIndex) == '1' ? '0' : '1');

            decodedBinaryString.append(fixedCharacterAsBinary.charAt(2));
            decodedBinaryString.append(fixedCharacterAsBinary.charAt(4));
            decodedBinaryString.append(fixedCharacterAsBinary.charAt(5));
            decodedBinaryString.append(fixedCharacterAsBinary.charAt(6));

            charAsNumber = inputStream.read();
        }
        inputStream.close();

        for (int i = 0; i < decodedBinaryString.length(); i += 8) {
            int endIndex = Math.min(i + 8, decodedBinaryString.length());
            String binaryByte = decodedBinaryString.substring(i, endIndex);
            binaryByte = addLeadingZerosToBin(binaryByte);

            if (binaryByte.equals("00000000") && endIndex == decodedBinaryString.length()) {
                break;
            }

            byte decodedCharAsNumber = (byte) (Integer.parseInt(binaryByte, 2));
            char decodedChar = (char) decodedCharAsNumber;
            charactersToWrite.add(decodedChar);
        }

        System.out.println(charactersToWrite.toString());
        OutputStream outputStream = new FileOutputStream("decoded.txt");
        for (Character character : charactersToWrite) {
            outputStream.write(character);
        }
        outputStream.close();
    }

    private static int getCorruptedBitIndex(String bits) {
        if (bits.charAt(7) == '1') {
            return 7;
        }

        int correctFirstParity = countOnes(new char[]{
                bits.charAt(2),
                bits.charAt(4),
                bits.charAt(6)
        });

        int correctSecondParity = countOnes(new char[]{
                bits.charAt(2),
                bits.charAt(5),
                bits.charAt(6)
        });

        int correctThirdParity = countOnes(new char[]{
                bits.charAt(4),
                bits.charAt(5),
                bits.charAt(6)
        });

        boolean isFirstParityCorrect = bits.charAt(0) == (correctFirstParity % 2 == 1 ? '1' : '0');
        boolean isSecondParityCorrect = bits.charAt(1) == (correctSecondParity % 2 == 1 ? '1' : '0');
        boolean isThirdParityCorrect = bits.charAt(3) == (correctThirdParity % 2 == 1 ? '1' : '0');

        if (!isFirstParityCorrect && isSecondParityCorrect && isThirdParityCorrect) {
            return 0;
        } else if (isFirstParityCorrect && !isSecondParityCorrect && isThirdParityCorrect) {
            return 1;
        } else if (!isFirstParityCorrect && !isSecondParityCorrect && isThirdParityCorrect) {
            return 2;
        } else if (isFirstParityCorrect && isSecondParityCorrect && !isThirdParityCorrect) {
            return 3;
        } else if (!isFirstParityCorrect && isSecondParityCorrect && !isThirdParityCorrect) {
            return 4;
        } else if (isFirstParityCorrect && !isSecondParityCorrect && !isThirdParityCorrect) {
            return 5;
        } else {
            return 6;
        }
    }

    private static int countOnes(char[] bitArray) {
        int counter = 0;
        for (char bit : bitArray) {
            if (bit == '1') {
                counter++;
            }
        }
        return counter;
    }

    private static String getExpandView(ArrayList<Character> bits) {
        StringBuilder sb = new StringBuilder("........");
        sb.setCharAt(2, bits.get(0));
        sb.setCharAt(4, bits.get(1));
        sb.setCharAt(5, bits.get(2));
        sb.setCharAt(6, bits.get(3));
        sb.setCharAt(7, '.');
        return sb.toString();
    }

    private static String getParityView(ArrayList<Character> bits) {
        StringBuilder parityView = new StringBuilder(getExpandView(bits));
        parityView.setCharAt(7, '0');

        int amountOfOnesForFirstParity = countOnes(new char[]{
                parityView.charAt(2),
                parityView.charAt(4),
                parityView.charAt(6)
        });

        int amountOfOnesForSecondParity = countOnes(new char[]{
                parityView.charAt(2),
                parityView.charAt(5),
                parityView.charAt(6)
        });

        int amountOfOnesForThirdParity = countOnes(new char[]{
                parityView.charAt(4),
                parityView.charAt(5),
                parityView.charAt(6)
        });

        char firstParityBit = amountOfOnesForFirstParity % 2 == 1 ? '1' : '0';
        char secondParityBit = amountOfOnesForSecondParity % 2 == 1 ? '1' : '0';
        char thirdParityBit = amountOfOnesForThirdParity % 2 == 1 ? '1' : '0';

        parityView.setCharAt(0, firstParityBit);
        parityView.setCharAt(1, secondParityBit);
        parityView.setCharAt(3, thirdParityBit);

        return parityView.toString();
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
