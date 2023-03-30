package pt.tecnico.blockchain.server;

import pt.tecnico.blockchain.Messages.blockchain.BlockchainTransaction;

import java.util.*;

public class SynchronizedTransactionPool {

    private final int MINIMUM_TRANSACTIONS = 5;
    private List<BlockchainTransaction> pool;
    private final Set<String> transactionIDs;

    public SynchronizedTransactionPool() {
        pool = new ArrayList<>();
        transactionIDs = new HashSet<>();
    }

    public void addTransactionIfNotInPool(BlockchainTransaction txn) {
        synchronized (this) {
            if (!transactionIDs.contains(txn.getTransactionID())) {
                transactionIDs.add(txn.getTransactionID());
                pool.add(txn);
            }
        }
    }

    public List<BlockchainTransaction> getTransactionsIfHasEnough() {
        synchronized (this) {
            if (transactionIDs.size() >= MINIMUM_TRANSACTIONS) {
                return popHighestPaidTransactions();
            }
            else return new ArrayList<>();
        }
    }

    private List<BlockchainTransaction> popHighestPaidTransactions() {
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
