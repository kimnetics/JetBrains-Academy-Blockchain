package blockchain;

import blockchain.Data.BlockData;
import blockchain.Data.User;
import blockchain.Threads.Miners;
import blockchain.Threads.Users;
import blockchain.Utils.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Main {
    public static Logger logger;

    public static final int INITIAL_BALANCE_AMOUNT = 100;
    public static final int MINER_REWARD_AMOUNT = 100;
    public static final int NUMBER_OF_BLOCKS_FOR_TESTING = 15;

    public static void main(String[] args) {
        // Initialize logger.
        logger = new Logger(Main.class.getName(), "%h/blockchain.log");

        logger.info("Blockchain program started.");

        // Start miners.
        Miners.setLogger(logger);
        Miners.setNumberOfMiners(5);
        ArrayList<String> userIds = Miners.startMiners();

        // Start users.
        Users.setLogger(logger);
        Collections.addAll(userIds, "Tom", "Sarah", "Nick");
        Users.startUsers(userIds);

        // Wait until sufficient blocks are done to satisfy testing robot.
        long blockId;
        do {
            // Sleep for a while.
            try {
                TimeUnit.MILLISECONDS.sleep(200L);
            } catch (InterruptedException ignored) {
            }
            BlockData blockData = BlockchainManager.getBlockDataToMine();
            blockId = (blockData == null) ? 0 : blockData.getBlockId();
        } while (blockId <= NUMBER_OF_BLOCKS_FOR_TESTING);

        // Stop users.
        Users.stopUsers();

        // Stop miners.
        Miners.stopMiners();

        // Display validation errors for blockchain.
        String validationErrors = BlockchainManager.getValidationErrors();
        if (!validationErrors.equals("")) {
            logger.console(validationErrors);
        }

        // Log final balances for users.
        logger.info("Final balances");
        var users = UserManager.getUsers();
        for (Map.Entry<String, User> userEntry : users.entrySet()) {
            User user = userEntry.getValue();
            logger.info(String.format("%s: %s", user.userId(), BlockchainManager.getBalance(user.userId(), false).toString()));
        }

        logger.info("Blockchain program ended.");
    }
}
