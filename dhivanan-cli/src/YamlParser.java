import java.nio.file.*;
import java.util.*;

public class YamlParser {
    public static ProjectConfig parseProjectConfig(Path path) throws Exception {
        ProjectConfig config = new ProjectConfig();
        List<String> lines = Files.readAllLines(path);
        boolean inDeps = false;

        for (String raw : lines) {
            String line = raw.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;

            if (line.equals("dependencies:")) { inDeps = true; continue; }

            if (inDeps && line.startsWith("- ")) {
                String[] parts = line.substring(2).trim().split(":");
                config.dependencies.add(new Dependency(parts[0], parts[1], parts[2]));
                continue;
            }
            inDeps = false;

            int idx = line.indexOf(":");
            if (idx == -1) continue;
            String key = line.substring(0, idx).trim();
            String value = line.substring(idx + 1).trim();

            switch (key) {
                case "groupId" -> config.groupId = value;
                case "artifactId" -> config.artifactId = value;
                case "version" -> config.version = value;
                case "mainClass" -> config.mainClass = value;
            }
        }
        return config;
    }
}
