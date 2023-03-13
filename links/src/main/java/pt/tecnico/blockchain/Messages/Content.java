package pt.tecnico.blockchain.Messages;

public interface Content {

    default public String getContentType() {
        return ContentType.WRAPPER_CONTENT;
    }
}


