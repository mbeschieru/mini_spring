package framework.context;

import framework.annotations.Component;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Objects;

public class ScannerAPI {

    private final BeanRegistry registry;

    public ScannerAPI(final BeanRegistry registry) {
        this.registry = registry;
    }

    public void scan(final String basePackage) {
        final String path = basePackage.replace('.', '/');
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        try {
            final Enumeration<URL> resources = classLoader.getResources(path);
            while (resources.hasMoreElements()) {
                final URL resource = resources.nextElement();
                final File directory = new File(resource.toURI());
                scanDirectory(basePackage, directory);
            }
        } catch (final IOException e) {
            throw new RuntimeException("Failed to scan package");
        } catch (final URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private void scanDirectory(final String basePackage, final File directory) {
        for (File file: Objects.requireNonNull(directory.listFiles())) {
            if (file.isDirectory()) {
                final String nextPath = buildNextPathForDirectory(basePackage, file);
                scanDirectory(nextPath, file);
                continue;
            }
            if (!file.getName().endsWith(".class")) {
                continue;
            }

            final String className = basePackage + "." + file.getName().replace(".class", "");

            try {
                final Class<?> clasz = Class.forName(className);
                if (clasz.isAnnotationPresent(Component.class)) {
                    this.registry.registerBean(clasz);
                }
            } catch (final ClassNotFoundException exception) {
                throw new RuntimeException("Failed to load class " + className, exception);
            }
        }
    }

    private String buildNextPathForDirectory(final String basePackage, final File file) {
        return basePackage + '.' + file.getName();
    }
}
