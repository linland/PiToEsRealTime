package com.link.entity;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

/**
 * <i>native declaration : piapix.h</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> , <a href="http://rococoa.dev.java.net/">Rococoa</a>, or <a href="http://jna.dev.java.net/">JNA</a>.
 */
public class PI_EVENT extends Structure {
	/**
	 * @see PIvaluetype<br>
	 * C type : PIvaluetype
	 */
	public int typex;
	//public Memory typex;
	/** C type : float64 */
	//public DoubleBuffer drval = DoubleBuffer.allocate(1024);
	//public double[] drval = new double[2];
	public double drval;
	//public IntBuffer ival = IntBuffer.allocate(1024);
	//public int[] ival = new int[2];
	public int ival;
	/**
	 * Set to NULL if not used.<br>
	 * C type : void*
	 */
	public Pointer bval = new Memory(512);
	public int bsize = 512;
	public int istat;
	//public Memory istat;
	public short flags;
	/** C type : PITIMESTAMP */
	public PITIMESTAMP.ByValue timestamp;
	public PI_EVENT() {
		super();
	}
	protected List<String> getFieldOrder() {
		return Arrays.asList("typex", "drval", "ival", "bval", "bsize", "istat", "flags", "timestamp");
	}
	/**
	 * @param typex @see PIvaluetype<br>
	 * C type : PIvaluetype<br>
	 * @param drval C type : float64<br>
	 * @param bval Set to NULL if not used.<br>
	 * C type : void*<br>
	 * @param timestamp C type : PITIMESTAMP
	 */
	public PI_EVENT(int typex, double drval, int ival, Pointer bval, int bsize, int istat, short flags, PITIMESTAMP.ByValue timestamp) {
		super();
		this.typex = typex;
		this.drval = drval;
		this.ival = ival;
		this.bval = bval;
		this.bsize = bsize;
		this.istat = istat;
		this.flags = flags;
		this.timestamp = timestamp;
	}
	public PI_EVENT(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PI_EVENT implements Structure.ByReference {
		
	};
	public static class ByValue extends PI_EVENT implements Structure.ByValue {
		
	};
}
