package blockchain.Data;

import java.security.PublicKey;

public record User(
        String userId,
        PublicKey publicKey) {
}
