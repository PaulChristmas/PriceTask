import org.junit.Before;
import org.junit.Test;
import ru.pchristmas.csi.test.PriceUnion;
import ru.pchristmas.csi.test.models.Price;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PriceUnionTestCases {

    // Example values
    private int year;
    private String productCode;
    private final String dimError = "Answer dimension is wrong.";
    private final String joinError = "Join algorithm error.";
    private PriceUnion combiner;

    @Before
    public void initValues() {
        long millis = System.currentTimeMillis();
        Date date = new Date(millis);
        year = date.getYear();
        productCode = "228-777";
        combiner = new PriceUnion();
    }

    private void checkCollections(Collection sample, Collection answer) {
        assertEquals(dimError, sample.size(), answer.size());
        Iterator it = sample.iterator();
        while (it.hasNext()) {
            assertTrue(joinError, answer.contains(it.next()));
        }
    }

    // Helper for local testing
    private Date getDate(int month, int date) {
        return new Date(year, month - 1, date);
    }

    @Test
    public void fromTaskExample() {
        List<Price> oldPrices = new ArrayList<>();
        List<Price> newPrices = new ArrayList<>();
        oldPrices.add(new Price("122856", 1, 1, getDate(1, 1), getDate(1, 31),11000));
        oldPrices.add(new Price("122856", 2, 1, getDate(1, 10), getDate(1, 20),99000));
        oldPrices.add(new Price("6654", 1, 2, getDate(1, 1), getDate(1, 31),5000));

        newPrices.add(new Price("122856", 1, 1, getDate(1, 20), getDate(2, 20),11000));
        newPrices.add(new Price("122856", 2, 1, getDate(1, 15), getDate(1, 25),92000));
        newPrices.add(new Price("6654", 1, 2, getDate(1, 12), getDate(1, 13),4000));


        List<Price> answer = new ArrayList<>();
        answer.add(new Price("122856", 1, 1, getDate(1, 1), getDate(2, 20),11000));
        answer.add(new Price("122856", 2, 1, getDate(1, 10), getDate(1, 15),99000));
        answer.add(new Price("122856", 2, 1, getDate(1, 15), getDate(1, 25),92000));
        answer.add(new Price("6654", 1, 2, getDate(1, 1), getDate(1, 12),5000));
        answer.add(new Price("6654", 1, 2, getDate(1, 12), getDate(1, 13),4000));
        answer.add(new Price("6654", 1, 2, getDate(1, 13), getDate(1, 31),5000));

        checkCollections(answer, combiner.insert(oldPrices, newPrices));
    }

    @Test
    public void thirdTaskExample() {
        List<Price> oldPrices = new ArrayList<>();
        List<Price> newPrices = new ArrayList<>();
        oldPrices.add(new Price(productCode, 1, 1, getDate(1, 1), getDate(1, 10),80));
        oldPrices.add(new Price(productCode, 1, 1, getDate(1, 10), getDate(1, 20),87));
        oldPrices.add(new Price(productCode, 1, 1, getDate(1, 20), getDate(1, 30),90));

        newPrices.add(new Price(productCode, 1, 1, getDate(1, 5), getDate(1, 15),80));
        newPrices.add(new Price(productCode, 1, 1, getDate(1, 15), getDate(1, 25),85));

        List<Price> answer = new ArrayList<>();
        answer.add(new Price(productCode, 1, 1, getDate(1, 1), getDate(1, 15),80));
        answer.add(new Price(productCode, 1, 1, getDate(1, 15), getDate(1, 25),85));
        answer.add(new Price(productCode, 1, 1, getDate(1, 25), getDate(1, 30),90));

        checkCollections(answer, combiner.insert(oldPrices, newPrices));
    }

    @Test
    public void splitDateCase() {
        List<Price> oldPrices = new ArrayList<>();
        List<Price> newPrices = new ArrayList<>();
        oldPrices.add(new Price(productCode, 1, 1, getDate(1, 1), getDate(1, 31),11000));

        newPrices.add(new Price(productCode, 1, 1, getDate(1, 5), getDate(1, 7),22000));
        newPrices.add(new Price(productCode, 1, 1, getDate(1, 16), getDate(1, 20),33000));

        List<Price> answer = new ArrayList<>();
        answer.add(new Price(productCode, 1, 1, getDate(1, 1), getDate(1, 5),11000));
        answer.add(new Price(productCode, 1, 1, getDate(1, 5), getDate(1, 7),22000));
        answer.add(new Price(productCode, 1, 1, getDate(1, 7), getDate(1, 16),11000));
        answer.add(new Price(productCode, 1, 1, getDate(1, 16), getDate(1, 20),33000));
        answer.add(new Price(productCode, 1, 1, getDate(1, 20), getDate(1, 31),11000));

        checkCollections(answer, combiner.insert(oldPrices, newPrices));
    }

    @Test
    public void rewriteParts() {
        List<Price> oldPrices = new ArrayList<>();
        List<Price> newPrices = new ArrayList<>();
        oldPrices.add(new Price(productCode, 1, 1, getDate(1, 5), getDate(1, 15),11000));
        oldPrices.add(new Price(productCode, 1, 1, getDate(1, 15), getDate(1, 25),22000));

        newPrices.add(new Price(productCode, 1, 1, getDate(1, 1), getDate(1, 7),17777));
        newPrices.add(new Price(productCode, 1, 1, getDate(1, 10), getDate(1, 20),27777));
        newPrices.add(new Price(productCode, 1, 1, getDate(1, 23), getDate(2, 10),37777));
        newPrices.add(new Price(productCode, 1, 1, getDate(2, 10), getDate(2, 23),47777));

        List<Price> answer = new ArrayList<>();
        answer.add(new Price(productCode, 1, 1, getDate(1, 1), getDate(1, 7),17777));
        answer.add(new Price(productCode, 1, 1, getDate(1, 7), getDate(1, 10),11000));
        answer.add(new Price(productCode, 1, 1, getDate(1, 10), getDate(1, 20),27777));
        answer.add(new Price(productCode, 1, 1, getDate(1, 20), getDate(1, 23),22000));
        answer.add(new Price(productCode, 1, 1, getDate(1, 23), getDate(2, 10),37777));
        answer.add(new Price(productCode, 1, 1, getDate(2, 10), getDate(2, 23),47777));

        checkCollections(answer, combiner.insert(oldPrices, newPrices));
    }

    @Test
    public void createNewProducts() {
        List<Price> oldPrices = new ArrayList<>();
        List<Price> newPrices = new ArrayList<>();
        oldPrices.add(new Price(productCode, 1, 1, getDate(1, 1), getDate(1, 10),80));
        oldPrices.add(new Price(productCode, 1, 1, getDate(1, 10), getDate(1, 20),87));
        oldPrices.add(new Price(productCode, 1, 1, getDate(1, 20), getDate(1, 30),90));


        newPrices.add(new Price(productCode, 1, 1, getDate(1, 5), getDate(1, 15),80));
        newPrices.add(new Price(productCode, 1, 1, getDate(1, 15), getDate(1, 25),85));
        newPrices.add(new Price(productCode + "2", 1, 1, getDate(1, 1), getDate(1, 30),90));
        newPrices.add(new Price(productCode + "2", 2, 1, getDate(1, 15), getDate(1, 20),90));
        newPrices.add(new Price(productCode + "3", 1, 1, getDate(1, 1), getDate(12, 30),500));

        List<Price> answer = new ArrayList<>();
        answer.add(new Price(productCode, 1, 1, getDate(1, 1), getDate(1, 15),80));
        answer.add(new Price(productCode, 1, 1, getDate(1, 15), getDate(1, 25),85));
        answer.add(new Price(productCode, 1, 1, getDate(1, 25), getDate(1, 30),90));
        answer.add(new Price(productCode + "2", 1, 1, getDate(1, 1), getDate(1, 30),90));
        answer.add(new Price(productCode + "2", 2, 1, getDate(1, 15), getDate(1, 20),90));
        answer.add(new Price(productCode + "3", 1, 1, getDate(1, 1), getDate(12, 30),500));

        checkCollections(answer, combiner.insert(oldPrices, newPrices));
    }


    @Test
    public void stressTest() {
        List<Price> oldPrices = new ArrayList<>();
        List<Price> newPrices = new ArrayList<>();
        oldPrices.add(new Price(productCode, 70000, 70000, getDate(1, 1), getDate(1, 10),80));
        oldPrices.add(new Price(productCode, 70000, 70000, getDate(1, 10), getDate(1, 20),87));
        oldPrices.add(new Price(productCode, 70000, 70000, getDate(1, 20), getDate(1, 30),90));
        int days = 28;
        int months = 12;
        int products = 1000;
        for (int month = 0; month < months; month++) {
            for (int day = 0; day < days; day++) {
                for (int i = 0; i < products; i++) {
                    newPrices.add(new Price(productCode + i, 1, 1, getDate(month, day), getDate(month, day + 1),
                            month + day + i));
                }
            }
        }
        assertEquals(dimError, combiner.insert(oldPrices, newPrices).size(), days*months*products + 3);
    }

    @Test
    public void emptySets() {
        List<Price> priceList1 = new ArrayList<>();
        List<Price> priceList2 = new ArrayList<>();
        priceList1.add(new Price(productCode, 70000, 70000, getDate(1, 1), getDate(1, 10),80));

        checkCollections(priceList1, combiner.insert(priceList1, priceList2));

        priceList1 = new ArrayList<>();
        priceList2.add(new Price(productCode, 70000, 70000, getDate(1, 10), getDate(1, 20),87));

        checkCollections(priceList2, combiner.insert(priceList1, priceList2));
    }

    @Test(expected = NullPointerException.class)
    public void testNulls() {
        combiner.insert(null, null);
    }

}

