package blockchain;

import blockchain.Data.User;
import blockchain.Utils.Cryptography;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.Optional;

public class UserManager {
    public static final String BLOCKCHAIN_USER_ID = "*Blockchain*";

    private static final HashMap<String, User> userMap = new HashMap<>();

    // Register user with system.
    public static void registerUser(String userId, PublicKey publicKey) {
        var user = new User(userId, publicKey);
        userMap.put(userId, user);
    }

    // Get all users.
    public static HashMap<String, User> getUsers() {
        return userMap;
    }

    // Get user by id.
    public static User getUser(String userId) {
        return userMap.get(userId);
    }

    // Verify message signature.
    public static Optional<Boolean> verifyMessageSignature(String message, String messageSignature, String userId) {
        return Cryptography.verifyMessageSignature(message, messageSignature, userMap.get(userId).publicKey());
    }
}
