package tools;

import main.MainController;

public class Generator {

    private MainController mainController;
    private char[] charset;

    public Generator(MainController mainController, String charset) {
        this.mainController = mainController;
        this.charset = charset.toCharArray();
    }

    public void generate(String str, int pos, int length)
    {
        if (length == 0) {
            System.out.println(str);
        } else {
            if (pos != 0) {
                pos = 0;
            }
            for (int i = pos; i < charset.length; i++) {
                generate(str + charset[i], i, length - 1);
            }
        }
    }

}