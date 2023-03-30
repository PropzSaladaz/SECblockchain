package pt.tecnico.blockchain.server;

import pt.tecnico.blockchain.Messages.blockchain.BlockchainTransaction;

import java.util.*;

public class SynchronizedTransactionPool {

    private static final int MINIMUM_TRANSACTIONS = 5;
    private static List<BlockchainTransaction> pool = new ArrayList<>();
    private static final Set<String> transactionIDs = new HashSet<>();
    private static final Object _poolLock = new Object();

    public static void addTransactionIfNotInPool(BlockchainTransaction txn) {
        synchronized (_poolLock) {
            if (!transactionIDs.contains(txn.getTransactionID())) {
                transactionIDs.add(txn.getTransactionID());
                pool.add(txn);
            }
        }
    }

    public static List<BlockchainTransaction> getTransactionsIfHasEnough() {
        synchronized (_poolLock) {
            if (transactionIDs.size() >= MINIMUM_TRANSACTIONS) {
                return popHighestPaidTransactions();
            }
            else return new ArrayList<>();
        }
    }

    private static List<BlockchainTransaction> popHighestPaidTransactions() {
        pool.sort(Comparator.comparingInt(BlockchainTransaction::getGasPrice));
        List<BlockchainTransaction> txns = new ArrayList<>();
        Iterator<BlockchainTransaction> iter = pool.listIterator();
        for (int i = 0 ; i < MINIMUM_TRANSACTIONS && iter.hasNext(); i++) {
            BlockchainTransaction txn = iter.next();
            txns.add(txn);
            transactionIDs.remove(txn.getTransactionID());
            iter.remove();
        }
        return txns;
    }

}
