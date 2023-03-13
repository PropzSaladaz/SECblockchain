package pt.tecnico.blockchain.Messages;

import java.io.Serializable;

public interface Content extends Serializable {
    public String toString(int tabs);

    public default String toStringWithTabs(String str, int numTabs) {
        String tabs = "\t".repeat(numTabs);
        return tabs + str + "\n";
    }
}


