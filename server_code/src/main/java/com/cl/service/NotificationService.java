package com.cl.service;

import com.cl.entity.JiuzhentongzhiEntity;
import com.cl.entity.YishengyuyueEntity;

import java.util.List;

public interface NotificationService {
    /**
     * 发送所有后续提醒
     * @param yishengyuyue 预约信息
     */
    void sendAllNotifications(YishengyuyueEntity yishengyuyue);
    
    /**
     * 发送单个通知
     * @param tongzhi 通知信息
     * @return 是否发送成功
     */
    boolean sendNotification(JiuzhentongzhiEntity tongzhi);
    
    /**
     * 重试发送失败的通知
     * @param tongzhiId 通知ID
     * @return 是否重试成功
     */
    boolean retrySendNotification(Long tongzhiId);
    
    /**
     * 获取发送失败的通知列表
     * @return 失败通知列表
     */
    List<JiuzhentongzhiEntity> getFailedNotifications();
}