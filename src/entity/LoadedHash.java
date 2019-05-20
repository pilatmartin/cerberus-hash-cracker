package entity;

import java.io.Serializable;
import java.util.Objects;

// loaded hash just need to know hexString
public class LoadedHash implements Serializable {

    private String hexString;

    public LoadedHash(String hexString) {
        this.hexString = hexString;
    }

    public String getHexString() {
        return hexString;
    }
    public void setHexString(String hexString) {
        this.hexString = hexString;
    }

    // toString returns hexString now
    @Override public String toString() {
        return hexString;
    }

    // we need to override equals and hashcode functions so we can compare hashes
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoadedHash hash = (LoadedHash) o;
        return Objects.equals(hexString, hash.hexString);
    }

    @Override public int hashCode() {
        return Objects.hash(hexString);
    }
}
