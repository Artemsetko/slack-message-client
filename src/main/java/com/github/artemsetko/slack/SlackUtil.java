package com.github.artemsetko.slack;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.webhook.Payload;

import java.io.IOException;
import java.util.Scanner;

public class SlackUtil {

    public static final String WEBHOOK = "https://hooks.slack.com/services/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";

    public static void main(String[] args) throws IOException {
        new Thread(SlackUtil::sendMessage).start();
    }

    private static void sendMessage(){
        Slack slack = Slack.getInstance();
        Scanner scanner = new Scanner(System.in);

        while (!Thread.currentThread().isInterrupted()) {

            System.out.print("Enter message - ");
            String message = scanner.nextLine();

            Payload payload = Payload.builder()
                    .channel("#general")
                    .username("jSlack Bot")
                    .iconEmoji(":smile_cat:")
                    .text(message)
                    .build();

            try {
                slack.send(WEBHOOK, payload);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
