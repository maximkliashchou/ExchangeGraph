package by.mmkle.plesko.ExchangeGraph.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GraphCandleDto {
    private LocalDateTime time;

    private BigDecimal min;

    private BigDecimal max;

    private BigDecimal first;

    private BigDecimal last;
}
