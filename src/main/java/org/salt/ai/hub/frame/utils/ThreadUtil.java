/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.salt.ai.hub.frame.utils;

import java.util.HashMap;
import java.util.Map;

public class ThreadUtil {

    private static final ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal<>();

    public static Map<String, Object> getAll() {
        if (threadLocal.get() == null) {
            threadLocal.set(new HashMap<>());
        }
        return threadLocal.get();
    }

    public static void putAll(Map<String, Object> map) {
        threadLocal.set(map);
    }

    public static <P> void put(String key, P value) {
        if (threadLocal.get() == null) {
            threadLocal.set(new HashMap<>());
        }
        threadLocal.get().put(key, value);
    }

    public static <P> P get(String key) {
        if (threadLocal.get() == null) {
            threadLocal.set(new HashMap<>());
        }
        return (P) threadLocal.get().get(key);
    }

    public static void clean() {
        threadLocal.remove();
    }
}
