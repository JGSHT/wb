package org.example.util;

import reactor.core.publisher.Mono;

public class TT {

    public static void main(String[] args) {
        tm().subscribe(System.out::println);
//        List<Integer> a = Arrays.asList(1,2,3);
//        List<Integer> b = Arrays.asList(5,2,7);
//        Set<Integer> result = new HashSet<>();
//        result.addAll(a);
//        result.addAll(b);
//
//        System.out.println(a.size() + b.size() > result.size());
//
//        String name = Thread.currentThread().getName();
//        System.out.println(name);
//        Mono.just("tmd").delaySubscription(Duration.ofSeconds(1)).subscribe(r ->  {
//            String name1 = Thread.currentThread().getName();
//            System.out.println(name1);
//            throw new RuntimeException("fuck");
//        });
//
//        System.out.println(111);
//
//
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
    }

    private static Mono<String> tm(){
        return Mono.justOrEmpty("test").flatMap(o -> {
            System.out.println(o);
            return Mono.just("ct");
        });
    }
}
