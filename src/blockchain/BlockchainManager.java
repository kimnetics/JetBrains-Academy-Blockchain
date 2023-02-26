package blockchain;

import blockchain.Data.Block;
import blockchain.Data.BlockData;
import blockchain.Data.Transaction;
import blockchain.Utils.Serialization;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlockchainManager {
    public static final String ADD_TRANSACTION_ERROR_INVALID_TRANSACTION_ID = "Invalid transaction id";
    public static final String ADD_TRANSACTION_ERROR_INVALID_TRANSACTION_SIGNATURE = "Invalid transaction signature";
    public static final String ADD_TRANSACTION_ERROR_INSUFFICIENT_BALANCE = "Insufficient balance to cover amount";

    private static final LinkedHashSet<Block> blockChain = new LinkedHashSet<>();
    private static final int DESIRED_SOLUTION_SECONDS = 2;
    private static final int DESIRED_SOLUTION_PLUS_MINUS = 1;
    private static final int MAXIMUM_NUMBER_OF_ZEROS = 4;

    private static final AtomicLong transactionId = new AtomicLong(0);
    private static long highestAcceptedTransactionId = 0;

    private static final AtomicInteger targetNumberOfZeros = new AtomicInteger(0);
    private static final ArrayList<Transaction> transactionQueue = new ArrayList<>();
    private static final String NO_PREVIOUS_HASH = "0";
    private static String previousHash = NO_PREVIOUS_HASH;
    private static long blockId = 0;
    private static final AtomicReference<BlockData> blockDataToMine = new AtomicReference<>(null);

    public BlockchainManager() {
    }

    public static long getNextTransactionId() {
        return transactionId.incrementAndGet();
    }

    public static int getTargetNumberOfZeros() {
        return targetNumberOfZeros.get();
    }

    // Get balance for user.
    public synchronized static BigDecimal getBalance(String userId, boolean includeWaiting) {
        BigDecimal balance = new BigDecimal(Main.INITIAL_BALANCE_AMOUNT);

        // Loop through blockchain.
        for (Block block : blockChain) {
            // Update balance from block transactions.
            balance = updateBalanceFromTransactions(userId, block.blockData().getTransactions(), balance);
        }

        // Include waiting transactions?
        if (includeWaiting) {
            // Update balance from transaction queue.
            balance = updateBalanceFromTransactions(userId, transactionQueue, balance);
        }

        return balance;
    }

    private synchronized static BigDecimal updateBalanceFromTransactions(String userId, ArrayList<Transaction> transactions, BigDecimal balance) {
        // Loop through transactions.
        for (Transaction transaction : transactions) {
            if (transaction.fromUserId().equals(userId)) {
                balance = balance.subtract(transaction.amount());
            } else if (transaction.toUserId().equals(userId)) {
                balance = balance.add(transaction.amount());
            }
        }

        return balance;
    }

    // Add transaction to queue.
    public synchronized static String addTransaction(String transactionAsString, String transactionSignature) {
        // Deserialize transaction.
        Transaction transaction = Serialization.deserializeObject(transactionAsString);

        // Verify transaction id is not less than ones from prior transactions.
        if (transaction.transactionId() < highestAcceptedTransactionId) {
            String errorMessage = ADD_TRANSACTION_ERROR_INVALID_TRANSACTION_ID;
            Main.logger.info(String.format("%s received from user '%s'.", errorMessage, transaction.fromUserId()));
            return errorMessage;
        }

        // Verify transaction signature.
        Optional<Boolean> verified = UserManager.verifyMessageSignature(transactionAsString, transactionSignature, transaction.fromUserId());
        if (verified.isEmpty() || !verified.get()) {
            String errorMessage = ADD_TRANSACTION_ERROR_INVALID_TRANSACTION_SIGNATURE;
            Main.logger.info(String.format("%s received from user '%s'.", errorMessage, transaction.fromUserId()));
            return errorMessage;
        }

        // Verify user has sufficient balance to cover amount.
        BigDecimal balance = getBalance(transaction.fromUserId(), true);
        if (transaction.amount().compareTo(balance) > 0) {
            String errorMessage = ADD_TRANSACTION_ERROR_INSUFFICIENT_BALANCE;
            Main.logger.info(String.format("%s for user '%s'.", errorMessage, transaction.fromUserId()));
            return errorMessage;
        }

        // Add transaction to queue.
        transactionQueue.add(transaction);

        // Update highest accepted transaction id.
        highestAcceptedTransactionId = transaction.transactionId();

        // Is there no block data for miners to work on?
        if (blockDataToMine.get() == null) {
            giveBlockDataToMiners();
        }

        return null;
    }

    // Give next block data to miners.
    private synchronized static void giveBlockDataToMiners() {
        // Are we on first block?
        ArrayList<Transaction> transactions;
        if (previousHash.equals(NO_PREVIOUS_HASH)) {
            // First block has no transactions.
            transactions = new ArrayList<>();
        } else {
            // Transfer transaction queue items to list.
            transactions = new ArrayList<>(transactionQueue);
            transactionQueue.clear();
        }

        // Prepare block data.
        blockId++;
        var blockData = new BlockData(previousHash, blockId, transactions);

        // Make block data available to miners.
        blockDataToMine.set(blockData);
    }

    public static BlockData getBlockDataToMine() {
        return blockDataToMine.get();
    }

    // Submit finished block for adding to blockchain.
    public synchronized static boolean submitFinishedBlock(Block block, String minerUserId, long durationSeconds) {
        // Do not accept more than required blocks to not confuse testing robot.
        if (block.blockData().getBlockId() > Main.NUMBER_OF_BLOCKS_FOR_TESTING) {
            blockDataToMine.set(null);
            return false;
        }

        // Was block already added?
        if (blockDataToMine.get() == null) {
            return false;
        }

        // Is block invalid?
        if (isBlockInvalid(block, previousHash, true)) {
            return false;
        }

        // Add block to blockchain.
        blockChain.add(block);
        previousHash = block.hash();

        // Print block information.
        printInformation(block, minerUserId, durationSeconds);

        // Adjust proof of work complexity based on time for solution.
        if (durationSeconds < (DESIRED_SOLUTION_SECONDS - DESIRED_SOLUTION_PLUS_MINUS)) {
            if (targetNumberOfZeros.get() >= MAXIMUM_NUMBER_OF_ZEROS) {
                Main.logger.console("N stays the same\n");
            } else {
                targetNumberOfZeros.incrementAndGet();
                Main.logger.console(String.format("N was increased to %d\n", targetNumberOfZeros.get()));
            }
        } else if (durationSeconds > (DESIRED_SOLUTION_SECONDS + DESIRED_SOLUTION_PLUS_MINUS)) {
            targetNumberOfZeros.decrementAndGet();
            Main.logger.console("N was decreased by 1\n");
        } else {
            Main.logger.console("N stays the same\n");
        }

        // Are there more transactions in queue?
        if (!transactionQueue.isEmpty()) {
            giveBlockDataToMiners();
        } else {
            blockDataToMine.set(null);
        }

        return true;
    }

    // Is block invalid?
    private static boolean isBlockInvalid(Block block, String previousHash, boolean checkProofOfWork) {
        final Pattern leadingZeroPattern = Pattern.compile("^(0+)");

        // Does previous block hash not match?
        if (!block.blockData().getPreviousHash().equals(previousHash)) {
            return true;
        }

        // Does block data hash not match block hash?
        String hash = hashBlockData(block.blockData());
        if (!hash.equals(block.hash())) {
            return true;
        }

        // Do we not need to check proof of work?
        if (!checkProofOfWork) {
            return false;
        }

        // Was proof of work not satisfied?
        Matcher leadingZeroMatcher = leadingZeroPattern.matcher(hash);
        if (leadingZeroMatcher.find()) {
            int leadingZeroCount = leadingZeroMatcher.group(1).length();
            return leadingZeroCount < targetNumberOfZeros.get();
        } else {
            return true;
        }
    }

    // Print block information.
    private static void printInformation(Block block, String minerUserId, long durationSeconds) {
        var blockData = block.blockData();
        String hash = block.hash();
        Main.logger.console("Block:");
        Main.logger.console(String.format("Created by: %s", minerUserId));
        Main.logger.console(String.format("%s gets %d VC", minerUserId, Main.MINER_REWARD_AMOUNT));
        Main.logger.console(String.format("Id: %d", blockData.getBlockId()));
        Main.logger.console(String.format("Timestamp: %d", blockData.getTimeStamp()));
        Main.logger.console(String.format("Magic number: %d", blockData.getNonce()));
        Main.logger.console("Hash of the previous block:");
        Main.logger.console(blockData.getPreviousHash());
        Main.logger.console("Hash of the block:");
        Main.logger.console(hash);
        Main.logger.console("Block data:");
        if (blockData.getTransactions().size() <= 1) {
            Main.logger.console("No transactions");
        } else {
            blockData.getTransactions().stream()
                    .filter(t -> !t.fromUserId().equals(UserManager.BLOCKCHAIN_USER_ID))
                    .forEach(t -> Main.logger.console(t.toString()));
        }
        Main.logger.console(String.format("Block was generating for %d seconds", durationSeconds));
    }

    // Hash block data.
    public static String hashBlockData(BlockData blockData) {
        String blockDataString = Serialization.serializeObject(blockData);
        return Serialization.hashString(blockDataString);
    }

    // Get validation errors for blockchain.
    public synchronized static String getValidationErrors() {
        var results = new StringBuilder();

        // Loop through blockchain.
        String previousHash = "0";
        for (Block block : blockChain) {
            // Is block invalid?
            if (isBlockInvalid(block, previousHash, false)) {
                results.append(String.format("%s has a hash problem.\n", getBlockIdentifier(block)));
            }
            previousHash = block.hash();
        }

        // Loop through blockchain.
        long previousHighestTransactionId = 0;
        for (Block block : blockChain) {
            // Does block have a transaction with a smaller transaction id than highest from prior block?
            for (Transaction transaction : block.blockData().getTransactions()) {
                if (transaction.transactionId() < previousHighestTransactionId) {
                    results.append(String.format("%s has a transaction id problem.\n", getBlockIdentifier(block)));
                    break;
                }
            }
            previousHighestTransactionId = block.getHighestTransactionId();
        }

        return results.toString();
    }

    private static String getBlockIdentifier(Block block) {
        return String.format("Block with id %s", block.blockData().getBlockId());
    }
}
