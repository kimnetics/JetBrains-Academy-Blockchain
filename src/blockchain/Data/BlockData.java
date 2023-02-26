package blockchain.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class BlockData implements Cloneable, Serializable {
    @Serial
    private static final long serialVersionUID = 2L;
    private final String previousHash; // From blockchain.
    private long timeStamp; // From miners.
    private long nonce; // From miners.

    // Block data fields.
    private final long blockId; // From blockchain.
    private ArrayList<Transaction> transactions; // From users.

    public BlockData(String previousHash, long blockId, ArrayList<Transaction> transactions) {
        this.previousHash = previousHash;
        this.blockId = blockId;
        this.transactions = transactions;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public long getNonce() {
        return nonce;
    }

    public void setNonce(long nonce) {
        this.nonce = nonce;
    }

    public long getBlockId() {
        return blockId;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    @Override
    public Object clone() {
        try {
            var blockData = (BlockData) super.clone();
            blockData.transactions = new ArrayList<>(blockData.transactions);
            return blockData;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Unexpected error cloning block data. " + e.getMessage());
        }
    }
}
