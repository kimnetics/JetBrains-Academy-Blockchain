package blockchain.Data;

import blockchain.UserManager;

public record Block(
        BlockData blockData,
        String hash,
        String minerUserId) {

    public long getHighestTransactionId() {
        return blockData.getTransactions().stream()
                .filter(t -> !t.fromUserId().equals(UserManager.BLOCKCHAIN_USER_ID))
                .map(Transaction::transactionId)
                .max(Long::compare).orElse(0L);
    }
}
