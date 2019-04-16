package com.link.run;

import com.link.pi.realtime.PiRealTimeData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @Description: ${实时数据导入,pi-kafka}
 * @Date: 2019-04-11 9:43
 */
@Component
public class RunController {

  @Autowired
  private PiRealTimeData piRealTimeData;

  @PostConstruct
  public void run() throws Exception {

    //2.调用Pi的实时数据过程
    piRealTimeData.piRealTimeData();
  }
}
