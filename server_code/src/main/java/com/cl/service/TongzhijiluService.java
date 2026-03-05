package com.cl.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.cl.utils.PageUtils;
import com.cl.entity.TongzhijiluEntity;
import java.util.List;
import java.util.Map;
import com.cl.entity.view.TongzhijiluView;


/**
 * 通知记录
 *
 * @author 
 * @email 
 * @date 2025-03-27 15:44:15
 */
public interface TongzhijiluService extends IService<TongzhijiluEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
    PageUtils queryPage(Map<String, Object> params, Wrapper<TongzhijiluEntity> wrapper);
	
	List<TongzhijiluView> selectListView(Wrapper<TongzhijiluEntity> wrapper);
	
	TongzhijiluView selectView(Wrapper<TongzhijiluEntity> wrapper);
	
	List<Map<String, Object>> selectValue(Map<String, Object> params, Wrapper<TongzhijiluEntity> wrapper);

	List<Map<String, Object>> selectGroup(Map<String, Object> params, Wrapper<TongzhijiluEntity> wrapper);

}
