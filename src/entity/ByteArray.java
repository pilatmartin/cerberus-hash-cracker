package entity;

import java.util.Arrays;

public class ByteArray {
    private byte[] hash;

    public  ByteArray(byte[] hash){
        this.hash = hash;
    }

    public byte[] getHash() {
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ByteArray byteArray = (ByteArray) o;
        return Arrays.equals(hash, byteArray.hash);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(hash);
    }
}
