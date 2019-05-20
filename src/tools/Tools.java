package tools;

import javafx.scene.control.Alert;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// Tools class contains static methods that can be used everywhere in our project
public class Tools {

    /*
        open info window, most likely that you aren't using full version
        you can set title and body text
     */
    public static void openInfoWindow(String title, String text){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.show();
    }

    /*
        take string password and create byte array of hash of this password
     */
    public static byte[] hash(String password, String algorithm){
        try {
            byte[] inputBytes = Tools.stringToByteArray(password);

            MessageDigest digest = MessageDigest.getInstance(algorithm);

            return digest.digest(inputBytes);

        }catch (NoSuchAlgorithmException e){
            e.printStackTrace();
            return null;
        }
    }

    /*
        DO NOT TRY TO UNDERSTAND ACTUAL CODE
        open file and count all lines
        every time reads 1024 chars and count how many '\n' are there
        repeat to end of a file
        final count is number of lines in file
     */
    public static long countLines(File filename){

        // copy&paste code JUST IGNORE THIS 20 LINES
        try {
            InputStream is = new BufferedInputStream(new FileInputStream(filename.getAbsolutePath()));
            byte[] c = new byte[1024];
            int readChars = is.read(c);
            if (readChars == -1) return 0;
            long count = 0;
            while (readChars == 1024) {
                for (int i=0; i<1024;) if (c[i++] == '\n') ++count;
                readChars = is.read(c);
            }
            while (readChars != -1) {
                for (int i=0; i<readChars; ++i) if (c[i] == '\n') ++count;
                readChars = is.read(c);
            }
            return count == 0 ? 1 : count;
        } catch (Exception e){
            System.out.println(e);
            return 0;
        }
    }

    /*
        get string and transform it to byte array
        ex:
            "AAB" -> [65, 65, 66]
     */
    public static byte[] stringToByteArray(String string){
        return string.getBytes();
    }

    /*
        get hexString and transform it to byte array
        ex:
            "00ff05" -> [0, 255, 5]
     */
    public static byte[] hexStringToByteArray(String s) {

        // copy&paste code
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    /*
        get hexString and transform it to byte array
        ex:
            [65, 65, 66] -> "AAB"
     */
    public static String byteArrayToString(byte[] byteArray){

        // copy&paste code
        StringBuilder sb = new StringBuilder();
        for (byte b : byteArray)
            sb.append(String.format("%02x", b));

        return sb.toString();
    }

    /*
        take string and based on hash length distinguish type of hash
     */
    public static String distinguishHash(String hash) {
        switch (hash.length()){
            case 32:
                return "MD5";
            case 40:
                return "SHA";
            case 56:
                return "SHA-224";
            case 64:
                return "SHA-256";
            case 96:
                return "SHA-384";
            case 128:
                return "SHA-512";
            default:
                return "Unknown hash";
        }
    }
}
