package cracker.entity;

public class CrackedHash extends LoadedHash {

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
