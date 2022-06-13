package correcter;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class Main {
    public static void main(String[] args) throws IOException {
        FileReader reader = new FileReader("send.txt");
        FileWriter writer = new FileWriter("received.txt");

        int charAsNumber = reader.read();
        while(charAsNumber != -1) {
            char character = (char) charAsNumber;
            byte characterAsByte = (byte) character;
            characterAsByte ^= 1 << 1;
            writer.write(characterAsByte);

            charAsNumber = reader.read();
        }

        reader.close();
        writer.close();
    }
}
