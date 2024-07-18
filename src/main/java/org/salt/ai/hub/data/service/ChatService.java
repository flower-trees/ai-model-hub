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

package org.salt.ai.hub.data.service;

import org.salt.ai.hub.data.vo.ChatVo;

import java.util.List;

public interface ChatService {
    ChatVo load(String id);
    void create(ChatVo chatVo);
    void update(ChatVo chatVo);
    List<ChatVo> queryLastList(String sessionId, int limit);
}
