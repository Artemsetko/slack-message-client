package com.github.artemsetko.slack;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.model.Message;
import com.github.seratch.jslack.api.rtm.RTMClient;
import com.github.seratch.jslack.api.webhook.Payload;
import com.google.gson.Gson;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Scanner;

public class SlackUtil {

    private static final String WEBHOOK = "https://hooks.slack.com/services/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";
    private static final String TOKEN = "xoxb-894002955235-894173982499-pgiLN8TDR1wwEizDS0R5VKGV";
   // private static final String PATH_TO_POM_XML = "/Users/asiet/Artem/YOTTAA/SOURCES/aggregation/pom.xml";
    private static final String PATH_TO_POM_XML = "path/to/pom.xml";
    private static final String PATH_TO_MAVEN_BIN = "/usr/local/Cellar/maven/3.6.1/libexec/";


    public static void main(String[] args) {
       Gson gson = new Gson();
        new Thread(SlackUtil::sendMessage).start();

        try (RTMClient rtm = new Slack().rtm(TOKEN)){

            rtm.addMessageHandler(m -> {
                Optional<Message> message = Optional.ofNullable(gson.fromJson(m, Message.class));
                message.filter(e -> "message".equals(e.getType())).map(t -> t.getText()).ifPresent(System.out::println);
                message.filter(e -> "message".equals(e.getType())).map(t -> t.getText()).ifPresent(SlackUtil::runMavenBuild);
            });

            rtm.connect();

            while (true);
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    private static void sendMessage() {
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

    private static void runMavenBuild(String trigger) {
        if(trigger.equals("runBuild")) {
            InvocationRequest request = new DefaultInvocationRequest();
            request.setPomFile(new File(PATH_TO_POM_XML));
            request.setGoals(Arrays.asList("clean", "install"));

            Invoker invoker = new DefaultInvoker();
            invoker.setMavenHome(new File(PATH_TO_MAVEN_BIN));
            try {
                invoker.execute(request);
            } catch (MavenInvocationException e) {
                e.printStackTrace();
            }
        }
    }
}


