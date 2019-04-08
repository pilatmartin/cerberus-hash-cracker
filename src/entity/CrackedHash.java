package entity;

import java.io.Serializable;

public class CrackedHash extends LoadedHash implements Serializable {

    private String password;

    public CrackedHash(String hexString, String password) {
        super(hexString);
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
