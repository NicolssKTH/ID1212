package server.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileHandler {
    private static final String root = "./remoteFiles";
    private static Path workingDir = Paths.get(root);

    public static byte[] getFile(String name) throws IOException{
        Path path = workingDir.resolve(Paths.get(name));
        return Files.readAllBytes(path);
    }

    public static void persistFile(String name, byte[] data)throws IOException{
        Path path = workingDir.resolve(Paths.get(name));
        Files.write(path, data);
    }

    public static void deleteFile(String name) throws IOException{
        Path path = workingDir.resolve(Paths.get(name));
        Files.deleteIfExists(path);
    }
}
