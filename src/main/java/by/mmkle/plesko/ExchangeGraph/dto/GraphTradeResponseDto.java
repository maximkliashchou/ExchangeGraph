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
public class GraphTradeResponseDto {
    private Long id;

    private BigDecimal price;

    private BigDecimal qty;

    private BigDecimal quoteQty;

    private Long time;

    private Boolean isBuyerMaker;

    private Boolean isBestMatch;

}
