package me.changchao.webflux.reactivecats.service;

import me.changchao.webflux.reactivecats.dto.CatDto;
import reactor.core.publisher.Flux;

public interface CatService {
	Flux<CatDto> search();
}
