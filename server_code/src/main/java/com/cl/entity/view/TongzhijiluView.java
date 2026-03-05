package com.cl.entity.view;

import com.baomidou.mybatisplus.annotations.TableName;
import com.cl.entity.TongzhijiluEntity;
import org.apache.commons.beanutils.BeanUtils;
import java.lang.reflect.InvocationTargetException;
import java.io.Serializable;
import java.util.Date;


/**
 * 通知记录
 * 后端返回视图实体辅助类
 * （通常后端关联的表或者自定义的字段需要返回使用）
 */
@TableName("tongzhijilu")
public class TongzhijiluView extends TongzhijiluEntity implements Serializable {
    private static final long serialVersionUID = 1L;

	public TongzhijiluView(){
	}

	public TongzhijiluView(TongzhijiluEntity tongzhijiluEntity){
		try {
			BeanUtils.copyProperties(this, tongzhijiluEntity);
		} catch (IllegalAccessException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
