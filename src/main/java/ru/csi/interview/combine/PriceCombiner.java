package ru.csi.interview.combine;

import ru.csi.interview.dto.Price;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PriceCombiner {

    public List<Price> combine(List<Price> oldPrices, List<Price> newPrices) {
        List<Price> resultPrices = findAllPricesThatAreNotToBeCombined(oldPrices, newPrices);
        Map<Price, Price> OldToNewPriceMap = mapOldToNewPricesThatAreToBeCombined(oldPrices, newPrices);
        OldToNewPriceMap.forEach((oldPrice, newPrice) -> {
            resultPrices.addAll(combine(oldPrice, newPrice));
        });
        return resultPrices;
    }

    private List<Price> combine(Price oldPrice, Price newPrice) {
        List<Price> combinedPrices = new ArrayList<>();
        if (oldPrice.getValue() == newPrice.getValue()) {
            combinedPrices.addAll(combinePricesWithSameValue(oldPrice, newPrice));
        } else {
            combinedPrices.add(newPrice);
            if (doPricePeriodsIntersect(oldPrice, newPrice)) {
                List<Price> overlapped = overlayNewPriceToOld(oldPrice, newPrice);
                combinedPrices.addAll(overlapped);
            } else {
                combinedPrices.add(oldPrice);
            }
        }
        combinedPrices.sort(Comparator.comparing(Price::getBegin)); //отсортировать по начальной дате
        return combinedPrices;
    }

    private List<Price> findAllPricesThatAreNotToBeCombined(List<Price> oldPrices, List<Price> newPrices) {
        List<Price> resultList = new ArrayList<>();
        List<Price> oldPricesWithoutCorrespondingNew = oldPrices.stream()
                .filter(hasNoCorrespondingPriceIn(newPrices))
                .collect(Collectors.toList());
        List<Price> newPricesWithoutCorrespondingOld = newPrices.stream()
                .filter(hasNoCorrespondingPriceIn(oldPrices))
                .collect(Collectors.toList());
        resultList.addAll(oldPricesWithoutCorrespondingNew);
        resultList.addAll(newPricesWithoutCorrespondingOld);
        return resultList;
    }

    private Map<Price, Price> mapOldToNewPricesThatAreToBeCombined(List<Price> oldPrices, List<Price> newPrices) {
        Map<Price, Price> oldToNewPriceMap = new HashMap<>();
        newPrices.forEach((newPrice) -> {
            Price oldPriceCorrespondingToNew = findCorrespondingPrice(oldPrices, newPrice);
            if (oldPriceCorrespondingToNew != null) {
                oldToNewPriceMap.put(oldPriceCorrespondingToNew, newPrice);
            }
        });
        return oldToNewPriceMap;
    }

    private Predicate<Price> hasNoCorrespondingPriceIn(List<Price> priceList) {
        return (oldPrice) -> findCorrespondingPrice(priceList, oldPrice) == null;
    }

    private boolean doPricePeriodsIntersect(Price oldPrice, Price newPrice) {
        return (oldPrice.getEnd().after(newPrice.getBegin()));
    }

    private List<Price> overlayNewPriceToOld(Price oldPrice, Price newPrice) {
        List<Price> combinedPrices = new ArrayList<>();
        Date oldBeginDate = oldPrice.getBegin();
        Date oldEndDate = oldPrice.getEnd();
        Date newBeginDate = newPrice.getBegin();
        Date newEndDate = newPrice.getEnd();
        if (newBeginDate.before(oldEndDate) && newBeginDate.after(oldBeginDate)) {
            combinedPrices.add(copyPriceWithNewDates(oldPrice, oldBeginDate, newBeginDate));
        }
        if (newEndDate.before(oldEndDate)) {
            combinedPrices.add(copyPriceWithNewDates(oldPrice, newEndDate, oldEndDate));
        }
        return combinedPrices;
    }

    private List<Price> combinePricesWithSameValue(Price oldPrice, Price newPrice) {
        List<Price> combinedPrices = new ArrayList<>();
        if (doPricePeriodsIntersect(oldPrice, newPrice)) {
            combinedPrices.add(mergePriceDatePeriod(oldPrice, newPrice));
        } else {
            combinedPrices.add(oldPrice);
            combinedPrices.add(newPrice);
        }
        return combinedPrices;
    }

    //Находит соответствующую цену среди списка по атрибутам кода, числа и отдела
    private Price findCorrespondingPrice(Collection<Price> PriceList, Price price) {
        return PriceList.stream().filter(oldPrice -> {
            String oldProductCode = oldPrice.getProductCode();
            String newProductCode = price.getProductCode();
            int oldNumber = oldPrice.getNumber();
            int newNumber = price.getNumber();
            int oldDepart = oldPrice.getDepart();
            int newDepart = price.getDepart();
            return (oldProductCode.equals(newProductCode)) && (oldNumber == newNumber) && (oldDepart == newDepart);
        }).findFirst().orElse(null);
    }

    private Price mergePriceDatePeriod(Price firstPrice, Price secondPrice) {
        Date firstBeginDate = firstPrice.getBegin();
        Date firstEndDate = firstPrice.getEnd();
        Date secondBeginDate = secondPrice.getBegin();
        Date secondEndDate = secondPrice.getEnd();
        Date finalBeginDate = firstBeginDate;
        Date finalEndDate = firstEndDate;
        if (firstBeginDate.after(secondBeginDate)) {
            finalBeginDate = secondBeginDate;
        }
        if (firstEndDate.before(secondEndDate)) {
            finalEndDate = secondEndDate;
        }
        return copyPriceWithNewDates(firstPrice, finalBeginDate, finalEndDate);
    }

    private Price copyPriceWithNewDates(Price priceToCopy, Date begin, Date end) {
        return new Price(
                priceToCopy.getProductCode(),
                priceToCopy.getNumber(),
                priceToCopy.getDepart(),
                begin,
                end,
                priceToCopy.getValue()
        );
    }
}
