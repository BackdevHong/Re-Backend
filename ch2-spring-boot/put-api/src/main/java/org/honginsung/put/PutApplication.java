package org.honginsung.put;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PutApplication {
    public static void main(String[] args) {
        SpringApplication.run(PutApplication.class, args);
    }
}

/*
  {
      "name" : "honginsung",
      "age" : 22,
      "car_list" : [
           {
               "name" : "BMW",
               "car_number" : "11가 1234"
           },
           {
               "name" : "AUDI",
               "car_number" : "22가 0923"
           }
      ]
  }
 */