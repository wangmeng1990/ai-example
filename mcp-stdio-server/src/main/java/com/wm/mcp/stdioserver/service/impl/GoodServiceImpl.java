package com.wm.mcp.stdioserver.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wm.mcp.stdioserver.mapper.GoodMapper;
import com.wm.mcp.stdioserver.model.Good;
import com.wm.mcp.stdioserver.service.GoodService;
import org.springframework.stereotype.Service;

@Service
public class GoodServiceImpl extends ServiceImpl<GoodMapper, Good> implements GoodService {
}
