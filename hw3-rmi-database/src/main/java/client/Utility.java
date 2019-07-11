package client;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Utility {
    private static final String root = "./localFiles";
    private static Path workingDir = Paths.get(root);

    public static void writeFile(String name, byte[] bytes)throws IOException{
        Path file = workingDir.resolve(Paths.get(name));
        Files.write(file, bytes);
    }

    public static byte[] readFile(String name)throws IOException{
        Path file = workingDir.resolve(Paths.get(name));
        System.out.println(file);
        if (!Files.exists(file)){
            throw new FileNotFoundException(file + " not found");
        }
        return Files.readAllBytes(file);
    }
}
