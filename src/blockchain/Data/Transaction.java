package blockchain.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

public record Transaction(
        long transactionId,
        String fromUserId,
        String toUserId,
        BigDecimal amount) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
}
