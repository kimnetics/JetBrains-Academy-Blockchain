package blockchain.Utils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;
import java.util.Optional;

public class Cryptography {
    // Create RSA key pair.
    public static KeyPair generateKeyPair() {
        KeyPairGenerator keyPairGenerator;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Unexpected error getting KeyPairGenerator instance. " + e.getMessage());
        }
        keyPairGenerator.initialize(1024);

        return keyPairGenerator.generateKeyPair();
    }

    // Get message signature.
    public static Optional<String> getMessageSignature(String message, PrivateKey privateKey) {
        Signature signature;
        try {
            signature = Signature.getInstance("SHA1withRSA");
            signature.initSign(privateKey);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Unexpected error preparing Signature instance. " + e.getMessage());
        }
        String messageSignature;
        try {
            signature.update(message.getBytes(StandardCharsets.UTF_8));
            messageSignature = Base64.getEncoder().encodeToString(signature.sign());
        } catch (SignatureException e) {
            return Optional.empty();
        }

        return Optional.of(messageSignature);
    }

    // Verify message signature.
    public static Optional<Boolean> verifyMessageSignature(String message, String messageSignature, PublicKey publicKey) {
        Signature signature;
        try {
            signature = Signature.getInstance("SHA1withRSA");
            signature.initVerify(publicKey);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Unexpected error preparing Signature instance. " + e.getMessage());
        }
        boolean verified;
        try {
            signature.update(message.getBytes(StandardCharsets.UTF_8));
            verified = signature.verify(Base64.getDecoder().decode(messageSignature));
        } catch (SignatureException e) {
            return Optional.empty();
        }

        return Optional.of(verified);
    }

    // Encrypt message.
    public static Optional<String> encryptMessage(String message, PrivateKey privateKey) {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            throw new RuntimeException("Unexpected error preparing Cipher instance. " + e.getMessage());
        }
        String encryptedMessage;
        try {
            encryptedMessage = Base64.getEncoder().encodeToString(cipher.doFinal(message.getBytes(StandardCharsets.UTF_8)));
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            return Optional.empty();
        }

        return Optional.of(encryptedMessage);
    }

    // Decrypt message.
    public static Optional<String> decryptMessage(String message, PublicKey publicKey) {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            throw new RuntimeException("Unexpected error preparing Cipher instance. " + e.getMessage());
        }
        String decryptedMessage;
        try {
            decryptedMessage = new String(cipher.doFinal(Base64.getDecoder().decode(message)), StandardCharsets.UTF_8);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            return Optional.empty();
        }

        return Optional.of(decryptedMessage);
    }
}
