package entity;

import java.io.Serializable;
import java.util.Arrays;

// ByteArray is just wrapper for byte[]
public class ByteArray implements Serializable {

    private byte[] hash;

    // constructor to create hash
    public  ByteArray(byte[] hash){
        this.hash = hash;
    }

    // getter, we don't need to change bytearray after creation so no setter
    public byte[] getHash() {
        return hash;
    }

    // we need to override equals and hashcode functions so we can compare our byte arrays
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ByteArray byteArray = (ByteArray) o;
        return Arrays.equals(hash, byteArray.hash);
    }

    @Override public int hashCode() {
        return Arrays.hashCode(hash);
    }
}
