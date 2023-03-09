package pt.tecnico.blockchain.Path;

import java.io.File;

import java.nio.file.Paths;

public class ModulePath implements Path {
    private final java.nio.file.Path modulePath;

    public ModulePath() {
        modulePath = Paths.get(new File("").getAbsolutePath());
    }

    public ModulePath(String fullPath) {
        modulePath = Paths.get(fullPath);
    }

    @Override
    public ModulePath getParent() {
        return new ModulePath(modulePath.getParent().toString());
    }

    @Override
    public ModulePath append(String node) {
        String newPath = modulePath + File.separator + node;
        return new ModulePath(new File(newPath).getAbsolutePath());
    }

    @Override
    public String getPath() {
        return toString();
    }

    @Override
    public String toString() {
        return modulePath.toString();
    }
}
