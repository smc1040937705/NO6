package com.cl.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cl.entity.JiuzhentongzhiEntity;
import com.cl.entity.YishengyuyueEntity;
import com.cl.service.JiuzhentongzhiService;
import com.cl.service.NotificationService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("notificationService")
public class NotificationServiceImpl implements NotificationService {
    
    @Autowired
    private JiuzhentongzhiService jiuzhentongzhiService;
    
    @Override
    public void sendAllNotifications(YishengyuyueEntity yishengyuyue) {
        // 创建所有后续提醒通知
        List<JiuzhentongzhiEntity> notifications = createAllNotifications(yishengyuyue);
        
        // 立即发送所有通知
        for (JiuzhentongzhiEntity notification : notifications) {
            sendNotification(notification);
        }
    }
    
    @Override
    public boolean sendNotification(JiuzhentongzhiEntity tongzhi) {
        try {
            // 设置初始状态为待发送
            tongzhi.setFafangzhuangtai("待发送");
            tongzhi.setChongshishu(0);
            
            // 保存通知记录
            jiuzhentongzhiService.insert(tongzhi);
            
            // 这里可以添加实际的通知发送逻辑，比如短信、邮件等
            // 模拟发送成功
            System.out.println("发送通知成功: " + tongzhi.getTongzhibianhao());
            
            // 更新发送状态为成功
            tongzhi.setFafangzhuangtai("成功");
            jiuzhentongzhiService.updateById(tongzhi);
            
            return true;
        } catch (Exception e) {
            // 记录发送失败的日志
            System.err.println("发送通知失败: " + e.getMessage());
            
            // 更新发送状态为失败
            tongzhi.setFafangzhuangtai("失败");
            tongzhi.setShibaireason(e.getMessage());
            tongzhi.setChongshishu(1);
            jiuzhentongzhiService.updateById(tongzhi);
            
            return false;
        }
    }
    
    @Override
    public boolean retrySendNotification(Long tongzhiId) {
        try {
            JiuzhentongzhiEntity tongzhi = jiuzhentongzhiService.selectById(tongzhiId);
            if (tongzhi != null) {
                // 检查重试次数，防止无限重试
                if (tongzhi.getChongshishu() >= 3) {
                    System.err.println("通知重试次数已达上限: " + tongzhi.getTongzhibianhao());
                    return false;
                }
                
                // 更新重试次数
                tongzhi.setChongshishu(tongzhi.getChongshishu() + 1);
                tongzhi.setFafangzhuangtai("重试中");
                jiuzhentongzhiService.updateById(tongzhi);
                
                // 重新发送通知
                System.out.println("重试发送通知: " + tongzhi.getTongzhibianhao());
                // 这里可以添加实际的通知发送逻辑
                
                // 模拟发送成功
                tongzhi.setFafangzhuangtai("成功");
                tongzhi.setShibaireason(null);
                jiuzhentongzhiService.updateById(tongzhi);
                
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("重试发送通知失败: " + e.getMessage());
            
            // 更新状态为失败
            JiuzhentongzhiEntity tongzhi = jiuzhentongzhiService.selectById(tongzhiId);
            if (tongzhi != null) {
                tongzhi.setFafangzhuangtai("失败");
                tongzhi.setShibaireason(e.getMessage());
                jiuzhentongzhiService.updateById(tongzhi);
            }
            
            return false;
        }
    }
    
    @Override
    public List<JiuzhentongzhiEntity> getFailedNotifications() {
        // 查询发送失败的通知
        com.baomidou.mybatisplus.mapper.EntityWrapper<JiuzhentongzhiEntity> ew = new com.baomidou.mybatisplus.mapper.EntityWrapper<>();
        ew.eq("fafangzhuangtai", "失败");
        return jiuzhentongzhiService.selectList(ew);
    }
    
    /**
     * 创建所有后续提醒通知
     */
    private List<JiuzhentongzhiEntity> createAllNotifications(YishengyuyueEntity yishengyuyue) {
        List<JiuzhentongzhiEntity> notifications = new ArrayList<>();
        
        // 1. 预约成功通知
        JiuzhentongzhiEntity successNotification = new JiuzhentongzhiEntity();
        successNotification.setTongzhibianhao(System.currentTimeMillis() + "");
        successNotification.setYishengzhanghao(yishengyuyue.getYishengzhanghao());
        successNotification.setDianhua(yishengyuyue.getDianhua());
        successNotification.setJiuzhenshijian(yishengyuyue.getYuyueshijian());
        successNotification.setTongzhishijian(new Date());
        successNotification.setZhanghao(yishengyuyue.getZhanghao());
        successNotification.setShouji(yishengyuyue.getShouji());
        successNotification.setTongzhibeizhu("预约成功，请注意按时就诊");
        notifications.add(successNotification);
        
        // 2. 就诊前提醒通知（提前1天）
        JiuzhentongzhiEntity reminderNotification = new JiuzhentongzhiEntity();
        reminderNotification.setTongzhibianhao(System.currentTimeMillis() + "_reminder");
        reminderNotification.setYishengzhanghao(yishengyuyue.getYishengzhanghao());
        reminderNotification.setDianhua(yishengyuyue.getDianhua());
        reminderNotification.setJiuzhenshijian(yishengyuyue.getYuyueshijian());
        reminderNotification.setTongzhishijian(new Date());
        reminderNotification.setZhanghao(yishengyuyue.getZhanghao());
        reminderNotification.setShouji(yishengyuyue.getShouji());
        reminderNotification.setTongzhibeizhu("明天将进行就诊，请做好准备");
        notifications.add(reminderNotification);
        
        // 3. 就诊当天提醒通知（提前2小时）
        JiuzhentongzhiEntity dayNotification = new JiuzhentongzhiEntity();
        dayNotification.setTongzhibianhao(System.currentTimeMillis() + "_day");
        dayNotification.setYishengzhanghao(yishengyuyue.getYishengzhanghao());
        dayNotification.setDianhua(yishengyuyue.getDianhua());
        dayNotification.setJiuzhenshijian(yishengyuyue.getYuyueshijian());
        dayNotification.setTongzhishijian(new Date());
        dayNotification.setZhanghao(yishengyuyue.getZhanghao());
        dayNotification.setShouji(yishengyuyue.getShouji());
        dayNotification.setTongzhibeizhu("您的就诊将在2小时后开始，请准时到达");
        notifications.add(dayNotification);
        
        return notifications;
    }
}