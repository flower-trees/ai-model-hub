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
import org.salt.ai.hub.data.mapper.AgentMapper;
import org.salt.ai.hub.data.po.AgentInfo;
import org.salt.ai.hub.data.service.AgentService;
import org.salt.ai.hub.data.vo.AgentVo;
import org.salt.ai.hub.frame.utils.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgentServiceImpl implements AgentService {

    @Autowired
    AgentMapper agentMapper;

    @Override
    public AgentVo load(String id) {
        return ConvertUtil.convert(agentMapper.selectById(id), AgentVo.class);
    }

    @Override
    public List<AgentVo> list(AgentVo agentVo) {
        LambdaQueryWrapper<AgentInfo> wrapper = Wrappers.lambdaQuery();
        wrapper.orderByAsc(AgentInfo::getId);
        wrapper.last("LIMIT " + 50);
        return ConvertUtil.convertList(agentMapper.selectList(wrapper), AgentVo.class);
    }
}
