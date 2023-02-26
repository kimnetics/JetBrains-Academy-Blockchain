package blockchain.Threads;

import blockchain.BlockchainManager;
import blockchain.Data.Transaction;
import blockchain.Data.User;
import blockchain.UserManager;
import blockchain.Utils.Cryptography;
import blockchain.Utils.Logger;
import blockchain.Utils.Serialization;

import java.math.BigDecimal;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Users {
    private static Logger logger;
    private static ExecutorService executor;

    public Users() {
    }

    public static void setLogger(Logger logger) {
        Users.logger = logger;
    }

    // Start users.
    public static void startUsers(ArrayList<String> userIds) {
        executor = Executors.newFixedThreadPool(userIds.size());
        for (String userId : userIds) {
            KeyPair keyPair = Cryptography.generateKeyPair();
            UserManager.registerUser(userId, keyPair.getPublic());
            executor.execute(new UserThread(userId, keyPair.getPrivate()));
        }
    }

    // Stop users.
    public static void stopUsers() {
        executor.shutdown();
        while (!executor.isTerminated()) {
            // Sleep for a while.
            try {
                TimeUnit.MILLISECONDS.sleep(200L);
            } catch (InterruptedException ignored) {
            }
        }
    }

    private static class UserThread implements Runnable {
        private final String userId;
        private final PrivateKey privateKey;

        public UserThread(String userId, PrivateKey privateKey) {
            this.userId = userId;
            this.privateKey = privateKey;
        }

        @Override
        public void run() {
            logger.info(String.format("User %s started.", userId));

            Random random = new Random();

            // Loop until shutdown.
            while (!executor.isShutdown()) {
                // Sleep for a random while.
                try {
                    TimeUnit.MILLISECONDS.sleep(400L * (random.nextInt(3) + 1));
                } catch (InterruptedException ignored) {
                }
                // Add a transaction to send someone some money.
                // Loop is present, so we can try a larger transaction id if someone already added a transaction with a larger one than we used.
                String response;
                String toUserId = getRandomToUserId(random, userId);
                BigDecimal amount = getRandomAmount(random);
                do {
                    var transaction = new Transaction(BlockchainManager.getNextTransactionId(), userId, toUserId, amount);
                    String transactionAsString = Serialization.serializeObject(transaction);
                    String transactionSignature = Cryptography.getMessageSignature(transactionAsString, privateKey).get();
                    response = BlockchainManager.addTransaction(transactionAsString, transactionSignature);
                } while ((response != null) && (!response.equals(BlockchainManager.ADD_TRANSACTION_ERROR_INSUFFICIENT_BALANCE)));
            }

            logger.info(String.format("User %s stopped.", userId));
        }

        private String getRandomToUserId(Random random, String myUserId) {
            Map<String, User> users = UserManager.getUsers();
            ArrayList<String> userIds = new ArrayList<>(users.keySet());

            String toUserId;
            do {
                toUserId = userIds.get(random.nextInt(userIds.size()));
            } while (toUserId.equals(myUserId));
            return toUserId;
        }

        private BigDecimal getRandomAmount(Random random) {
            int amount = random.nextInt(10) + 1;
            return new BigDecimal(amount);
        }
    }
}
