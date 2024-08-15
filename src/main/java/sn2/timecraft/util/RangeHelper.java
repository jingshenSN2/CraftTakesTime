package sn2.timecraft.util;

import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@UtilityClass
public class RangeHelper {
    public static List<Integer> range(int start, int end) {
        return IntStream.rangeClosed(1, 9).boxed().collect(Collectors.toList());
    }
}
