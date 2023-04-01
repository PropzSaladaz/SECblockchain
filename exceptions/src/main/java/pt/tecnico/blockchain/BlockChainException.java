package pt.tecnico.blockchain;
import pt.tecnico.blockchain.ErrorMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockChainException extends RuntimeException {
    private static final Logger logger = LoggerFactory.getLogger(BlockChainException.class);

    private final ErrorMessage errorMessage;

    public BlockChainException(ErrorMessage errorMessage) {
        super(errorMessage.label);
        logger.error(errorMessage.label);
        this.errorMessage = errorMessage;
    }

    public BlockChainException(ErrorMessage errorMessage, Object... value) {
        super(String.format(errorMessage.label, value));
        logger.error(String.format(errorMessage.label, value));
        this.errorMessage = errorMessage;
    }
}
