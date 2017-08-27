package com.kotelking.event;

public class LimitReachedException extends RuntimeException {

    public LimitReachedException(){
        super("선착순 신청이 마감되었습니다");
    }

    public LimitReachedException(int userCount){
        super("선착순 신청이 거의 마감되어 " + userCount + " 명으로 신청 불가합니다.");
    }
}
