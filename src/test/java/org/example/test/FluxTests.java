package org.example.test;

import org.example.entity.RoleMenu;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * flux语法测试
 */

@SpringBootTest
class FluxTests {

/**************************发布****************************************/


	@Test
	void testJust() {
		Flux.just("Hello", "World").subscribe(System.out::println);

		Flux.just(1,2,3).buffer().next().subscribe(r -> r.forEach(System.out::println));
	}

	@Test
	void testForm(){

		// fromArray()
		Flux.fromArray(new Integer[] {1, 2, 3}).subscribe(System.out::println);

		//fromIterable()
		Flux.fromIterable(Arrays.asList(1,2,3,55)).subscribe(System.out::println);

		//fromStream()
		Flux.fromStream(Stream.of(1,2,3)).subscribe(System.out::println);

	}

	@Test
	void testRange(){
		Flux.range(1, 10).subscribe(System.out::println);
	}

	@Test
	void testInterval(){
		Flux.interval(Duration.of(10, ChronoUnit.SECONDS)).subscribe(System.out::println);
//		Flux.intervalMillis(1000).subscribe(System.out::println);
	}

	@Test
	void testFirstEmittingWith(){
		//flux1.firstEmittingWith(flux2); // 谁先有数据就用谁，跟mono的or差不多
	}

/**************************转化****************************************/

	@Test
	void testMap(){
		Flux.just(1,2,3).map(o -> o + 10).subscribe(System.out::println);
		Flux.just(1,2,3).map(String::valueOf).subscribe(System.out::println);
	}

	@Test
	void testCollectMap(){
		RoleMenu roleMenu1 = new RoleMenu().setMenuId(1).setRoleId(1);
		RoleMenu roleMenu2 = new RoleMenu().setMenuId(2).setRoleId(1);
		RoleMenu roleMenu3 = new RoleMenu().setMenuId(3).setRoleId(1);
		Flux.just(roleMenu1, roleMenu2, roleMenu3).collectMap(RoleMenu::getMenuId).subscribe(System.out::println);
		Flux.just(roleMenu1, roleMenu2, roleMenu3).collectMap(RoleMenu::getMenuId, RoleMenu::getRoleId).subscribe(System.out::println);
	}

	@Test
	void testDistinct(){
		Flux.just(1,2,3,3,4).distinct().subscribe(System.out::println);
	}

	@Test
	void testCollectGroup(){
		RoleMenu roleMenu1 = new RoleMenu().setMenuId(1).setRoleId(1);
		RoleMenu roleMenu2 = new RoleMenu().setMenuId(2).setRoleId(1);
		RoleMenu roleMenu3 = new RoleMenu().setMenuId(3).setRoleId(1);
		RoleMenu roleMenu4 = new RoleMenu().setMenuId(1).setRoleId(2);
		Flux.just(roleMenu1, roleMenu2, roleMenu3, roleMenu4)
				.groupBy(RoleMenu::getMenuId)
				.map(o -> o)
				.subscribe(
						r -> {
							System.out.println("-" + r.toString());
							r.subscribe(System.out::println);
						});

	}



/**************************消费****************************************/

	@Test
	void testCount(){
		Flux.just(1,2,3,5).count().subscribe(System.out::println);
	}

	@Test
	void testDefaultIfEmpty(){
		Flux.empty().defaultIfEmpty(Flux.just("fk").subscribe(System.out::println)).subscribe(System.out::println);
	}

	@Test
	void testDelayElements() throws InterruptedException {
		Flux.just(1,2,3).delayElements(Duration.ofSeconds(1)).subscribe(System.out::println);
		Thread.sleep(1000L);
	}

	@Test
	void testDelaySubscription() throws InterruptedException {
		Flux.just(1,2,3).delaySubscription(Duration.ofSeconds(1)).subscribe(System.out::println);
		Thread.sleep(1000L);
	}

}