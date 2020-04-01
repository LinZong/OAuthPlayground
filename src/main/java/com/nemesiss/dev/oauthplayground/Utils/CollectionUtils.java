package com.nemesiss.dev.oauthplayground.Utils;

import java.util.Set;

public class CollectionUtils {

    public static Set<?> GetIntersects(Set<?> origin, Set<?> destination) {
        origin.retainAll(destination);
        return origin;
    }
}
