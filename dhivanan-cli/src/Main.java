import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Usage: dhivanan <phase> [phase...]  e.g. dhivanan clean install");
            return;
        }

        ProjectConfig config = YamlParser.parseProjectConfig(Paths.get("dhivanan.yaml"));
        List<String> classpath = new ArrayList<>();

        for (String phase : args) {
            switch (phase) {
                case "clean" -> clean();
                case "compile" -> {
                    if (classpath.isEmpty()) classpath = resolveDependencies(config);
                    compile(classpath);
                }
                case "package" -> pack(config);
                case "install" -> {
                    if (classpath.isEmpty()) classpath = resolveDependencies(config);
                    compile(classpath);
                    pack(config);
                    installLocally(config);
                }
                case "run" -> {
                    if (classpath.isEmpty()) classpath = resolveDependencies(config);
                    run(config, classpath);
                }
                default -> System.out.println("Unknown phase: " + phase);
            }
        }
    }

    static void clean() throws IOException {
        Path target = Paths.get("target");
        if (Files.exists(target)) {
            try (var walk = Files.walk(target)) {
                walk.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete());
            }
        }
        System.out.println("[clean] target/ removed");
    }

    static List<String> resolveDependencies(ProjectConfig config) throws Exception {
        List<String> classpath = new ArrayList<>();
        for (Dependency dep : config.dependencies) {
            Path jarPath = DependencyResolver.resolve(dep);
            classpath.add(jarPath.toString());
        }
        return classpath;
    }

    static void compile(List<String> classpath) throws IOException, InterruptedException {
        Files.createDirectories(Paths.get("target/classes"));
        List<String> sources = new ArrayList<>();
        try (var walk = Files.walk(Paths.get("src"))) {
            walk.filter(p -> p.toString().endsWith(".java")).forEach(p -> sources.add(p.toString()));
        }

        List<String> cmd = new ArrayList<>(List.of("javac", "-d", "target/classes"));
        if (!classpath.isEmpty()) {
            cmd.add("-cp");
            cmd.add(String.join(File.pathSeparator, classpath));
        }
        cmd.addAll(sources);

        Process p = new ProcessBuilder(cmd).inheritIO().start();
        if (p.waitFor() != 0) throw new IOException("compile failed");
        System.out.println("[compile] done");
    }

    static void pack(ProjectConfig config) throws IOException, InterruptedException {
        String jarName = config.artifactId + "-" + config.version + ".jar";
        Process p = new ProcessBuilder("jar", "cf", "target/" + jarName, "-C", "target/classes", ".")
                .inheritIO().start();
        p.waitFor();
        System.out.println("[package] target/" + jarName);
    }

    static void installLocally(ProjectConfig config) throws IOException {
        String jarName = config.artifactId + "-" + config.version + ".jar";
        Path src = Paths.get("target", jarName);
        Path destDir = DependencyResolver.localRepoPath(config.groupId, config.artifactId, config.version);
        Files.createDirectories(destDir);
        Files.copy(src, destDir.resolve(jarName), StandardCopyOption.REPLACE_EXISTING);
        System.out.println("[install] " + destDir.resolve(jarName));
    }

    static void run(ProjectConfig config, List<String> classpath) throws IOException, InterruptedException {
        List<String> cp = new ArrayList<>(classpath);
        cp.add("target/classes");
        List<String> cmd = List.of("java", "-cp", String.join(File.pathSeparator, cp), config.mainClass);
        new ProcessBuilder(cmd).inheritIO().start().waitFor();
    }
}
