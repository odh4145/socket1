package com.odh.socket1;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class socket1 {
    public static String uploadPath = "D:/odh/socket1/";

    public static void main(String[] args) {
        // 1초 간격 실행 위해 Timer 사용
        Timer random = new Timer();
        random.schedule(new TimerTask() {
            @Override
            public void run() {
                randomSave(); // 랜덤숫자 생성 후 저장
            }
        }, 0, 1000);

        Timer socket = new Timer();
        socket.schedule(new TimerTask() {
            @Override
            public void run() {
                send(); // 소켓으로 보내기
            }
        }, 0, 1000);
    }

    public static void randomSave(){
        Random random = new Random(); // 랜덤숫자 생성

        // 실시간 표출
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMddHHmmss");
        String formatedNow = now.format(formatter);

        // 파일 저장
        try {
            // 경로가 없는 경우 경로 만들기
            File folder = new File(uploadPath);
            if (!folder.exists()) folder.mkdirs();

            // 파일 제목
            String title = random.nextInt(1000) + "_" + formatedNow;

            // 파일 만들기
            OutputStream output = new FileOutputStream(uploadPath + title + ".txt");
            String str = title;
            byte[] by=str.getBytes();
            output.write(by);

        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public static void send() {
        // 경로 중 파일 제목 리스트 읽어오기
        File dir = new File(uploadPath);
        String[] filenames = dir.list();

        // 경로 안에 파일이 존재 한다면
        if(filenames != null){
            // 파일 중 랜덤으로 하나 뽑기
            int idx = new Random().nextInt(filenames.length);
            String title = filenames[idx];

            try {
                // 8082 port로 소켓연결
                Socket socket = new Socket("localhost", 8082);
                // 데이터를 보내기 위해 DataOutputStream 생성
                OutputStream outputStream = socket.getOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                
                dataOutputStream.writeUTF(title); // 데이터 쓰기
                dataOutputStream.flush(); // 데이터 보내기

                // 끝나면 닫기
                System.out.println("데이터 보내기 완료"); // 통신 확인을 위한 sout
                dataOutputStream.close();
                socket.close();
            }
            catch (IOException e){
                System.out.println("Error : " + e); // 에러난 경우 에러 띄우기
            }
        }
    }
}
