package com.wm.ai.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.wm.ai.exception.BusinessException;
import com.wm.ai.exception.ErrorCode;
import com.wm.ai.model.ChatMessage;
import com.wm.ai.model.User;
import com.wm.ai.model.vo.ChatSessionVO;
import com.wm.ai.service.ChatMessageService;
import com.wm.ai.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wm.ai.mapper.AiSessionMapper;
import com.wm.ai.model.ChatSession;
import com.wm.ai.service.ChatSessionService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChatSessionServiceImpl extends ServiceImpl<AiSessionMapper, ChatSession> implements ChatSessionService {

    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(ChatSession chatSession) {
        User user=null;
        if (StrUtil.isNotEmpty(chatSession.getUserId())){
            user=userService.getById(chatSession.getUserId());
        }
        if (null==user){
            throw new BusinessException("用户不存在", ErrorCode.FAIL);
        }
        baseMapper.insert(chatSession);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String sessionId) {
        chatMessageService.delBySessionId(sessionId);
        baseMapper.deleteById(sessionId);
    }

    @Override
    public ChatSessionVO detail(String sessionId) {
        ChatSessionVO vo = new ChatSessionVO();
        ChatSession session = getById(sessionId);
        BeanUtil.copyProperties(session,vo);
        List<ChatMessage> chatMessageList = chatMessageService.getBySessionId(sessionId);
        vo.setChatMessageList(chatMessageList);
        return vo;
    }

    @Override
    public List<ChatSession> listUserSession(String userId) {
        if (StrUtil.isNotEmpty(userId)){
            return lambdaQuery().eq(ChatSession::getUserId,userId).list();
        }
        return List.of();
    }
}
