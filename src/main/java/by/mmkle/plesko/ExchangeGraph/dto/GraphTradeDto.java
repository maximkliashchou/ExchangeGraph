package by.mmkle.plesko.ExchangeGraph.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GraphTradeDto {
    private Long tradeId;

    private Long date;

    private String type;

    private BigDecimal quantity;

    private BigDecimal price;

    private BigDecimal amount;

}
