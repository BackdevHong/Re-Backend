package org.honginsung.objectmapper;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
    private String name = null;
    private Integer age = null;

    @JsonProperty("phone_number")
    public String phoneNumber = null;

    public User(String name, Integer age, String phoneNumber) {
        this.name = name;
        this.age = age;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public Integer getAge() {
        return age;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    // Object Mapper에서 에러 발생함
//    public User getDefaultUser() {
//        return new User("default", 0);
//    }

    // 앞에 get을 없애면 정상적으로 사용 가능
    public User defaultUser() {
        return new User("default", 0, "010-1234-1234");
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
