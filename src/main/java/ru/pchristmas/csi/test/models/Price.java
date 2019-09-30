package ru.pchristmas.csi.test.models;

import lombok.*;

import java.util.Date;
import java.util.Objects;

@Data
@RequiredArgsConstructor
public class Price {
    private long id; // идентификатор в БД
    @NonNull private String productCode; // код товара
    @NonNull private int number; // номер цены
    @NonNull private int depart; // номер отдела
    @NonNull private Date begin; // начало действия
    @NonNull private Date end; // конец действия
    @NonNull private long value; // значение цены в копейках

    @Override
    public int hashCode() {
        return Objects.hash(productCode, number, depart, begin, end, value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Price) {
            Price p = (Price) obj;
            return productCode.equals(p.getProductCode()) && number == p.getNumber() &&
                    depart == p.getDepart() && begin.compareTo(p.getBegin()) == 0 &&
                    end.compareTo(p.getEnd()) == 0 && value == p.getValue();
        }
        return false;
    }
}
