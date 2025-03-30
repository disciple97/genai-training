package com.epam.training.gen.ai.service.plugin;

import com.microsoft.semantickernel.semanticfunctions.annotations.DefineKernelFunction;
import com.microsoft.semantickernel.semanticfunctions.annotations.KernelFunctionParameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class DateTimePlugin {

    private static final String DATE_FORMAT = "eeee, d MMMM yyyy";
    private static final String TIME_FORMAT = "HH:mm:ss";

    private static String getLocalizedDateOrTime(String format, String locale, String timeZone) {
        return DateTimeFormatter
                .ofPattern(format)
                .withLocale(StringUtils.parseLocale(locale))
                .format(ZonedDateTime.now(ZoneId.of(timeZone)));
    }

    @DefineKernelFunction(name = "date", description = "Get current date for provided locale and time zone")
    public String date(
            @KernelFunctionParameter(name = "locale", description = "Valid Java locale based on user input") String locale,
            @KernelFunctionParameter(name = "time_zone", description = "Valid Java time zone based on user input") String timeZone
    ) {
        log.trace("Requested date for {} locale and {} time zone", locale, timeZone);
        var date = getLocalizedDateOrTime(DATE_FORMAT, locale, timeZone);
        log.trace("Current date: {}", date);
        return date;
    }

    @DefineKernelFunction(name = "time", description = "Get current time for provided locale and time zone")
    public String time(
            @KernelFunctionParameter(name = "locale", description = "Valid Java locale based on user input") String locale,
            @KernelFunctionParameter(name = "time_zone", description = "Valid Java time zone based on user input") String timeZone
    ) {
        log.trace("Requested time for {} locale and {} time zone", locale, timeZone);
        var time = getLocalizedDateOrTime(TIME_FORMAT, locale, timeZone);
        log.trace("Current time: {}", time);
        return time;
    }
}
