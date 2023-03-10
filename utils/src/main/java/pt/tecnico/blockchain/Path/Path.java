package pt.tecnico.blockchain.Path;

import java.io.File;

public interface Path {
    Path getParent();
    Path append(String node);

    String getPath();

    @Override
    String toString();

}
