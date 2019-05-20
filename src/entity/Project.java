package entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// this object is implementing serializable so we can write him into a file
public class Project implements Serializable {

    // loaded hashes
    private Set<LoadedHash> loadedHashes = new HashSet<>();

    /*
        map is used for faster access to hashes, when hashing we need to check
        if actual hashed password is contained in analyzed hashes

        if set was used and we would have 35000 hashes to crack we will need to
        check every time all items of set, that means O(35000)

        if map was used with 35000 hashes to crack
        map search time is always O(1) and it does not matter on size of map

        O(35000) > O(1)
        set      > map
        map uses less time
    */
    private Map<ByteArray,AnalyzedHash> analyzedHashes = new HashMap<>();

    // cracked hashes
    private Set<CrackedHash> crackedHashes = new HashSet<>();

    // empty constructor
    public Project(){ }

    // getters, setters, adders for loaded hashes
    public Set<LoadedHash> getLoadedHashes() {
        return loadedHashes;
    }
    public void setLoadedHashes(Set<LoadedHash> loadedHashes) {
        this.loadedHashes = loadedHashes;
    }
    public void addLoadedHash(LoadedHash loadedHash) {
        this.loadedHashes.add(loadedHash);
    }

    // getters, setters, adders for analyzed hashes
    public Map<ByteArray, AnalyzedHash> getAnalyzedHashes() {
        return analyzedHashes;
    }
    public void setAnalyzedHashes(Map<ByteArray, AnalyzedHash> analyzedHashes) {
        this.analyzedHashes = analyzedHashes;
    }
    public void addAnalyzedHash(ByteArray key, AnalyzedHash analyzedHash) {
        this.analyzedHashes.put(key, analyzedHash);
    }

    // getters, setters, adders for cracked hashes
    public Set<CrackedHash> getCrackedHashes() {
        return crackedHashes;
    }
    public void setCrackedHashes(Set<CrackedHash> crackedHashes) {
        this.crackedHashes = crackedHashes;
    }
    public void addCrackedHash(CrackedHash crackedHash) {
        this.crackedHashes.add(crackedHash);
    }
}
