package pt.tecnico.blockchain.Messages;

import pt.tecnico.blockchain.Messages.ibft.ContentType;

import java.io.Serializable;

public interface Content extends Serializable {
    default String getContentType() {
        return ContentType.WRAPPER_CONTENT;
    }
    
    String toString(int tabs);

    default String toStringWithTabs(String str, int numTabs) {
        String tabs = "\t".repeat(numTabs);
        return tabs + str + "\n";
    }
}


