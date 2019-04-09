package entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Project implements Serializable {

    private Set<LoadedHash> loadedHashes = new HashSet<>();
    private Map<ByteArray,AnalyzedHash> analyzedHashes = new HashMap<>();
    private Set<CrackedHash> crackedHashes = new HashSet<>();

    public Project(){ }

    public Set<LoadedHash> getLoadedHashes() {
        return loadedHashes;
    }
    public void setLoadedHashes(Set<LoadedHash> loadedHashes) {
        this.loadedHashes = loadedHashes;
    }
    public void addLoadedHash(LoadedHash loadedHash) {
        this.loadedHashes.add(loadedHash);
    }

    public Map<ByteArray, AnalyzedHash> getAnalyzedHashes() {
        return analyzedHashes;
    }
    public void setAnalyzedHashes(Map<ByteArray, AnalyzedHash> analyzedHashes) {
        this.analyzedHashes = analyzedHashes;
    }
    public void addAnalyzedHash(ByteArray key, AnalyzedHash analyzedHash) {
        this.analyzedHashes.put(key, analyzedHash);
    }

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
