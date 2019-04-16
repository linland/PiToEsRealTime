package com.link.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PIAttribute {
	private String originalName;
	private int piIdentification;
	private String digitalset;
	private String identifier;
	private int digcode;
}
