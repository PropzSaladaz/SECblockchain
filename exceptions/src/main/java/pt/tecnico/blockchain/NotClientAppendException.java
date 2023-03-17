package pt.tecnico.blockchain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotClientAppendException extends RuntimeException {

    public NotClientAppendException() {
    }

    public void printError(){
       System.out.println("A NOM CLIENT IS TRYING TO UPDATE THE BLOCK\n");
    }
}
