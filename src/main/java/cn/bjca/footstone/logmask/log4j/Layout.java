package cn.bjca.footstone.logmask.log4j;

import cn.bjca.footstone.logmask.LogMask;
import cn.bjca.footstone.logmask.MaskConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

import java.util.ArrayList;
import java.util.List;

public class Layout extends PatternLayout {
  // 用于从xml接收配置，
  @Getter private String masks;
  @Getter @Setter private String others;
  @Getter private final List<MaskConfig> parsed = new ArrayList<MaskConfig>();
  @Getter @Setter private String separate = " ";

  public void setMasks(String masks) {
    this.parsed.add(parseMask(masks));
  }

  /** 所有的参数已经设置完毕后调用 */
  @Override
  public void activateOptions() {
    super.activateOptions();
  }

  private MaskConfig parseMask(String mask) {
    val parts = mask.split(separate);
    val m = new MaskConfig();

    for (val part : parts) {
      val kvs = part.split("=", 2);
      if (kvs.length == 2) {
        m.fulfil(kvs[0], kvs[1]);
      }
    }

    m.fix();

    return m;
  }

  @Override
  public String format(LoggingEvent event) {
    if (!(event.getMessage() instanceof String)) {
      return super.format(event);
    }

    val ti = event.getThrowableInformation();

    return super.format(
        new LoggingEvent(
            event.fqnOfCategoryClass,
            Logger.getLogger(event.getLoggerName()),
            event.timeStamp,
            event.getLevel(),
            LogMask.mask(parsed, event.getRenderedMessage()),
            ti != null ? ti.getThrowable() : null));
  }
}
