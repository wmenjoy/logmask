package cn.bjca.footstone.logmask;

import lombok.Data;
import lombok.val;

import java.util.regex.Pattern;

/**
 * 掩码配置类。
 *
 * @author bingoobjca
 */
@Data
public class MaskConfig {
  private String pattern;
  private Pattern compiled;

  private int leftKeep;
  private int rightKeep;
  private String keep;
  private String mask;
  private boolean keepMasksLength;
  private boolean boundary;
  private String keys;

  public MaskConfig fix() {
    if (this.mask == null) {
      this.mask = "...";
    }

    if (this.pattern != null) {
      String p = pattern;
      if (boundary) {
        p = "\\b" + pattern + "\\b";
      }
      this.compiled = Pattern.compile(p, Pattern.MULTILINE);
    }

    return this;
  }

  public String replace(String s) {
    val maskLength = s.length() - leftKeep - rightKeep;

    if (s.length() < mask.length() || maskLength <= 0) {
      return mask;
    }

    val sb = new StringBuilder();

    if (leftKeep > 0) {
      sb.append(s, 0, leftKeep);
    }

    if (keepMasksLength) {
      for (int i = 0, j = maskLength / mask.length(); i < j; i++) {
        sb.append(mask);
      }
    } else {
      sb.append(mask);
    }

    if (rightKeep > 0) {
      sb.append(s.substring(s.length() - rightKeep));
    }

    return sb.toString();
  }

  public void setKeep(String keep) {
    val parts = keep.split(",");
    if (parts.length == 1) {
      int v = Integer.parseInt(parts[0]);
      this.leftKeep = v;
      this.rightKeep = v;
    } else if (parts.length >= 2) {
      this.leftKeep = Integer.parseInt(parts[0]);
      this.rightKeep = Integer.parseInt(parts[1]);
    }
  }

  public void fulfil(String k, String v) {
    if (k.equals("pattern")) {
      setPattern(v);
    } else if (k.equals("leftKeep")) {
      setLeftKeep(Integer.parseInt(v));
    } else if (k.equals("rightKeep")) {
      setRightKeep(Integer.parseInt(v));
    } else if (k.equals("keep")) {
      setKeep(v);
    } else if (k.equals("mask")) {
      setMask(v);
    } else if (k.equals("keepMasksLength")) {
      setKeepMasksLength(Boolean.parseBoolean(v));
    } else if (k.equals("boundary")) {
      setBoundary(Boolean.parseBoolean(v));
    } else if (k.equals("keys")) {
      setKeys(v);
    }
  }
}
