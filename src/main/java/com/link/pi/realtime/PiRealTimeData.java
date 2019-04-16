package com.link.pi.realtime;

import com.alibaba.fastjson.JSONObject;
import com.link.entity.PIClient;
import com.link.entity.PI_EVENT;
import com.link.entity.PIvaluetype;
import com.link.pi.method.PiMethod;
import com.sun.jna.ptr.IntByReference;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisCluster;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Description: 实时数据导入
 * @Date: 2019-04-12 14:51
 */
@Component
public class PiRealTimeData {
  private Logger logger = LoggerFactory.getLogger(PiRealTimeData.class);

  @Autowired
  private JedisCluster jedisCluster;
  @Autowired
  private JdbcTemplate jdbcTemplate;
  @Autowired
  private KafkaProducer<String, String> kafkaProducer;
  @Autowired
  private PiMethod piMethod;

  @Value("${pidata.ip}")
  private String piServer;
  @Value("${pidata.plant}")
  private String plant;
  @Value("${pidata.table}")
  private String table;
  @Value("${kafka.topic}")
  private String topic;

  public void piRealTimeData() throws InterruptedException {
    //断档开始时间
    String startTime = "";
    //断档结束时间
    String endTime = "";
    int piConnectStatus = PIClient.INSTANCE.piut_setservernode(piServer);
    logger.info("与pi服务器建立远程连接 :" + piMethod.getMessage(piConnectStatus, "piut_setservernode"));
    //1.无跳出循环访问Pi数据库
    while (true) {
      if (piConnectStatus == 0) {
        //2.通过redis客户端传入plant获取 pi_identification数组
        String ptRedis = jedisCluster.get(plant);
        List<Object> list = JSONObject.parseArray((JSONObject.parse(ptRedis).toString()));
        Integer[] pointId = list.toArray(new Integer[]{});
        int[] piTagArray = Arrays.stream(pointId).mapToInt(Integer::valueOf).toArray();

        logger.info("读取pi测点信息 count : " + piTagArray.length);

        //设置PiStateFlag初始值为0
        jedisCluster.set("PiStateFlag", "0");

        int piTagCount = piTagArray.length;//pi测点数量

        /**
         * 注册事件方式读取PI数据测点清单
         */
        IntByReference conn_count = null;
        conn_count = new IntByReference(piTagCount);
        piConnectStatus = PIClient.INSTANCE.pisn_evmestablish(conn_count, piTagArray);

        int funccode = 0;//GETFIRST
        final int evm_count = piTagCount * 10;

        if (piConnectStatus == 0) { //pi测点注册成功
          logger.info("pi点注册成功   count : " + conn_count.getValue());
          IntByReference count = null;
          IntByReference ptnum = null;
          PI_EVENT.ByReference event = null;

          while (true) {
            /**
             * 判断是否重新注册
             * 	0  不注册
             *  1 注册
             */
            if ("1".equals(jedisCluster.get("PiStateFlag"))) {
              PIClient.INSTANCE.pisn_evmdisestablish(conn_count, piTagArray);
              logger.error("重新注册,删除注册信息  count is " + conn_count.getValue() + " PiStateFlag is " + jedisCluster.get("PiStateFlag") + "  hashCode is " + jedisCluster.get("PiStateFlag").hashCode() + " :" + piMethod.getMessage(piConnectStatus, "pisn_evmexceptionsx"));
              list.clear();
              break;
            }
            count = new IntByReference(evm_count); //监听到多少个测点发生变化
            ptnum = new IntByReference();  //变化的测点id
            event = new PI_EVENT.ByReference(); //C语言的结构体-变化的测点数据
            /**
             * 读取PI事件数据(一组数据,时间精度到毫秒)
             * funccode:
             * 		GETFIRST(0):从PI系统获取数据并返回第一个PI点的数据
             *  	GETNEXT(1):返回下一个PI点的数据
             *  	GETSAME(2):返回与上次使用GETNEXT或GETFIRST调用返回的数据值相同的数据值
             */
            piConnectStatus = PIClient.INSTANCE.pisn_evmexceptionsx(count, ptnum, event, funccode);

            JSONObject jsonObject = null;
            while (piConnectStatus == 0 || piConnectStatus == -15010) {
              //从redis获取测点属性 （key为 pointid_plant）
              jsonObject = getESData(JSONObject.parseObject(JSONObject.parse(jedisCluster.get(ptnum.getValue() + "_" + plant)).toString()), event);
              kafkaProducer.send(new ProducerRecord<String, String>(topic, jsonObject.toString()));
              //	logger.info("查询成功  : "+ context.toJSONString()+" count: "+count.getValue());
              funccode = 1;//GETNEXT
              piConnectStatus = PIClient.INSTANCE.pisn_evmexceptionsx(count, ptnum, event, funccode);
            }
            if (piConnectStatus == 100) {
              funccode = 0;
              count = null;
              ptnum = null;
              event = null;
            } else {
              startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
              //停止异常报告,删除注册信息
              PIClient.INSTANCE.pisn_evmdisestablish(conn_count, piTagArray);
              //断开连接
              PIClient.INSTANCE.piut_disconnect();
              logger.error("查询失败 , 删除注册信息,count is " + conn_count.getValue() + " :" + piMethod.getMessage(piConnectStatus, "pisn_evmexceptionsx"));
              break;
            }
          }
        } else { //pi测点注册失败
          logger.error("pi点注册失败 : " + piMethod.getMessage(piConnectStatus, "pisn_evmestablish"));
        }
      } else {
        piConnectStatus = PIClient.INSTANCE.piut_setservernode(piServer);
        logger.error("与pi服务器重新建立连接 : " + piMethod.getMessage(piConnectStatus, "piut_setservernode"));
        //PI数据库断开连接,等待网络完全恢复
        Thread.sleep(1000);
        if (piConnectStatus == 0) {
          //记录网络中断时间
          endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
          jdbcTemplate.update("insert into " + table + " (plant,starttime,endtime,state) values ('" + plant + "','" + startTime + "','" + endTime + "',0)");
        }
      }
    }
  }

  /**
   * @param attr  测点属性（redis获取）
   * @param event 监听事件
   * @return ES数据（@timestamp,original_name,listener_time,value,istat,flag,quality）
   */
  public JSONObject getESData(JSONObject attr, PI_EVENT.ByReference event) {
    JSONObject context = new JSONObject();

    String time = piMethod.getESTime(event.timestamp);
    context.put("@timestamp", time);
    context.put("original_name", attr.get("originalName"));
    String listener_time = DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd'T'HH:mm:ss.SSS'+08:00'");
    context.put("listener_time", listener_time);
    String type = PIvaluetype.getByValue(event.typex).toString();
    String value = "";
    int istat = event.istat;
    int quality = 1;
    if (istat == 0) {
      quality = 0;
    } else {
      quality = 1;
    }
    if (type.startsWith("Float")) {
      //模拟量
      value = String.valueOf(event.drval);
    } else if ("Digital".equals(type)) {
      //开关量
      IntByReference digcode = new IntByReference();
      IntByReference dignumb = new IntByReference();
      int pt = attr.getInteger("piIdentification").intValue();
      int ret = PIClient.INSTANCE.pipt_digpointers(pt, digcode, dignumb);
      int digitalSet = Math.abs(Math.abs(istat) - Math.abs(digcode.getValue()));
      //获取测点 PI System State（key为 plant_digitalset）
      String statesRedis = jedisCluster.get(plant + "_" + attr.get("digitalset"));
      List<Object> list = JSONObject.parseArray((JSONObject.parse(statesRedis).toString()));
      if (statesRedis != null && list != null) {
        if (list.contains(digitalSet)) {
          value = String.valueOf(digitalSet);
          quality = 0;
        } else {
          value = "0";
          quality = 1;
        }
      } else {
        logger.error("tag: " + context.toString() + "   DigitalSet不存在:" + attr.get("digitalset"));
      }
    } else if (type.startsWith("Int")) {
      value = String.valueOf(event.ival);
    } else if ("String".equals(type)) {
      int size = event.bsize;
      value = new String(event.bval.getByteArray(0, size));

    } else {
      logger.error("  time:" + time + "," + attr.get("originalName") + "插入失败, " + attr.get("identifier") + "(" + event.typex + ")类型不匹配!");
    }
    context.put("value", value);
    context.put("istat", istat);
    context.put("flag", event.flags);
    context.put("quality", quality);
    return context;
  }
}
