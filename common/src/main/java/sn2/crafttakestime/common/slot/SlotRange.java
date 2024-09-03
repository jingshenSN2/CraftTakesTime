package sn2.crafttakestime.common.slot;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Builder
@Data
public class SlotRange implements Iterable<Integer> {

    @Builder.Default
    private List<Integer> slots = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);

    public static SlotRange fromString(String range) {
        // Format: 1-9,12,15-18
        String[] split = range.split(",");
        List<Integer> slots = new ArrayList<>();
        for (String s : split) {
            if (s.contains("-")) {
                String[] rangeSplit = s.split("-");
                int start = Integer.parseInt(rangeSplit[0]);
                int end = Integer.parseInt(rangeSplit[1]);
                slots.addAll(IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList()));
            } else {
                slots.add(Integer.parseInt(s));
            }
        }
        return SlotRange.builder().slots(slots).build();
    }

    public String toString() {
        // Format: 1-9,12,15-18
        StringBuilder sb = new StringBuilder();
        int start = -1;
        int end = -1;
        for (int slot : slots) {
            if (start == -1) {
                start = slot;
                end = start;
            } else {
                if (slot == end + 1) {
                    end = slot;
                } else {
                    if (start == end) {
                        sb.append(start);
                    } else {
                        sb.append(start).append("-").append(end);
                    }
                    sb.append(",");
                    start = slot;
                    end = start;
                }
            }
        }
        if (start == end) {
            sb.append(start);
        } else {
            sb.append(start).append("-").append(end);
        }
        return sb.toString();
    }

    @Override
    public Iterator<Integer> iterator() {
        return slots.iterator();
    }
}
