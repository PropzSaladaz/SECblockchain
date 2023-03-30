package pt.tecnico.blockchain.Messages;

import java.io.Serializable;

public interface Content extends Serializable {
    
    String toString(int tabs);

    boolean equals(Content another);

    default String toStringWithTabs(String str, int numTabs) {
        String tabs = "\t".repeat(numTabs);
        return tabs + str + "\n";
    }
}


