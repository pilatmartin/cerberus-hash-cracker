package entity;

public class AnalyzedHash extends LoadedHash {

    private String algorithm;

    public AnalyzedHash(String hexString, String algorithm) {
        super(hexString);
        this.algorithm = algorithm;
    }

    public String getAlgorithm() {
        return algorithm;
    }
    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }
}
