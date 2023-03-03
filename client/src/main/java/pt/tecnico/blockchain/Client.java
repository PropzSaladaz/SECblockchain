package pt.tecnico.blockchain;

import pt.tecnico.blockchain.KeyGenerate;

import javax.crypto.SecretKey;

/**
 * Hello world!
 *
 */
public class Client
{
    public static void main( String[] args ) throws Exception
    {
        SecretKey key = KeyGenerate.generateKey("alibaba");
        System.out.println( "Worked!" );
    }
}
