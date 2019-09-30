package ru.pchristmas.csi.test;

import ru.pchristmas.csi.test.models.Price;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PriceUnion {

    public Collection<Price> insert(Collection<Price> oldPrices, Collection<Price> newPrices) {
        List<Price> result = new ArrayList<>();
        Function<Price, List<Object>> compositeKey = price ->
                Arrays.asList(price.getProductCode(), price.getNumber(), price.getDepart());

        Map<Object, Set<Price>> currentMap =
                oldPrices.stream().collect(Collectors.groupingBy(compositeKey, Collectors.toSet()));

        Map<Object, Set<Price>> mapInsert =
                newPrices.stream().collect(Collectors.groupingBy(compositeKey, Collectors.toSet()));

        Set<Object> startKeys = new HashSet<>(currentMap.keySet());
        Set<Object> newKeys = new HashSet<>(mapInsert.keySet());
        // common keys should be processed
        Set<Object> intersection = new HashSet<>(startKeys);
        intersection.retainAll(newKeys);
        for (Object key : intersection) {
            Set<Price> prices = currentMap.get(key);
            for (Price p : mapInsert.get(key)) {
                prices = insertNewPrice(prices, p);
            }
            result.addAll(prices);
        }
        // remove empty intervals
        result = result.stream().filter(c -> !c.getEnd().equals(c.getBegin())).collect(Collectors.toList());
        // values of unique keys for both maps could be added to result without changes
        startKeys.removeAll(intersection);
        newKeys.removeAll(intersection);
        addPrices(result, currentMap, startKeys);
        addPrices(result, mapInsert, newKeys);

        return result;
    }

    private Set<Price> insertNewPrice(Set<Price> oldSet, Price newPrice) {
        Set<Price> result = new HashSet<>();
        boolean hadCommonDays = false; // new price have any intersections with one of existing prices
        for (Price oldPrice : oldSet) {
            if (oldPrice.getBegin().after(newPrice.getEnd()) ||
                    oldPrice.getEnd().before(newPrice.getBegin())) {
                result.add(oldPrice); // 1: new price has no effect on this old price - just add it to result
            } else { // 2: common dates
                hadCommonDays = true;
                if (newPrice.getValue() == oldPrice.getValue()) {
                    // 2.1: price values the same, enlarge existing price according to rule
                    result.add(commonPriceRule(oldPrice, newPrice));
                } else {
                    // 2.2: diff values, change old
                    differentPriceRule(result, oldPrice, newPrice);
                }
            }
        }
        if (!hadCommonDays) {
            result.add(newPrice); // Just new independent price for pricelist
        }
        return result;
    }

    private Price commonPriceRule(Price oldPrice, Price newPrice) {
        if (oldPrice.getBegin().after(newPrice.getBegin())) {
            oldPrice.setBegin(newPrice.getBegin());
        }
        if (oldPrice.getEnd().before(newPrice.getEnd())) {
            oldPrice.setEnd(newPrice.getEnd());
        }
        newPrice.setBegin(oldPrice.getEnd());
        return oldPrice;
    }

    private void differentPriceRule(Collection<Price> result, Price oldPrice, Price newPrice) {
        Date oldStart = oldPrice.getBegin();
        Date newStart = newPrice.getBegin();
        Date oldEnd = oldPrice.getEnd();
        Date newEnd = newPrice.getEnd();

        if (oldStart.before(newStart)) {
            if (newEnd.before(oldEnd)) {
                // new price cut old into 3 pieces
                Price cutPrice = new Price(oldPrice.getProductCode(), oldPrice.getNumber(),
                        oldPrice.getDepart(), newEnd, oldEnd, oldPrice.getValue());
                oldPrice.setEnd(newStart);
                addPrices(result, oldPrice, newPrice, cutPrice);
            } else {
                oldPrice.setEnd(newStart);
                addPrices(result, oldPrice, newPrice);
            }
        } else {
            if (newEnd.before(oldEnd)) {
                oldPrice.setBegin(newEnd);
                addPrices(result, oldPrice, newPrice);
            } else {
                // new price devours the old one
                result.add(newPrice);
            }
        }
    }

    private void addPrices(Collection<Price> result, Price... prices) {
        for (int i = 0; i < prices.length; i++) {
            result.add(prices[i]);
        }
    }

    private void addPrices(Collection<Price> result, Map<Object, Set<Price>> map, Set<Object> keySet) {
        for (Object key : keySet) {
            result.addAll(map.get(key));
        }
    }

}
