import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import net.fabricmc.api.ModInitializer;

public final class EntrypointSmokeTest {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new IllegalArgumentException("Usage: EntrypointSmokeTest <mod.jar>");
        }

        Path jar = Path.of(args[0]);
        try (URLClassLoader loader = new URLClassLoader(new URL[] { jar.toUri().toURL() }, EntrypointSmokeTest.class.getClassLoader())) {
            run(loader, "com.example.target.TargetMod");
            run(loader, "com.example.donor.DonorInjectedMod");
        }
    }

    private static void run(ClassLoader loader, String className) throws Exception {
        Class<?> type = Class.forName(className, true, loader);
        Object instance = type.getDeclaredConstructor().newInstance();
        if (!(instance instanceof ModInitializer initializer)) {
            throw new IllegalStateException(className + " is not a ModInitializer");
        }
        initializer.onInitialize();
    }
}
