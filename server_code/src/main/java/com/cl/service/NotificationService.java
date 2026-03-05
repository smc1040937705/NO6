package com.cl.service;

import com.cl.entity.TongzhijiluEntity;
import com.cl.entity.YishengyuyueEntity;
import com.cl.entity.JiuzhentongzhiEntity;

import java.util.Date;
import java.util.List;

/**
 * 通知服务接口
 * 用于处理预约成功后的自动通知发送
 */
public interface NotificationService {

    /**
     * 预约成功后立即发送所有后续提醒通知
     * @param yuyue 预约信息
     */
    void sendAppointmentNotifications(YishengyuyueEntity yuyue);

    /**
     * 发送单条通知
     * @param tongzhijilu 通知记录
     * @return 是否发送成功
     */
    boolean sendSingleNotification(TongzhijiluEntity tongzhijilu);

    /**
     * 重试发送失败的通知
     * @param tongzhijilu 通知记录
     * @return 是否发送成功
     */
    boolean retryNotification(TongzhijiluEntity tongzhijilu);

    /**
     * 获取用户的未接收通知列表
     * @param zhanghao 用户账号
     * @return 通知列表
     */
    List<TongzhijiluEntity> getUnreadNotifications(String zhanghao);

    /**
     * 标记通知为已接收
     * @param id 通知记录ID
     */
    void markAsReceived(Long id);

    /**
     * 标记通知为已读
     * @param id 通知记录ID
     */
    void markAsRead(Long id);

    /**
     * 生成通知编号
     * @return 通知编号
     */
    String generateNotificationNo();

    /**
     * 创建预约成功通知
     * @param yuyue 预约信息
     * @return 通知记录
     */
    TongzhijiluEntity createAppointmentSuccessNotification(YishengyuyueEntity yuyue);

    /**
     * 创建就诊前提醒通知（提前1天）
     * @param yuyue 预约信息
     * @return 通知记录
     */
    TongzhijiluEntity createPreVisitReminderNotification(YishengyuyueEntity yuyue);

    /**
     * 创建就诊当天提醒通知
     * @param yuyue 预约信息
     * @return 通知记录
     */
    TongzhijiluEntity createSameDayReminderNotification(YishengyuyueEntity yuyue);

    /**
     * 同步创建就诊通知到jiuzhentongzhi表
     * @param yuyue 预约信息
     * @return 就诊通知实体
     */
    JiuzhentongzhiEntity syncCreateVisitNotification(YishengyuyueEntity yuyue);
}
