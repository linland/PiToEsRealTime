package com.link.pi.method;

import com.link.entity.PIClient;
import com.link.entity.PITIMESTAMP;
import com.sun.jna.ptr.IntByReference;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @Description: ${description}
 * @Date: 2019-04-12 15:37
 */
@Component
public class PiMethod {
  private Logger logger = LoggerFactory.getLogger(PiMethod.class);
  /**
   * PI系统日志
   * @param state
   * @param filter
   * @return
   */
  public String getMessage(int state,String filter){
    byte[] msg = new byte[50];
    IntByReference msglen = new IntByReference(50);
    PIClient.INSTANCE.piut_strerror(state,msg,msglen,filter);
    String message = new String(msg);
    return message;
  }

  /**
   * 按天拆分
   */

  public List<String> getKeyValueForDate(String startTime, String endTime) throws ParseException {
    List<String> date = new ArrayList<String>();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Date startDate = sdf.parse(startTime);
    Date endDate = sdf.parse(endTime);
    date.add(sdf.format(startDate));
    Calendar calBegin = Calendar.getInstance();
    // 使用给定的 Date 设置此 Calendar 的时间
    calBegin.setTime(startDate);
    Calendar calEnd = Calendar.getInstance();
    // 使用给定的 Date 设置此 Calendar 的时间
    calEnd.setTime(endDate);
    // 测试此日期是否在指定日期之后
    while (endDate.after(calBegin.getTime())) {
      // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
      calBegin.add(Calendar.DAY_OF_MONTH, 1);
      date.add(sdf.format(calBegin.getTime()));
    }
    return date;
  }

  public PITIMESTAMP setPITimeStamp(String timestamp) throws ParseException {
    PITIMESTAMP timeStruct = new PITIMESTAMP();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    Date time = sdf.parse(timestamp);
    Calendar cal = Calendar.getInstance();
    cal.setTime(time);
    int y, m, d, h, min, s, mm;

    y = cal.get(Calendar.YEAR);
    m = cal.get(Calendar.MONTH) + 1;
    d = cal.get(Calendar.DATE);
    h = cal.get(Calendar.HOUR_OF_DAY);
    min = cal.get(Calendar.MINUTE);
    s = cal.get(Calendar.SECOND);
    mm = cal.get(Calendar.MILLISECOND);
    double ms = Double.parseDouble(s + "." + mm);
    timeStruct.year = y;
    timeStruct.month = m;
    timeStruct.day = d;
    timeStruct.hour = h;
    timeStruct.minute = min;
    timeStruct.second = ms;
    timeStruct.tzinfo = 436096;
    return timeStruct;
  }

  /**
   * 获取PITIMESTAMP时间
   *
   * @param value
   * @return
   * @throws ParseException
   */
  public String getESTime(PITIMESTAMP value) {
    //com.linsdom.pi.client.PITIMESTAMP.ByValue timestamp = value;
    int year = value.year;
    int month = value.month;
    int day = value.day;
    int hour = value.hour;
    int minute = value.minute;

    DecimalFormat df = new DecimalFormat("#.000");
    String second = df.format(value.second);
    if (value.second < 1) {
      second = "00" + second;
    }
    String timestamp = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
    try {
      Date current = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(timestamp);
      String time = DateFormatUtils.format(current, "yyyy-MM-dd'T'HH:mm:ss.SSS'+08:00'");
      return time;
    } catch (Exception e) {
      logger.error(timestamp + "转化失败", e);
    }
    return null;
  }

}
