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

package org.salt.ai.hub.data.service.impl;

import org.salt.ai.hub.data.mapper.ChatMapper;
import org.salt.ai.hub.data.service.ChatService;
import org.salt.ai.hub.data.vo.ChatVo;
import org.salt.ai.hub.frame.utils.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    ChatMapper chatMapper;

    @Override
    public ChatVo load(String id) {
        return ConvertUtil.convert(chatMapper.selectById(id), ChatVo.class);
    }
}
