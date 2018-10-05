package me.changchao.webflux.reactivecats.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import me.changchao.webflux.reactivecats.dto.CatDto;
import me.changchao.webflux.reactivecats.service.CatService;
import reactor.core.publisher.Flux;

@RestController
public class CatController {
	@Autowired
	private CatService catService;

	@GetMapping("/")
	public Flux<CatDto> list() {
		return catService.search();
	}
}
