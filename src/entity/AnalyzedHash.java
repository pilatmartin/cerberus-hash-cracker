package entity;

import java.io.Serializable;

// analyzed hash inherits everything from LoadedHash and add algorithm variable
public class AnalyzedHash extends LoadedHash implements Serializable {

    private String algorithm;

    // constructor
    public AnalyzedHash(String hexString, String algorithm) {
        super(hexString);
        this.algorithm = algorithm;
    }

    // getter setter
    public String getAlgorithm() {
        return algorithm;
    }
    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }
}
