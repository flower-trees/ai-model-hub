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
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.salt.ai.hub.data.mapper.ChatHisMapper;
import org.salt.ai.hub.data.po.ChatHisInfo;
import org.salt.ai.hub.data.po.ChatInfo;
import org.salt.ai.hub.data.service.ChatHisService;
import org.salt.ai.hub.data.vo.ChatHisVo;
import org.salt.ai.hub.frame.utils.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatHisServiceImpl implements ChatHisService {

    @Autowired
    ChatHisMapper chatHisMapper;

    @Override
    public ChatHisVo load(String id) {
        LambdaQueryWrapper<ChatHisInfo> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ChatHisInfo::getChatHisId, id);
        return ConvertUtil.convert(chatHisMapper.selectById(id), ChatHisVo.class);
    }

    @Override
    public void create(ChatHisVo chatHisVo) {
        ChatHisInfo chatHisInfo = ConvertUtil.convert(chatHisVo, ChatHisInfo.class);
        chatHisMapper.insert(chatHisInfo);
    }
}
