import java.io.CharArrayWriter;
import java.io.IOException;

class Converter {
    public static char[] convert(String[] words) throws IOException {
        CharArrayWriter charArrayWriter = new CharArrayWriter();

        for (String word : words) {
            charArrayWriter.write(word);
        }

        charArrayWriter.close();
        return charArrayWriter.toCharArray();
    }
}