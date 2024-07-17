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

package org.salt.ai.hub.frame.user;

import org.apache.commons.lang3.ObjectUtils;
import org.salt.ai.hub.frame.utils.ThreadUtil;

public class UserUtil {

    public static final String USER_LOCAL_THREAD_KEY = "user_local_thread_key";

    public static Long getUserId() {
        UserData userData = ThreadUtil.get(USER_LOCAL_THREAD_KEY);
        if (ObjectUtils.isNotEmpty(userData) && userData.getUserId() != null) {
            return userData.getUserId();
        }
        throw new RuntimeException("user on login");
    }

    public static void setUserId(Long userId) {
        UserData userData = new UserData();
        userData.setUserId(userId);
        ThreadUtil.put(USER_LOCAL_THREAD_KEY, userData);
    }
}
