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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.salt.ai.hub.data.mapper.ChatMapper;
import org.salt.ai.hub.data.po.ChatInfo;
import org.salt.ai.hub.data.po.SessionInfo;
import org.salt.ai.hub.data.service.ChatService;
import org.salt.ai.hub.data.vo.ChatVo;
import org.salt.ai.hub.frame.utils.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    ChatMapper chatMapper;

    @Override
    public ChatVo load(String id) {
        LambdaQueryWrapper<ChatInfo> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ChatInfo::getChatId, id);
        return ConvertUtil.convert(chatMapper.selectOne(wrapper), ChatVo.class);
    }

    @Override
    public void create(ChatVo chatVo) {
        ChatInfo chatInfo = ConvertUtil.convert(chatVo, ChatInfo.class);
        chatMapper.insert(chatInfo);
    }

    @Override
    public void update(ChatVo chatVo) {
        LambdaQueryWrapper<ChatInfo> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ChatInfo::getChatId, chatVo.getChatId());
        ChatInfo chatInfo = new ChatInfo();
        chatInfo.setQuestion(chatVo.getQuestion());
        chatInfo.setAnswer(chatVo.getQuestion());
        chatMapper.update(chatInfo, wrapper);
    }
}
