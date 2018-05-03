import org.junit.Test;
import ru.csi.interview.combine.PriceCombiner;
import ru.csi.interview.dto.Price;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class CombinePriceTest {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private PriceCombiner priceCombiner = new PriceCombiner();

    @Test
    public void testCombinePriceWithSameValue() throws ParseException {
        Date oldPriceBeginDate = dateFormat.parse("01.01.2013 00:00:00");
        Date oldPriceEndDate = dateFormat.parse("31.01.2013 23:59:59");
        List<Price> oldPrices = new ArrayList<>();
        Price oldPrice1 = new Price();
        oldPrice1.setProductCode("122856");
        oldPrice1.setNumber(1);
        oldPrice1.setDepart(1);
        oldPrice1.setBegin(oldPriceBeginDate);
        oldPrice1.setEnd(oldPriceEndDate);
        oldPrice1.setValue(11000);
        oldPrices.add(oldPrice1);

        Date newPriceBeginDate = dateFormat.parse("20.01.2013 00:00:00");
        Date newPriceEndDate = dateFormat.parse("20.02.2013 23:59:59");
        List<Price> newPrices = new ArrayList<>();
        Price newPrice1 = new Price();
        newPrice1.setProductCode("122856");
        newPrice1.setNumber(1);
        newPrice1.setDepart(1);
        newPrice1.setBegin(newPriceBeginDate);
        newPrice1.setEnd(newPriceEndDate);
        newPrice1.setValue(11000);
        newPrices.add(newPrice1);

        List<Price> combinedPrices = priceCombiner.combine(oldPrices, newPrices);

        assertEquals(combinedPrices.size(), 1);
        Price resultPrice = combinedPrices.get(0);
        assertEquals(resultPrice.getBegin(), oldPriceBeginDate);
        assertEquals(resultPrice.getEnd(), newPriceEndDate);
    }

    @Test
    public void testAddNewPriceWithoutDateIntersectWithSameValue() throws ParseException {
        Price oldPrice = new Price();
        oldPrice.setProductCode("6654");
        oldPrice.setNumber(1);
        oldPrice.setDepart(2);
        oldPrice.setBegin(dateFormat.parse("01.01.2013 00:00:00"));
        oldPrice.setEnd(dateFormat.parse("31.01.2013 00:00:00"));
        oldPrice.setValue(5000);
        List<Price> oldPrices = new ArrayList<>();
        oldPrices.add(oldPrice);

        List<Price> newPrices = new ArrayList<>();
        Price newPrice = new Price();
        newPrice.setProductCode("6654");
        newPrice.setNumber(1);
        newPrice.setDepart(2);
        newPrice.setBegin(dateFormat.parse("05.02.2013 00:00:00"));
        newPrice.setEnd(dateFormat.parse("25.02.2013 00:00:00"));
        newPrice.setValue(5000);
        newPrices.add(newPrice);

        List<Price> combinedPrices = priceCombiner.combine(oldPrices, newPrices);
        assertEquals(combinedPrices.size(), 2);
        assertTrue(combinedPrices.contains(oldPrice));
        assertTrue(combinedPrices.contains(newPrice));
    }

    @Test
    public void testAddNewPriceWithoutDateIntersectWithDifferentValue() throws ParseException {
        Price oldPrice = new Price();
        oldPrice.setProductCode("122856");
        oldPrice.setNumber(2);
        oldPrice.setDepart(1);
        oldPrice.setBegin(dateFormat.parse("01.01.2013 00:00:00"));
        oldPrice.setEnd(dateFormat.parse("31.01.2013 00:00:00"));
        oldPrice.setValue(5000);
        List<Price> oldPrices = new ArrayList<>();
        oldPrices.add(oldPrice);

        List<Price> newPrices = new ArrayList<>();
        Price newPrice = new Price();
        newPrice.setProductCode("122856");
        newPrice.setNumber(2);
        newPrice.setDepart(1);
        newPrice.setBegin(dateFormat.parse("05.02.2013 00:00:00"));
        newPrice.setEnd(dateFormat.parse("25.02.2013 00:00:00"));
        newPrice.setValue(6000);
        newPrices.add(newPrice);

        List<Price> combinedPrices = priceCombiner.combine(oldPrices, newPrices);
        assertEquals(combinedPrices.size(), 2);
        assertTrue(combinedPrices.contains(oldPrice));
        assertTrue(combinedPrices.contains(newPrice));
    }

    @Test
    public void testAddNewPriceWithDifferentNumbers() throws ParseException {
        Price oldPrice = new Price();
        oldPrice.setProductCode("122856");
        oldPrice.setNumber(2);
        oldPrice.setDepart(1);
        oldPrice.setBegin(dateFormat.parse("10.01.2013 00:00:00"));
        oldPrice.setEnd(dateFormat.parse("20.01.2013 23:59:59"));
        oldPrice.setValue(99000);
        List<Price> oldPrices = new ArrayList<>();
        oldPrices.add(oldPrice);

        List<Price> newPrices = new ArrayList<>();
        Price newPrice = new Price();
        newPrice.setProductCode("122856");
        newPrice.setNumber(1);
        newPrice.setDepart(1);
        newPrice.setBegin(dateFormat.parse("10.01.2013 00:00:00"));
        newPrice.setEnd(dateFormat.parse("20.01.2013 23:59:59"));
        newPrice.setValue(98000);
        newPrices.add(newPrice);

        List<Price> combinedPrices = priceCombiner.combine(oldPrices, newPrices);
        assertEquals(combinedPrices.size(), 2);
        assertTrue(combinedPrices.contains(oldPrice));
        assertTrue(combinedPrices.contains(newPrice));
    }

    @Test
    public void testAddNewPrices() throws ParseException {
        Price oldPrice = new Price();
        oldPrice.setProductCode("122856");
        oldPrice.setNumber(2);
        oldPrice.setDepart(1);
        oldPrice.setBegin(dateFormat.parse("10.01.2013 00:00:00"));
        oldPrice.setEnd(dateFormat.parse("20.01.2013 23:59:59"));
        oldPrice.setValue(99000);
        List<Price> oldPrices = new ArrayList<>();
        oldPrices.add(oldPrice);

        List<Price> newPrices = new ArrayList<>();
        Price newPrice = new Price();
        newPrice.setProductCode("9999");
        newPrice.setNumber(1);
        newPrice.setDepart(1);
        newPrice.setBegin(dateFormat.parse("10.01.2013 00:00:00"));
        newPrice.setEnd(dateFormat.parse("20.01.2013 23:59:59"));
        newPrice.setValue(98000);

        Price newPrice2 = new Price();
        newPrice2.setProductCode("9998");
        newPrice2.setNumber(1);
        newPrice2.setDepart(1);
        newPrice2.setBegin(dateFormat.parse("10.01.2013 00:00:00"));
        newPrice2.setEnd(dateFormat.parse("20.01.2013 23:59:59"));
        newPrice2.setValue(98000);

        newPrices.add(newPrice);
        newPrices.add(newPrice2);

        List<Price> combinedPrices = priceCombiner.combine(oldPrices, newPrices);
        assertEquals(combinedPrices.size(), 3);
        assertTrue(combinedPrices.contains(oldPrice));
        assertTrue(combinedPrices.contains(newPrice));
        assertTrue(combinedPrices.contains(newPrice2));
    }

    @Test
    public void testCombine() throws ParseException {
        List<Price> oldPrices = new ArrayList<>();
        Price oldPrice1 = new Price();
        oldPrice1.setProductCode("122856");
        oldPrice1.setNumber(1);
        oldPrice1.setDepart(1);
        oldPrice1.setBegin(dateFormat.parse("01.01.2013 00:00:00"));
        oldPrice1.setEnd(dateFormat.parse("31.01.2013 23:59:59"));
        oldPrice1.setValue(11000);

        Price oldPrice2 = new Price();
        oldPrice2.setProductCode("122856");
        oldPrice2.setNumber(2);
        oldPrice2.setDepart(1);
        oldPrice2.setBegin(dateFormat.parse("10.01.2013 00:00:00"));
        oldPrice2.setEnd(dateFormat.parse("20.01.2013 23:59:59"));
        oldPrice2.setValue(99000);

        Price oldPrice3 = new Price();
        oldPrice3.setProductCode("6654");
        oldPrice3.setNumber(1);
        oldPrice3.setDepart(2);
        oldPrice3.setBegin(dateFormat.parse("01.01.2013 00:00:00"));
        oldPrice3.setEnd(dateFormat.parse("31.01.2013 00:00:00"));
        oldPrice3.setValue(5000);

        oldPrices.add(oldPrice1);
        oldPrices.add(oldPrice2);
        oldPrices.add(oldPrice3);

        List<Price> newPrices = new ArrayList<>();

        Price newPrice1 = new Price();
        newPrice1.setProductCode("122856");
        newPrice1.setNumber(1);
        newPrice1.setDepart(1);
        newPrice1.setBegin(dateFormat.parse("20.01.2013 00:00:00"));
        newPrice1.setEnd(dateFormat.parse("20.02.2013 23:59:59"));
        newPrice1.setValue(11000);

        Price newPrice2 = new Price();
        newPrice2.setProductCode("122856");
        newPrice2.setNumber(2);
        newPrice2.setDepart(1);
        newPrice2.setBegin(dateFormat.parse("15.01.2013 00:00:00"));
        newPrice2.setEnd(dateFormat.parse("25.01.2013 23:59:59"));
        newPrice2.setValue(92000);

        Price newPrice3 = new Price();
        newPrice3.setProductCode("6654");
        newPrice3.setNumber(1);
        newPrice3.setDepart(2);
        newPrice3.setBegin(dateFormat.parse("12.01.2013 00:00:00"));
        newPrice3.setEnd(dateFormat.parse("13.01.2013 00:00:00"));
        newPrice3.setValue(4000);

        newPrices.add(newPrice1);
        newPrices.add(newPrice2);
        newPrices.add(newPrice3);

        List<Price> combinedPrices = priceCombiner.combine(oldPrices, newPrices);
        combinedPrices.forEach(System.out::println);
        assertEquals(combinedPrices.size(), 6);
    }

}
