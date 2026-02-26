package io.github.jacorycyjin.smartlibrary.backend.common.util;

import java.util.UUID;

/**
 * @author Jacory
 * @date 2025/12/13
 */
public class UUIDUtil {
    public static String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
