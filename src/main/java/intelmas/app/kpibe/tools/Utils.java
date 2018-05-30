package intelmas.app.kpibe.tools;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.StringUtils;

public class Utils {

	//public static final String TIMEZONE = "Etc/GMT+4";
	public static final ZoneId TIMEZONE = ZoneId.systemDefault();
	
	
	public static String removeNewLine(String rawString){
		return StringUtils.replaceAll(rawString, "[\n\r]", " ");
	}
	
	private static DateTimeFormatter localDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
	
	public static String timestampToString(Timestamp timestamp){
		Instant datetimeInstant = timestamp.toInstant();
		ZoneId zoneId = Utils.TIMEZONE;
		ZonedDateTime zdt = ZonedDateTime.ofInstant( datetimeInstant , zoneId );
		
		return zdt.format(localDateFormatter);
	}
	
	public static String localdatetimeToString(LocalDateTime datetime){
		ZonedDateTime zdt = datetime.atZone(Utils.TIMEZONE);
		return zdt.format(localDateFormatter);
	}
	
	public static String timestampToDateString(Timestamp timestamp){
		Instant datetimeInstant = timestamp.toInstant();
		ZoneId zoneId = Utils.TIMEZONE;
		ZonedDateTime zdt = ZonedDateTime.ofInstant( datetimeInstant , zoneId );
		
		return zdt.format(DateTimeFormatter.ofPattern("yyyyMMddHH"));
	}
	
	public static String localDateTimeToDateString(LocalDateTime localDateTime){
		ZonedDateTime zdt = ZonedDateTime.of(localDateTime, Utils.TIMEZONE);
		return zdt.format(DateTimeFormatter.ofPattern("yyyyMMddHH"));
	}
	
	public static Timestamp hourlyKpiDateStringToTimestamp(String hourlyKpi){
		try{
			LocalDateTime localDateTime = LocalDateTime.parse(hourlyKpi, DateTimeFormatter.ofPattern("yyyyMMddHH"));
			ZonedDateTime zdt = localDateTime.atZone(Utils.TIMEZONE);
			return Timestamp.from(zdt.toInstant());
		}catch(Exception e){
			return null;
		}
	}
}
