package by.mmkle.plesko.ExchangeGraph.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum GraphTimeEnum {
    MINUTES_1(1, "1 minutes"),
    MINUTES_5(2, "5 minutes"),
    MINUTES_15(3, "15 minutes"),
    HOUR_1(4, "1 hours"),
    HOUR_3(5, "3 hours"),
    HOUR_6(6, "6 hours"),
    HOUR_12(7, "12 hours"),
    DAY(8, "1 days");

    private Integer code;

    private String value;
}
