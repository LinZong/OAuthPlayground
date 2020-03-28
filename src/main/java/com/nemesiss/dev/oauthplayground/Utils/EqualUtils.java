package com.nemesiss.dev.oauthplayground.Utils;

import java.util.Map;

public class EqualUtils {

    public static boolean CompareTwoMap(Map<?,?> source,Map<?,?> destination) {
        for (Map.Entry<?, ?> entry : source.entrySet()) {
            if(!destination.containsKey(entry.getKey())) {
                return false;
            }
            if(destination.get(entry.getKey()).equals(entry.getValue())) {
                return false;
            }
        }
        return true;
    }
}
