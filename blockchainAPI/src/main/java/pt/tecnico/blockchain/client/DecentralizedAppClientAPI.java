package pt.tecnico.blockchain.client;

import pt.tecnico.blockchain.Messages.Content;

public interface DecentralizedAppClientAPI {
    void deliver(Content message);
}
