package com.link.entity;

import com.sun.jna.*;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.ShortByReference;

/**
 * 
 * PI-API Java客户端，映射了所有的API函数.
 *
 * @author <a href="mailto:lingh@linsdom.com">lingh</a>
 */
public interface PIClient extends Library {

	PIClient INSTANCE = (PIClient) Native.loadLibrary((Platform.isWindows() ? "piapi32" : "piapi"), PIClient.class);

	/** Archive Functions **/

	/**
	 * 
	 * This function returns evenly spaced values by calculating the result of
	 * the passed expression for the times from times[0] to times[count-1].
	 *
	 * @param count
	 *            Number of values
	 * @param times
	 *            Time stamps of values. Pass the start time in times[0] and the
	 *            end time in times[count-1]. If you set times[count-1] to zero,
	 *            PI will use the current time.
	 * @param rvals
	 *            Values in engineering units
	 * @param istats
	 *            Integer or status values
	 * @param calcStr
	 *            Calculation string (equation) to evaluate, in PI performance
	 *            equation syntax.
	 * @return >0 System error <br>
	 *         0 Success <br>
	 *         -105 Last time not after first time or first time is <= 0 <br>
	 *         -121 Invalid count parameter <br>
	 *         -8xx Expression parsing error <br>
	 *         -996 Message too big for PINet protocol <br>
	 *         -998 Memory allocation error <br>
	 * 
	 */
	int piar_calculation(IntByReference count, int[] times, float[] rvals, int[] istats, String calcStr);

	/**
	 * 
	 * This function returns events from the Archive for the passed point number
	 * starting at the time in the first element in the times array, times[0],
	 * and traversing the Archive forwards and backwards through time.
	 * times[count - 1] must also be set as described below.
	 *
	 * @param pt
	 *            Point number
	 * @param count
	 *            Number of values to retrieve (passed); number of values
	 *            actually retrieved (returned)
	 * @param times
	 *            Time stamps of values. times[0] is the start time;
	 *            times[count-1] should be set to: 0 if an unbounded search is
	 *            desired; > times[0] if rev is FALSE; < times[0] if rev is
	 *            TRUE.
	 * @param rvals
	 *            Values in engineering units. For values which are not of type
	 *            real, the values returned in rvals are meaningless.
	 * @param istats
	 *            Integer or status values
	 * @param rev
	 *            Reverse sequence flag. If false (zero), search will be
	 *            forwards in time; if true (non zero), search will be
	 *            backwards.
	 * @return >0 System error <br>
	 *         0 Success <br>
	 *         -1 Bad point number <br>
	 *         -101 Date not on line <br>
	 *         -103 No data for this point during the specified time range<br>
	 *         -105 Bad time stamp<br>
	 *         -121 Invalid count parameter<br>
	 *         -996 Message too big for PINET protocol<br>
	 *         -998 Memory allocation error<br>
	 * 
	 */
	int piar_compvalues(int pt, IntByReference count, int[] times, float[] rvals, int[] istats, IntByReference rev);

	/**
	 * 
	 * Retrieve filtered, compressed Archive data.The count, start time in
	 * times[0], end time in times[count-1], and filter expression are passed to
	 * obtain an array of compressed Archive events. The rev flag is used to
	 * obtain sequential Archive events starting with times[0] and going
	 * backwards in time until count or the end time is reached. The filt flag
	 * specifies whether a FailFilterStatus or no value is returned at times
	 * when the filter expression is not satisfied. The number of values found
	 * is returned in count.
	 * 
	 * @param pt
	 *            Point number
	 * @param count
	 *            Number of values
	 * @param times
	 *            Time stamps of values. See {@link PIClient}
	 * @param rvals
	 *            Values in engineering units
	 * @param istats
	 *            Integer or status values
	 * @param expression
	 *            Expression string
	 * @param rev
	 *            Reverse sequence flag; TRUE to go backwards from times[0],
	 *            FALSE to go forwards
	 * @param filt
	 *            Filter status flag, TRUE to return FilterFailStatus and FALSE
	 *            to skip values at times when the expression is not satisfied.
	 * 
	 * @return >0 System error<br>
	 *         0 Success<br>
	 *         -1 Bad point number<br>
	 *         -101 Date not on line<br>
	 *         -103 No data for this point for the passed time<br>
	 *         -105 Bad time and date<br>
	 *         -121 Invalid count parameter<br>
	 *         -8xx Expression Parsing Error<br>
	 *         -9xx Network Error<br>
	 * 
	 */
	int piar_compvaluesfil(int pt, IntByReference count, int[] times, float[] rvals, int[] istats, String expression,
                         int rev, int filt);

	/**
	 *
	 * Delete an Archive value.This routine deletes an existing Archive value
	 * for the specified point and time.
	 *
	 * @param pt
	 *            Point number
	 * @param timeDate
	 *            Time stamp of value to delete
	 * @return >0 System error<br>
	 *         0 Success<br>
	 *         -1 Bad point number<br>
	 *         -101 Date not on-line<br>
	 *         -103 No data for this point during the specified time range<br>
	 *         -108 Time is before the editing time limit
	 *
	 */
	int piar_deletevalue(int pt, int timeDate);

	/**
	 *
	 * This function returns evenly spaced (sampled) events derived by
	 * interpolating between Archive events. The time after the first event is
	 * passed in times[0] and the time of the last event is passed in
	 * times[count-1]. If times[count-1] is 0, the current time is used for the
	 * end time.<br>
	 * <br>
	 * Interpolated values are used for resolution codes 1, 2, and 3 while the
	 * previous value is returned for resolution code 4 points. If there are two
	 * events for a single time, the first event in the Archive is returned. For
	 * points which are not of type real, rvals is meaningless and the value is
	 * returned in istats. The number of values returned is passed in count.
	 *
	 * @param pt
	 *            Point number
	 * @param count
	 *            Number of values
	 * @param times
	 *            Time stamps of values
	 * @param rvals
	 *            Values in engineering units
	 * @param istats
	 *            Integer or status values
	 * @return >0 System error <br>
	 *         0 Success <br>
	 *         -1 Bad point number <br>
	 *         -105 Bad time stamp <br>
	 *         -121 Invalid count parameter <br>
	 *         -996 Message too big for PINET protocol <br>
	 *         -998 Memory allocation error
	 *
	 *
	 */
	int piar_interpvalues(int pt, IntByReference count, int[] times, float[] rvals, int[] istats);

	/**
	 *
	 * The count, start time in times[0], end time in times[count-1], and filter
	 * expression are passed to obtain an array of sampled Archive events.
	 * Interpolated values are used for resolution codes 1, 2, and 3 while the
	 * previous value is returned for resolution code 4 points. If there are two
	 * events for a single time, the first event in the Archive is returned. For
	 * points which are not of type real, rvals is meaningless and the value is
	 * returned in istats. For requests which require interpolation, the values
	 * are first interpolated over the specified time range and the results are
	 * then filtered.
	 *
	 * @param pt
	 *            Point number
	 * @param count
	 *            Number of values
	 * @param times
	 *            Time stamps of values
	 * @param rvals
	 *            Values in engineering units
	 * @param istats
	 *            Integer or status values
	 * @param expression
	 *            Expression string
	 * @return >0 System error <br>
	 *         0 Success <br>
	 *         -1 Bad point number<br>
	 *         -105 Bad time and date<br>
	 *         -121 Invalid count parameter<br>
	 *         -8xx Expression Parsing Error<br>
	 *         -9xx Network Error
	 *
	 */
	int piar_interpvaluesfil(int pt, IntByReference count, int[] times, float[] rvals, int[] istats, String expression);

	/**
	 *
	 * This routine returns the time of the event count events from the passed
	 * time, timedate. The resulting time and the events counted are returned in
	 * timedate and count, respectively. A positive value of count specifies
	 * counting forwards in time and a negative value specifies counting
	 * backwards in time from the passed timedate. The count is not inclusive of
	 * events at the passed time. Thus if the passed time is the same as the
	 * time of an Archived event, the time of the event count events from the
	 * current event is returned.
	 *
	 * @param pt
	 *            Point number
	 * @param count
	 *            Number of values
	 * @param timeDate
	 *            Time stamp
	 * @return >0 System error <br>
	 *         0 Success<br>
	 *         -1 Bad point number<br>
	 *         -101 Date not on-line<br>
	 *         -103 No data for this point during the specified time range<br>
	 *         -105 Bad time stamp<br>
	 *         -11032 No data for this point during the specified time range<br>
	 *         -11043 Date not on-line<br>
	 *
	 */
	int piar_panvalues(int pt, IntByReference count, IntByReference timeDate);

	/**
	 *
	 * TThis function returns events from the Archive for the passed point
	 * number, start time, times[0], and end time, times[count-1]. It returns a
	 * list of events for plotting in intervals time sections. intervals should
	 * be the number of screen pixels in the time direction.<br>
	 *
	 * The events are returned as a time, a real value, and an integer status in
	 * chronological order. For points which are not of type real, the value is
	 * returned in istats and rvals is meaningless.<br>
	 *
	 * The maximum number of values to return is passed in count. The number
	 * actually returned is returned in this same variable. The maximum number
	 * of values that may be returned is five times intervals.<br>
	 *
	 * The five possible values for an interval represent the start, the end,
	 * the highest, the lowest and at most one additional exception point. If
	 * any of the values are duplicates they are omitted.
	 *
	 * @param pt
	 *            Point number
	 * @param intervals
	 *            Number of plot intervals (pixels per plot)
	 * @param count
	 *            Number of values
	 * @param times
	 *            Time stamps of values
	 * @param rvals
	 *            Values in engineering units
	 * @param istats
	 *            Integer or status values
	 * @return >0 System error<br>
	 *         0 Success<br>
	 *         -1 Bad point number<br>
	 *         -103 No data for this point during the specified time range<br>
	 *         -105 Bad time stamp<br>
	 *         -113 Count too small<br>
	 *         -121 Invalid count parameter<br>
	 *         -996 Message too big for PINET protocol<br>
	 *         -998 Memory allocation error
	 *
	 */
	int piar_plotvalues(int pt, int intervals, IntByReference count, int[] times, float[] rvals, int[] istats);

	/**
	 *
	 * This function adds a new value to the Archive, or it replaces a value if
	 * one exists at the same time stamp. <br>
	 * If the passed time is after the current Snapshot time, pisn_putsnapshot
	 * is called. Otherwise, if there is a value in the Archive for the
	 * specified time, it is replaced. <br>
	 * This function is intended to be used in place of pisn_putsnapshot for
	 * laboratory results so that corrected values can be replaced in the
	 * Archive. Typically points for lab values are configured with compression
	 * off. Edited values and out of order values bypass the compression
	 * algorithm. <br>
	 * A wait flag is provided, which if TRUE, directs the call to return when
	 * the value has been written or if over thirty seconds have elapsed. If the
	 * wait flag is FALSE, the function returns immediately.
	 *
	 * @param pt
	 *            Point number
	 * @param rval
	 *            Value in engineering units
	 * @param isStat
	 *            Integer value or status
	 * @param timeDate
	 *            Time stamp
	 * @param wait
	 *            Wait for completion flag
	 * @return>0 System error<br>
	 *           0 Success<br>
	 *           -1 Bad point number<br>
	 *           -8 Time is after current time<br>
	 *           -9 Bad integer or digital value<br>
	 *           -101 Date not on-line<br>
	 *           -108 Time is before the editing time limit<br>
	 *           -110 Queue has fallen behind<br>
	 *           -111 Timed out
	 *
	 */
	int piar_putvalue(int pt, float rval, int isStat, int timeDate, int wait);

	/**
	 *
	 * This function replaces a single Archive value for the passed time.
	 *
	 * @param pt
	 *            Point number
	 * @param timeDate
	 *            Time stamp
	 * @param rval
	 *            Value in engineering units
	 * @param isStat
	 *            Integer value or status
	 * @return >0 System error<br>
	 *         0 Success<br>
	 *         -1 Bad point number<br>
	 *         -8 Time is after current time<br>
	 *         -9 Bad integer or digital value<br>
	 *         -101 Date not on-line<br>
	 *         -103 No data for this point at this time<br>
	 *         -108 Time is before the editing time limit
	 *
	 */
	int piar_replacevalue(int pt, int timeDate, float rval, int isStat);

	/**
	 *
	 * This function calculates the Archive function specified by the code
	 * parameter from Archive data for the time between the passed starting and
	 * ending times inclusive. Pctgood returns the percentage of the given time
	 * that the point was good. A digital point always returns zero percent
	 * good.<br>
	 * <br>
	 * Bad data are ignored in the calculation. See Usage Notes for behavior
	 * specific to the Archive function parameter, code.
	 *
	 *
	 * @param pt
	 *            Point number
	 * @param time1
	 *            Starting time stamp, returned with the time of the determined
	 *            minimum or maximum value or range value
	 * @param time2
	 *            Ending time stamp, returned with the time of the range value
	 * @param rval
	 *            Calculated engineering value
	 * @param pctgood
	 *            Percent of time Archive value was good
	 * @param code
	 *            Archive function to execute (average, minimum, maximum, etc.)
	 * @return >0 System error<br>
	 *         0 Success<br>
	 *         -1 Bad point number<br>
	 *         -104 Invalid code<br>
	 *         -105 Last time not after first time or time < 0<br>
	 *         -106 No good data for this point for this time<br>
	 *         -120 Invalid code for the currently connected server.
	 *
	 */
	int piar_summary(int pt, IntByReference time1, IntByReference time2, FloatByReference rval,
                   FloatByReference pctgood, int code);

	/**
	 *
	 * This function returns values for the times in the passed array. The
	 * values are derived by interpolating between Archive events for resolution
	 * codes 1, 2, and 3 and the previous value is used for resolution code 4.
	 * The time of the first value is passed in times[0] and succeeding value
	 * times in the rest of the times array. The times must be in ascending
	 * order.<br>
	 * <br>
	 * If there is more than one event at the desired time, the first event in
	 * the Archive is the one returned. For points which are not of type real,
	 * the value is returned in istats and rvals is meaningless. The number of
	 * values to return is passed in count. The prev flag, when used with
	 * resolution code 4 tags, indicates the times of the events are to be
	 * returned.
	 *
	 *
	 * @param pt
	 *            Point number
	 * @param count
	 *            Number of values
	 * @param times
	 *            Time stamps of values (returned only if the prev flag is true
	 *            for resolution code 4 tags)
	 * @param rvals
	 *            Values in engineering units
	 * @param istats
	 *            Integer or status values
	 * @param prev
	 *            Previous value flag (if true causes return of actual event
	 *            times for resolution code 4 points)
	 * @return >0 System error<br>
	 *         0 Success<br>
	 *         -1 Bad point number<br>
	 *         -105 Bad time stamp<br>
	 *         -121 Invalid count parameter<br>
	 *         -996 Message too big for PINET protocol<br>
	 *         -998 Memory allocation error
	 *
	 */
	int piar_timedvalues(int pt, IntByReference count, int[] times, float[] rvals, int[] istats, int prev);

	/**
	 * This function returns a single value and status for a specified time
	 * stamp. mode determines whether the returned value is interpolated or an
	 * actual Archive event. The possible modes are: 1 Value before given time
	 * and date (default) 2 Value after given time and date 3 Interpolated value
	 * at exact time and date 4 Interpolated value for resolution codes 1, 2,
	 * and 3 and value before given time for resolution code 4
	 *
	 *
	 * @param pt
	 *            Point number
	 * @param timedate
	 *            Time stamp
	 * @param mode
	 *            Retrieval mode
	 * @param rval
	 *            Value in engineering units
	 * @param istat
	 *            Integer value or status
	 * @return >0 System error<br>
	 *         0 Success<br>
	 *         -1 Bad point number<br>
	 *         -101 Date not on-line<br>
	 *         -103 No data for this point during the specified time range<br>
	 *         -105 Bad time stamp<br>
	 *
	 */
	int piar_value(int pt, IntByReference timedate, int mode, FloatByReference rval, IntByReference istat);

	/** ------------------- **/
	/** TODO Point Database Functions **/
	/**
	 *
	 * Retrieve a point's compression specifications in scaled units
	 * (pipt_compspecseng is the preferred routine.).
	 *
	 * @param pt
	 *            Point number
	 * @param compDev
	 *            Compression deviation in scaled units
	 * @param compMin
	 *            Compression minimum time in seconds
	 * @param compMax
	 *            Compression maximum time in seconds
	 * @return >0 System error<br>
	 *         0 Success<br>
	 *         -1 Point does not exist
	 *
	 */
	int pipt_compspecs(int pt, IntByReference compDev, IntByReference compMin, IntByReference compMax);

	/**
	 *
	 * Retrieve a point's compression specifications in engineering units.
	 *
	 * @param pt
	 *            Point number
	 * @param compDev
	 *            Compression deviation in scaled units
	 * @param compMin
	 *            Compression minimum time in seconds
	 * @param compMax
	 *            Compression maximum time in seconds
	 * @return >0 System error<br>
	 *         0 Success<br>
	 *         -1 Point does not exist
	 *
	 */
	int pipt_compspecseng(int pt, FloatByReference compDev, IntByReference compMin, IntByReference compMax);

	/**
	 * This function returns the point number for the given tagname. The tagname
	 * may be a long tagname or tagname. If found, the matching tagname in Point
	 * Database format is returned: tagnames are returned with delimiters and
	 * always 12 characters long; long tagnames are returned in upper case.
	 *
	 *
	 * @param tagname
	 *            Tagname (null terminated and returned in uppercase)
	 * @param pt
	 *            Point number
	 * @return >0 System error<br>
	 *         0 Success<br>
	 *         -2 Passed tag is all spaces<br>
	 *         -5 Tag not found, or not yet connected to a server
	 *
	 */
	int pipt_findpoint(String tagname, IntByReference pt);
	//int pipt_findpointnumber(String tagname, IntByReference pt);

	/**
	 * This function retrieves the point identifier for the point. The point
	 * identifier is unique, i.e., point identifiers are not reused when a point
	 * is deleted.
	 *
	 * @param pt
	 *            Point number
	 * @param ipt
	 *            Point identifier
	 * @return >0 System error<br>
	 *         0 Success -1 Point does not exist
	 *
	 */
	int pipt_pointid(int pt, IntByReference ipt);

	/** ------------------- **/
	/** TODO Snapshot Functions **/

	/** ------------------- **/
	/** Time Functions **/

	/**
	 *
	 * Wait a specified number of milliseconds.
	 *
	 * @param mseconds
	 *            Number of milliseconds to wait
	 * @return 0 always
	 */
	int pitm_delay(int mseconds);

	/**
	 *
	 * Returns server node time as PI time using a stored offset which is
	 * periodically refreshed.
	 *
	 * @return Current server time in PI format. Returns a time value of 0 if
	 *         there is a problem.
	 */
	int pitm_fastservertime();

	/**
	 *
	 * Generates a pi time string from a pi time.This function converts time in
	 * the pi time format to the pi string format:
	 *
	 * DD-MMM-YY HH:MM:SS
	 *
	 * The passed character buffer, timestring,should be at least 19 characters.
	 * If not the returned string will be truncated.
	 *
	 *
	 * @param timeDate
	 *            pi time stamp
	 * @param timeStr
	 *            pi time string buffer
	 * @param len
	 *            Length of the time string character buffer
	 */
	void pitm_formtime(int timeDate, byte[] timeStr, int len);

	/**
	 *
	 * Converts an integer time array to a pi time.This function converts an
	 * integer array of the following format: 将整数时间数组转换为pi时间 timeArray [0] month
	 * (1-12) <br>
	 *
	 * timeArray [1] day (1-31) <br>
	 *
	 * timeArray [2] year (four digit)<br>
	 *
	 * timeArray [3] hour (0-23)<br>
	 *
	 * timeArray [4] minute (0-59)<br>
	 *
	 * timeArray [5] second (0-59)<br>
	 *
	 * to PI time
	 *
	 *
	 * @param timeDate
	 *            PI time stamp
	 * @param timeArray
	 *            Array of time elements as specified above
	 */
	void pitm_intsec(IntByReference timeDate, int[] timeArray);

	/**
	 *
	 * Converts a pi time string into a pi time. This function parses the passed
	 * time string and returns the pi local time. If the passed string is a
	 * relative time, reltime is used as the starting point for calculating the
	 * absolute time. The function returns 0 if the time string is valid and -1
	 * if it is invalid. Valid time strings are an absolute format containing
	 * some fields of dd-mmm-yy hh:mm:ss, a relative time in +|- n d|h|m|s, an
	 * absolute time specified with a word (today, yesterday, sunday,
	 * monday,...), an asterisk for the current time, or a combination time
	 * using one of the word absolute times and a relative time. See the Data
	 * Archive Manual for more information on the time string format.
	 *
	 * @param str
	 *            Time string (null terminated)
	 * @param reltime
	 *            pi time stamp used as the basis for relative times. If str is
	 *            a relative time, the return time is relative to reltime.
	 * @param timeDate
	 *            Resulting time stamp
	 * @return 0 Success <br>
	 *         -1 Invalid time string
	 *
	 *
	 */
	int pitm_parsetime(String str, int reltime, IntByReference timeDate);

	/**
	 *
	 * This function converts the passed pi time stamp to an integer array of
	 * the format:<br>
	 * 将pi时间转换为整数时间数组 timearray[0] month (1-12)<br>
	 * timearray[1] day (1-31)<br>
	 * timearray[2] year (four digit) <br>
	 * timearray[3] hour (0-23) <br>
	 * timearray[4] min (0-59) <br>
	 * timearray[5] sec (0-59)<br>
	 *
	 * @param timeDate
	 *            pi time stamp
	 * @param timeArray
	 *            Array of time elements as specified above
	 */
	void pitm_secint(int timeDate, int[] timeArray);

	/**
	 *
	 * This function gets the time of the server node.
	 *
	 * @param serverTime
	 *            PI Server node time in PI time format
	 * @return >1 System Error<br>
	 *         1 Success -
	 *
	 *
	 */
	int pitm_servertime(IntByReference serverTime);

	/**
	 *
	 * This function gets the UTC time of the server node. The sub-second
	 * portion is included.
	 *
	 * @param serverTime
	 *            The server UTC time.
	 * @return >1 System Error<br>
	 *         0 Success -<br>
	 *         -10008 Function not supported
	 *
	 *
	 */
	int pitm_servertimeutc(FloatByReference serverTime);

	/**
	 *
	 * Synchronizes the local system time to the current server's system time.
	 *
	 * @return 0 If able to retrieve servertime. Attempts to change local system
	 *         time to agree with server. Success is subject to platform and
	 *         user privileges. (e.g., no action on VAX, must be super user on
	 *         some UNIX platforms) <br>
	 *
	 *         2 If there is a problem retrieving server time.
	 *
	 *
	 */
	int pitm_syncwithservertime();

	/**
	 *
	 * Returns the local system time as a pi time.
	 *
	 * @return PI time format
	 */
	int pitm_systime();

	/** ------------------- **/
	/** Utility Functions **/

	/**
	 *
	 * Establish a remote connection with the pi Server.
	 *
	 * @param procName
	 *            Character string identifying the client application to the
	 *            server
	 * @return >0 System Error<br>
	 *         0 Success<br>
	 *         -1 Attempt to reconnect within 60 seconds or socket_open has
	 *         failed<br>
	 *         -994 Incompatible PINET protocol version<br>
	 *         -1001 Default host not found<br>
	 *
	 */
	int piut_connect(String procName);

	/**
	 *
	 * Close all remote connections with PI Servers.
	 *
	 * @return >0 System Error<br>
	 *         0 Success
	 *
	 *
	 */
	int piut_disconnect();

	/**
	 *
	 * Close remote connection with passed node.
	 *
	 * @param nodeName
	 *            Name of node with which to close the connection
	 *
	 * @return >0 System Error<br>
	 *         0 Success<br>
	 *         -1 Passed name not same as PI home node (for VAX only)
	 *
	 *
	 */
	int piut_disconnectnode(String nodeName);

	/**
	 *
	 * Get the current API version number. 获取当前的API版本号
	 *
	 * @return 0 Success<br>
	 *         -1 The buffer is not large enough.
	 *
	 */
	int piut_getapiversion(byte[] version, int len);

	/**
	 *
	 * Get a value for an entry and section in an initialization file.
	 *
	 * @param section
	 *            Null-terminated string that specifies the section of the file
	 *            containing the entry. This section name should not include the
	 *            square brackets ("[]").
	 * @param entry
	 *            Null-terminated string containing the entry whose associated
	 *            string is to be retrieved
	 * @param defaultEntry
	 *            Null-terminated string that specifies the value for the given
	 *            entry if the entry cannot be found
	 * @param buf
	 *            Buffer that receives the character string
	 * @param bufSize
	 *            Size in bytes of buf
	 * @param file
	 *            Null-terminated string that identifies the initialization file
	 * @return The number of bytes copied into buf, not including the null
	 *         terminating character.
	 */
	int piut_getprofile(String section, String entry, String defaultEntry, byte[] buf, int bufSize, String file);

	/**
	 *
	 * Get the PI Server protocol version. 获取PI Server协议版本
	 *
	 * @param vers
	 *            Protocol version. This buffer should be at least 9 characters
	 *            long. It is returned null terminated.
	 * @param len
	 *            Size of character buffer vers.
	 */
	void piut_getprotocolvers(byte[] vers, int len);

	/**
	 *
	 * Increment an event counter.
	 *
	 * @param counter
	 *            Counter number (1-200)
	 * @param count
	 *            Number of new events to post
	 */
	void piut_inceventcounter(int counter, int count);

	/**
	 *
	 * whether the client application is connected to the current server.
	 * 客户端应用程序是否连接到当前服务器
	 *
	 * @return
	 *
	 *
	 */
	boolean piut_isconnected();

	/**
	 *
	 * Determines if this is a pi home node.
	 *
	 * @return <>0 This is a home node<br>
	 *         0 This is a client node
	 *
	 */
	int piut_ishome();

	/**
	 *
	 * Gain access to protected PI data.
	 *
	 * @param username
	 * @param password
	 * @param valid
	 * @return >0 System error<br>
	 *         0 Success<br>
	 *         -999 Login error
	 *
	 */
	int piut_login(String username, String password, IntByReference valid);

	/**
	 *
	 * Get local network information. 获取本地网络信息
	 *
	 * @param name
	 *            Local machine network name
	 * @param nameLen
	 *            Size, in bytes, of the buffer name
	 * @param address
	 *            Local machine network address
	 * @param addressLen
	 *            Size, in bytes, of the buffer address
	 * @param type
	 *            Network type being used by the PI-API
	 * @param typeLen
	 *            Size, in bytes, of the buffer type
	 * @return >0 System Error<br>
	 *         0 Success
	 *
	 */
	int piut_netinfo(byte[] name, int nameLen, byte[] address, int addressLen, byte[] type, int typeLen);

	/**
	 *
	 * Get network information for the passed node name.
	 *
	 * @param name
	 *            Default server machine network name
	 * @param nameLen
	 *            Size, in bytes, of the buffer name
	 * @param address
	 *            Default server machine network address
	 * @param addressLen
	 *            Size, in bytes, of the buffer address
	 * @param connected
	 *            TRUE if there is a valid connection to the current server or
	 *            FALSE if there is no connection
	 * @return >0 System Error<br>
	 *         0 Success
	 */
	int piut_netnodeinfo(byte[] name, int nameLen, byte[] address, int addressLen, IntByReference connected);

	/**
	 *
	 * Get network information for the default server. 获取默认服务器的网络信息
	 *
	 * @param name
	 *            Default server machine network name
	 * @param nameLen
	 *            Size, in bytes, of the buffer name
	 * @param address
	 *            Default server machine network address
	 * @param addressLen
	 *            Size, in bytes, of the buffer address
	 * @param connected
	 *            TRUE if there is a valid connection to the current server or
	 *            FALSE if there is no connection
	 * @return >0 System Error<br>
	 *         0 Success
	 */
	int piut_netserverinfo(byte[] name, int nameLen, byte[] address, int addressLen, IntByReference connected);

	/**
	 *
	 * Set the current server to the default server.
	 *
	 * @param server
	 *            Physical or logical node name where the default PI System
	 *            Server resides
	 * @return >0 System Error<br>
	 *         0 Success<br>
	 *         -1 Passed server name is not the same as the PI Home node (DEC
	 *         VAX only)
	 *
	 */
	int piut_setdefaultservernode(String server);

	/**
	 *
	 * Set the process name to specified name. 将进程名称设置为指定的名称
	 *
	 * @param procName
	 *            Name of the process
	 *
	 */
	void piut_setprocname(String procName);

	/**
	 *
	 * Set the current server to a specified node.
	 *
	 * @param serverName
	 * @return >0 System Error (typically network related)<br>
	 *         0 Success<br>
	 *         -1 Server name is not the same as the PI Home node (DEC VAX only)
	 *
	 */
	int piut_setservernode(String serverName);

	/**
	 *
	 * Write a value for an entry and section into an initialization file.
	 *
	 * @param section
	 *            Section of the file containing the entry. This section name
	 *            should not include the square brackets ("[]").
	 * @param entry
	 *            Entry to add or modify
	 * @param buf
	 *            Character string to insert. If this value is NULL, then the
	 *            entry is deleted.
	 * @param file
	 *            Initialization file name
	 * @return >0 System Error<br>
	 *         0 Success
	 *
	 */
	int piut_writeprofile(String section, String entry, String buf, String file);

	/**
	 *
	 * Zero an event counter.
	 *
	 * @param counter
	 *            Counter number (1-200)
	 */
	void piut_zeroeventcounter(int counter);

	/**
	 * 注册事件方式读取PI数据测点清单
	 *
	 * @param count
	 * @param pts
	 * @return
	 */
	int pisn_evmestablish(IntByReference count, int[] pts);

	/**
	 *
	 * 读取PI事件数据(一组数据,时间精度到秒)
	 *
	 * @param count
	 *
	 * @param pt
	 * @param rval
	 * @param istat
	 * @param timedate
	 * @return
	 */
	int pisn_evmexceptions(IntByReference count, Memory pt, Memory rval, Memory istat, Memory timedate);

	// int pisn_evmexceptions(IntByReference count, IntByReference pt, Float[]
	// rval, int[] istat, int[] timedate);

	int pisn_evmexceptionsx(IntByReference count, IntByReference ptnum, PI_EVENT.ByReference values, int funccode);

	/**
	 * 读取PI原始历史数据(单个数据) 待测
	 * @param mode
	 * @param drval
	 * @param ival
	 * @param bval
	 * @param bsize
	 * @param istat
	 * @param flags
	 * @param time
	 * @return
	 */

	int pisn_evmexceptions(int ptnum, int mode, FloatByReference drval, IntByReference ival, Pointer bval, IntByReference bsize, IntByReference istat, IntByReference flags, String[] time);
	/**
	 * 这个函数检索一个PI点的PointType。点类型是R(实数，浮点数)，I(整数，0-32767)或D(digital)。
	 * @param pt
	 * @param type
	 * @return
	 */
	int pipt_pointtype(int pt, byte[] type);

	int pipt_pointtypex(int ptnum, IntByReference typex);

	/**
	 * This function returns a single value from the PI Data Archive. Full status information and sub-second timestamp are included.
	 * The mode argument determines how the data value is found:
	 * 1 Value before given date and time
	 * 2 Value after given date and time
	 * 3 Interpolated value at exact date and time
	 * 4 Either interpolated or previous value
	 */
	int piar_getarcvaluex(int ptnum, int mode, DoubleByReference rval, IntByReference ival, byte[] bval, IntByReference bsize, IntByReference istat, ShortByReference flags, PITIMESTAMP timestamp);

	/**
	 * 此函数检索PI点的Descriptor。
	 * @param pt
	 * @param descriptor
	 * @param len
	 * @return
	 */
	int pipt_descriptorx(int pt, byte[] descriptor, IntByReference len);

	/**
	 *
	 * @param tagmask 标记掩码
	 * @param direction
	 * @param found
	 * @param tagname
	 * @param len
	 * @param pt
	 * @param numfound
	 * @return
	 */
	int pipt_wildcardsearch(String tagmask, int direction, IntByReference found, byte[] tagname, int len, IntByReference pt, IntByReference numfound);

	int pipt_signupforupdates();

	int pipt_updates(IntByReference pt, byte[] tagname, int len, IntByReference mode);

	int piar_getarcvaluesx(int ptnum, int arcmode, IntByReference count, DoubleByReference rval, IntByReference ival, byte[] bval, IntByReference bsize, IntByReference istat, ShortByReference flags, PITIMESTAMP timestamp0, PITIMESTAMP timestamp, int funccode);

	/**
	 * 删除注册的事件方式读取的PI数据测点清单
	 *
	 * @param count
	 * @param pts
	 * @return
	 */
	int pisn_evmdisestablish(IntByReference count, int[] pts);

	/**
	 * 读取PI实时数据，多个测点的数据，带毫秒标签。
	 * @param ptnum
	 * @param count_ptnum
	 * @param rval
	 * @param ival
	 * @param bval
	 * @param bsize
	 * @param istat
	 * @param flags
	 * @param timestamp
	 * @param error
	 * @param funccode
	 * @return
	 */
	int pisn_getsnapshotsx(int[] ptnum, IntByReference count_ptnum, DoubleByReference rval, IntByReference ival, byte[] bval, IntByReference bsize, IntByReference istat, ShortByReference flags, PITIMESTAMP timestamp, IntByReference error, int funccode);


	int piut_errormsg(int stat, byte[] msg, IntByReference msglen);


	int piut_strerror(int stat, byte[] msg, IntByReference msglen, String filter);


	int pipt_digpointers(int pt, IntByReference digcode, IntByReference dignumb);

	int pipt_digcode(IntByReference digcode, String digstring);

	int pipt_digstate(int digcode, byte[] digstate, int len);


}




