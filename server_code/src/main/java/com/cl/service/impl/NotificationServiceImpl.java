package com.cl.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cl.entity.JiuzhentongzhiEntity;
import com.cl.entity.TongzhijiluEntity;
import com.cl.entity.YishengyuyueEntity;
import com.cl.service.JiuzhentongzhiService;
import com.cl.service.NotificationService;
import com.cl.service.TongzhijiluService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 通知服务实现类
 */
@Service("notificationService")
public class NotificationServiceImpl implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Autowired
    private TongzhijiluService tongzhijiluService;

    @Autowired
    private JiuzhentongzhiService jiuzhentongzhiService;

    private static final int MAX_RETRY_COUNT = 3;

    @Override
    @Transactional
    public void sendAppointmentNotifications(YishengyuyueEntity yuyue) {
        try {
            logger.info("开始为用户 [{}] 发送预约通知，预约编号: {}", yuyue.getZhanghao(), yuyue.getYuyuebianhao());

            // 1. 创建并发送预约成功通知（立即发送）
            TongzhijiluEntity successNotification = createAppointmentSuccessNotification(yuyue);
            boolean successResult = sendSingleNotification(successNotification);
            logger.info("预约成功通知发送结果: {}", successResult);

            // 2. 创建就诊前提醒通知（提前1天）
            TongzhijiluEntity preReminder = createPreVisitReminderNotification(yuyue);
            // 计划发送时间早于当前时间则立即发送
            if (preReminder.getJihuafasongshijian().before(new Date())) {
                sendSingleNotification(preReminder);
            }

            // 3. 创建就诊当天提醒通知
            TongzhijiluEntity sameDayReminder = createSameDayReminderNotification(yuyue);
            // 计划发送时间早于当前时间则立即发送
            if (sameDayReminder.getJihuafasongshijian().before(new Date())) {
                sendSingleNotification(sameDayReminder);
            }

            // 4. 同步创建就诊通知到jiuzhentongzhi表（保持原有功能兼容）
            syncCreateVisitNotification(yuyue);

            logger.info("预约通知发送完成，预约编号: {}", yuyue.getYuyuebianhao());
        } catch (Exception e) {
            logger.error("发送预约通知时发生错误: {}", e.getMessage(), e);
        }
    }

    @Override
    public boolean sendSingleNotification(TongzhijiluEntity tongzhijilu) {
        try {
            // 模拟发送通知的逻辑
            // 实际项目中这里可能是调用短信API、推送服务等
            boolean sendResult = simulateSendNotification(tongzhijilu);

            if (sendResult) {
                // 发送成功
                tongzhijilu.setFasongzhuangtai("1"); // 1-发送成功
                tongzhijilu.setShijifasongshijian(new Date());
                tongzhijilu.setShibaiyuanyin(null);
            } else {
                // 发送失败
                tongzhijilu.setFasongzhuangtai("2"); // 2-发送失败
                tongzhijilu.setShibaiyuanyin("通知发送失败，网络异常或服务不可用");
                tongzhijilu.setChongshicishu(tongzhijilu.getChongshicishu() + 1);
            }

            tongzhijiluService.updateById(tongzhijilu);
            return sendResult;
        } catch (Exception e) {
            logger.error("发送通知时发生异常: {}", e.getMessage(), e);
            tongzhijilu.setFasongzhuangtai("2");
            tongzhijilu.setShibaiyuanyin("发送异常: " + e.getMessage());
            tongzhijilu.setChongshicishu(tongzhijilu.getChongshicishu() + 1);
            tongzhijiluService.updateById(tongzhijilu);
            return false;
        }
    }

    /**
     * 模拟发送通知
     * 实际项目中替换为真实的发送逻辑
     */
    private boolean simulateSendNotification(TongzhijiluEntity tongzhijilu) {
        // 模拟发送，95%成功率
        double random = Math.random();
        return random > 0.05;
    }

    @Override
    public boolean retryNotification(TongzhijiluEntity tongzhijilu) {
        if (tongzhijilu.getChongshicishu() >= MAX_RETRY_COUNT) {
            logger.warn("通知 [{}] 已达到最大重试次数，不再重试", tongzhijilu.getId());
            tongzhijilu.setShibaiyuanyin("已达到最大重试次数(" + MAX_RETRY_COUNT + "次)");
            tongzhijiluService.updateById(tongzhijilu);
            return false;
        }

        logger.info("重试发送通知 [{}]，当前重试次数: {}", tongzhijilu.getId(), tongzhijilu.getChongshicishu());

        // 重置状态
        tongzhijilu.setFasongzhuangtai("0"); // 0-待发送
        tongzhijilu.setShibaiyuanyin(null);
        tongzhijiluService.updateById(tongzhijilu);

        return sendSingleNotification(tongzhijilu);
    }

    @Override
    public List<TongzhijiluEntity> getUnreadNotifications(String zhanghao) {
        EntityWrapper<TongzhijiluEntity> wrapper = new EntityWrapper<>();
        wrapper.eq("zhanghao", zhanghao);
        wrapper.eq("fasongzhuangtai", "1"); // 发送成功
        wrapper.andNew()
            .eq("jieshouzhuangtai", "0") // 未接收
            .or()
            .eq("jieshouzhuangtai", "1"); // 已接收但未读
        wrapper.orderBy("jihuafasongshijian", false);
        return tongzhijiluService.selectList(wrapper);
    }

    @Override
    public void markAsReceived(Long id) {
        TongzhijiluEntity entity = tongzhijiluService.selectById(id);
        if (entity != null) {
            entity.setJieshouzhuangtai("1"); // 1-已接收
            entity.setJieshoushijian(new Date());
            tongzhijiluService.updateById(entity);
            logger.info("通知 [{}] 已标记为已接收", id);
        }
    }

    @Override
    public void markAsRead(Long id) {
        TongzhijiluEntity entity = tongzhijiluService.selectById(id);
        if (entity != null) {
            entity.setJieshouzhuangtai("2"); // 2-已读
            tongzhijiluService.updateById(entity);
            logger.info("通知 [{}] 已标记为已读", id);
        }
    }

    @Override
    public String generateNotificationNo() {
        return String.valueOf(System.currentTimeMillis());
    }

    @Override
    public TongzhijiluEntity createAppointmentSuccessNotification(YishengyuyueEntity yuyue) {
        TongzhijiluEntity notification = new TongzhijiluEntity();
        notification.setTongzhibianhao(generateNotificationNo());
        notification.setYuyuebianhao(yuyue.getYuyuebianhao());
        notification.setYishengzhanghao(yuyue.getYishengzhanghao());
        notification.setZhanghao(yuyue.getZhanghao());
        notification.setTongzhileixing(1); // 1-预约成功通知
        notification.setTongzhineirong(buildAppointmentSuccessContent(yuyue));
        notification.setJihuafasongshijian(new Date()); // 立即发送
        notification.setFasongzhuangtai("0"); // 0-待发送
        notification.setChongshicishu(0);
        notification.setJieshouzhuangtai("0"); // 0-未接收

        tongzhijiluService.insert(notification);
        return notification;
    }

    @Override
    public TongzhijiluEntity createPreVisitReminderNotification(YishengyuyueEntity yuyue) {
        TongzhijiluEntity notification = new TongzhijiluEntity();
        notification.setTongzhibianhao(generateNotificationNo());
        notification.setYuyuebianhao(yuyue.getYuyuebianhao());
        notification.setYishengzhanghao(yuyue.getYishengzhanghao());
        notification.setZhanghao(yuyue.getZhanghao());
        notification.setTongzhileixing(2); // 2-就诊前提醒
        notification.setTongzhineirong(buildPreVisitReminderContent(yuyue));

        // 计划发送时间：就诊时间前1天
        Date yuyueTime = yuyue.getYuyueshijian();
        Calendar cal = Calendar.getInstance();
        cal.setTime(yuyueTime);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        notification.setJihuafasongshijian(cal.getTime());

        notification.setFasongzhuangtai("0"); // 0-待发送
        notification.setChongshicishu(0);
        notification.setJieshouzhuangtai("0"); // 0-未接收

        tongzhijiluService.insert(notification);
        return notification;
    }

    @Override
    public TongzhijiluEntity createSameDayReminderNotification(YishengyuyueEntity yuyue) {
        TongzhijiluEntity notification = new TongzhijiluEntity();
        notification.setTongzhibianhao(generateNotificationNo());
        notification.setYuyuebianhao(yuyue.getYuyuebianhao());
        notification.setYishengzhanghao(yuyue.getYishengzhanghao());
        notification.setZhanghao(yuyue.getZhanghao());
        notification.setTongzhileixing(3); // 3-就诊当天提醒
        notification.setTongzhineirong(buildSameDayReminderContent(yuyue));

        // 计划发送时间：就诊当天早上8点
        Date yuyueTime = yuyue.getYuyueshijian();
        Calendar cal = Calendar.getInstance();
        cal.setTime(yuyueTime);
        cal.set(Calendar.HOUR_OF_DAY, 8);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        notification.setJihuafasongshijian(cal.getTime());

        notification.setFasongzhuangtai("0"); // 0-待发送
        notification.setChongshicishu(0);
        notification.setJieshouzhuangtai("0"); // 0-未接收

        tongzhijiluService.insert(notification);
        return notification;
    }

    @Override
    public JiuzhentongzhiEntity syncCreateVisitNotification(YishengyuyueEntity yuyue) {
        JiuzhentongzhiEntity jiuzhentongzhi = new JiuzhentongzhiEntity();
        jiuzhentongzhi.setTongzhibianhao(generateNotificationNo());
        jiuzhentongzhi.setYishengzhanghao(yuyue.getYishengzhanghao());
        jiuzhentongzhi.setDianhua(yuyue.getDianhua());
        jiuzhentongzhi.setJiuzhenshijian(yuyue.getYuyueshijian());
        jiuzhentongzhi.setTongzhishijian(new Date());
        jiuzhentongzhi.setZhanghao(yuyue.getZhanghao());
        jiuzhentongzhi.setShouji(yuyue.getShouji());
        jiuzhentongzhi.setTongzhibeizhu("您的预约已成功，就诊时间为：" + formatDate(yuyue.getYuyueshijian()));

        jiuzhentongzhiService.insert(jiuzhentongzhi);
        return jiuzhentongzhi;
    }

    private String buildAppointmentSuccessContent(YishengyuyueEntity yuyue) {
        StringBuilder sb = new StringBuilder();
        sb.append("【预约成功】\n");
        sb.append("尊敬的").append(yuyue.getZhanghao()).append("，您的预约已成功！\n");
        sb.append("预约编号：").append(yuyue.getYuyuebianhao()).append("\n");
        sb.append("医生账号：").append(yuyue.getYishengzhanghao()).append("\n");
        sb.append("就诊时间：").append(formatDate(yuyue.getYuyueshijian())).append("\n");
        sb.append("我们会提前发送提醒通知，请留意。");
        return sb.toString();
    }

    private String buildPreVisitReminderContent(YishengyuyueEntity yuyue) {
        StringBuilder sb = new StringBuilder();
        sb.append("【就诊提醒】\n");
        sb.append("尊敬的").append(yuyue.getZhanghao()).append("，提醒您：\n");
        sb.append("您预约的就诊将在明天进行。\n");
        sb.append("预约编号：").append(yuyue.getYuyuebianhao()).append("\n");
        sb.append("医生账号：").append(yuyue.getYishengzhanghao()).append("\n");
        sb.append("就诊时间：").append(formatDate(yuyue.getYuyueshijian())).append("\n");
        sb.append("请准时到达医院就诊。");
        return sb.toString();
    }

    private String buildSameDayReminderContent(YishengyuyueEntity yuyue) {
        StringBuilder sb = new StringBuilder();
        sb.append("【今日就诊提醒】\n");
        sb.append("尊敬的").append(yuyue.getZhanghao()).append("，提醒您：\n");
        sb.append("您今天有预约的就诊。\n");
        sb.append("预约编号：").append(yuyue.getYuyuebianhao()).append("\n");
        sb.append("医生账号：").append(yuyue.getYishengzhanghao()).append("\n");
        sb.append("就诊时间：").append(formatDate(yuyue.getYuyueshijian())).append("\n");
        sb.append("请准时到达医院就诊，如有变动请及时联系。");
        return sb.toString();
    }

    private String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }
}
