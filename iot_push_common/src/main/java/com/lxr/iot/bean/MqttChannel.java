package com.lxr.iot.bean;/**
 * Created by wangcy on 2017/11/21.
 */

import com.lxr.iot.enums.SessionStatus;
import com.lxr.iot.enums.SubStatus;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * channel 封装类
 *
 * @author lxr
 * @create 2017-11-21 14:04
 **/
@Builder
@Getter
@Setter
public class MqttChannel {

    private transient  volatile  Channel channel;


    private String deviceId;


    private boolean isWill;


    private volatile SubStatus subStatus; // 是否订阅过主题


    private  Set<String> topic  ;



    private volatile SessionStatus sessionStatus;  // 在线 - 离线



    private volatile boolean cleanSession; // 当为 true 时 channel close 时 从缓存中删除  此channel


    private volatile boolean flag; // 锁  取消订阅时使用


    private ConcurrentHashMap<Integer,ConfirmMessage>  message ; // messageId - message(qos1)  // 待确认消息


    private Set<Integer>  receive;

    public void  addRecevice(int messageId){
        receive.add(messageId);
    }

    public boolean  checkRecevice(int messageId){
       return  receive.contains(messageId);
    }

    public boolean  removeRecevice(int messageId){
        return receive.remove(messageId);
    }


    public void addConfirmMsg(int messageId,ConfirmMessage msg){
        message.put(messageId,msg);
    }


    public ConfirmMessage getConfirmMsg(int messageId){
        return  message.get(messageId);
    }


    public  void removeConfigMsg(int messageId){
        message.remove(messageId);
    }


    /**
     * 判断当前channel 是否登录过
     * @return
     */
    public boolean isLogin(){
        if(this.channel!=null){
            AttributeKey<Boolean> _login = AttributeKey.valueOf("login");
            return channel.isActive() && channel.hasAttr(_login);
        }
        return false;
    }

    /**
     * 非正常关闭
     */
    public void close(){
        Optional.ofNullable(this.channel).ifPresent(channel1 -> channel1.close());
    }

    /**
     *  通道是否活跃
     * @return
     */
    public  boolean isActive(){
        return  channel!=null&&this.channel.isActive();
    }



    public boolean addTopic(Set<String> topics){
        return topic.addAll(topics);
    }


}
