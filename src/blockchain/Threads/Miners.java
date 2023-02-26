package blockchain.Threads;

import blockchain.BlockchainManager;
import blockchain.Data.Block;
import blockchain.Data.BlockData;
import blockchain.Data.Transaction;
import blockchain.Main;
import blockchain.UserManager;
import blockchain.Utils.Logger;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Miners {
    private static Logger logger;
    private static int numberOfMiners;
    private static ExecutorService executor;

    public Miners() {
    }

    public static void setLogger(Logger logger) {
        Miners.logger = logger;
    }

    public static void setNumberOfMiners(int numberOfMiners) {
        Miners.numberOfMiners = numberOfMiners;
    }

    // Start miners.
    public static ArrayList<String> startMiners() {
        ArrayList<String> minerUserIds = new ArrayList<>();

        executor = Executors.newFixedThreadPool(numberOfMiners);
        for (int i = 1; i <= numberOfMiners; i++) {
            String minerUserId = "Miner" + i;
            executor.execute(new MinerThread(minerUserId));
            minerUserIds.add(minerUserId);
        }

        return minerUserIds;
    }

    // Stop miners.
    public static void stopMiners() {
        executor.shutdown();
        while (!executor.isTerminated()) {
            // Sleep for a while.
            try {
                TimeUnit.MILLISECONDS.sleep(200L);
            } catch (InterruptedException ignored) {
            }
        }
    }

    private static class MinerThread implements Runnable {
        private final Pattern leadingZeroPattern = Pattern.compile("^(0+)");
        private final String minerUserId;

        public MinerThread(String minerUserId) {
            this.minerUserId = minerUserId;
        }

        @Override
        public void run() {
            logger.info(String.format("Miner %s started.", minerUserId));

            long currentBlockId = -1;
            Instant start = null;
            BlockData blockDataLocal = null;

            // Loop until shutdown.
            while (!executor.isShutdown()) {
                // Is there block data to mine?
                var blockData = BlockchainManager.getBlockDataToMine();
                if (blockData != null) {
                    // Are we on a new block?
                    if (blockData.getBlockId() != currentBlockId) {
                        // Save start time.
                        start = Instant.now();
                        currentBlockId = blockData.getBlockId();

                        // Make clone of block data to manipulate.
                        blockDataLocal = (BlockData) blockData.clone();

                        // Add reward transaction for us.
                        var transactions = blockDataLocal.getTransactions();
                        var rewardTransaction = new Transaction(BlockchainManager.getNextTransactionId(), UserManager.BLOCKCHAIN_USER_ID, minerUserId, new BigDecimal(Main.MINER_REWARD_AMOUNT));
                        transactions.add(rewardTransaction);
                    }

                    // Try a random nonce to satisfy proof of work.
                    assert blockDataLocal != null;
                    tryANonce(blockDataLocal, start);
                } else {
                    // Sleep for a while.
                    try {
                        TimeUnit.MILLISECONDS.sleep(200L);
                    } catch (InterruptedException ignored) {
                    }
                }
            }

            logger.info(String.format("Miner %s stopped.", minerUserId));
        }

        // Try a random nonce to satisfy proof of work.
        private void tryANonce(BlockData blockData, Instant start) {
            // Get target number of zeros.
            int targetNumberOfZeros = BlockchainManager.getTargetNumberOfZeros();

            // Set timestamp.
            blockData.setTimeStamp(new Date().getTime());

            // Set nonce.
            blockData.setNonce((long) (Math.random() * 1_000_000_000L));

            // Hash block data.
            String hash = BlockchainManager.hashBlockData(blockData);

            // Did we satisfy proof of work?
            Matcher leadingZeroMatcher = leadingZeroPattern.matcher(hash);
            if (leadingZeroMatcher.find()) {
                int leadingZeroCount = leadingZeroMatcher.group(1).length();
                if (leadingZeroCount >= targetNumberOfZeros) {
                    // Calculate time taken to solve.
                    Instant end = Instant.now();
                    long durationSeconds = Duration.between(start, end).getSeconds();
                    // Submit finished block for adding to blockchain.
                    BlockchainManager.submitFinishedBlock(new Block(blockData, hash, minerUserId), minerUserId, durationSeconds);
                }
            }
        }
    }
}
