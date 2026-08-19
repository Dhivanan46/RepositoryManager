import java.util.ArrayList;
import java.util.List;

class ProjectConfig {
    String groupId, artifactId, version, mainClass;
    List<Dependency> dependencies = new ArrayList<>();
}

class Dependency {
    String groupId, artifactId, version;
    Dependency(String g, String a, String v) { groupId = g; artifactId = a; version = v; }
    public String toString() { return groupId + ":" + artifactId + ":" + version; }
}
