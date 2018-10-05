package me.changchao.webflux.reactivecats.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CatDto {
	private String id;
	private String url;
}
