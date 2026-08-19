import java.io.*;
import java.net.URI;
import java.net.http.*;
import java.nio.file.*;

public class DependencyResolver {

    static final Path LOCAL_REPO = Paths.get(System.getProperty("user.home"), ".dhivanan", "repository");

    public static Path localRepoPath(String groupId, String artifactId, String version) {
        return LOCAL_REPO.resolve(groupId.replace('.', '/')).resolve(artifactId).resolve(version);
    }

    public static Path resolve(Dependency dep) throws Exception {
        Path dir = localRepoPath(dep.groupId, dep.artifactId, dep.version);
        String jarName = dep.artifactId + "-" + dep.version + ".jar";
        Path jarPath = dir.resolve(jarName);

        // 1. Check local cache first
        if (Files.exists(jarPath)) {
            System.out.println("[cache] found locally: " + jarPath);
            return jarPath;
        }

        // 2. Not cached -> fetch from GitHub
        System.out.println("[cache] miss, fetching from GitHub: " + dep);
        Files.createDirectories(dir);

        String githubBase = System.getenv().getOrDefault(
            "DHIVANAN_GITHUB_BASE",
            "https://raw.githubusercontent.com/YOUR_USER/YOUR_REPO/main/repo"
        );
        String url = githubBase + "/" + dep.groupId.replace('.', '/') + "/"
                + dep.artifactId + "/" + dep.version + "/" + jarName;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET().build();
        HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

        if (response.statusCode() != 200) {
            throw new IOException("Failed to download " + url + " (status " + response.statusCode() + ")");
        }

        Files.copy(response.body(), jarPath, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("[github] downloaded -> " + jarPath);
        return jarPath;
    }
}
