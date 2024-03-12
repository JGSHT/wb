package org.example.test;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

/**
 * mono语法测试
 */

@SpringBootTest
class MonoTests {

	/**
	 * mono.just() 返回一个mono<元素>
	 */
	@Test
	void testJust() {
		Mono.just(1).subscribe(System.out::println);
	}

	/**
	 * Mono.justOrEmpty(null) 如果值为null返回 Mono.empty()
	 */
	@Test
	void testJustOrEmpty(){
		Mono.justOrEmpty(null).subscribe(System.out::println);
	}
}