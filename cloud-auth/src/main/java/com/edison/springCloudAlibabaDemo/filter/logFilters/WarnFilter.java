package com.edison.springCloudAlibabaDemo.filter.logFilters;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

/**
 * 堆栈日志过滤器.
 * <p>
 * ERROR级别，且没有捕获异常时输出到ERROR.log
 * @author yangbo
 *
 */
public class WarnFilter extends Filter<ILoggingEvent>
{

  @Override
  public FilterReply decide(ILoggingEvent event)
  {
    //不是ERROR级别，直接抛弃
    if( !event.getLevel().equals(Level.WARN) )
      return FilterReply.DENY;

    //捕获异常后输出到堆栈日志，否则输出到错误日志
    IThrowableProxy itp = event.getThrowableProxy();
    if(itp!=null)
      return FilterReply.ACCEPT;
    else
      return FilterReply.DENY;
  }

}
