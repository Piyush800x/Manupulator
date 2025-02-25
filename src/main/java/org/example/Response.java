package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Response {
    private static final HashMap<String, List<String>> response = new HashMap<>();
    private static final List<String> positive = new ArrayList<>();
    private static final List<String> negative = new ArrayList<>();

    private static final List<String> blockedSites = new ArrayList<>();

    static  {
        setPositive();
        setNegative();
        setBlockedSites();
        setResponse();
    }

    private static void setPositive() {
        positive.add("Just a friendly reminder to complete your task whenever you get a chance. Let me know if you need any help!");
        positive.add("When you have a moment, please make sure to finish your task. I'm here if you need any assistance!");
        positive.add("Don't forget to complete your task. Let me know if there's anything I can do to support you!");
        positive.add("It looks like your task is still pending. Would you be able to complete it soon?");
        positive.add("I know you're busy, but if you could complete your task when you have time, it would be greatly appreciated!");
        positive.add("Just checking in to see if you've had a chance to complete your task. Let me know if you need any help!");
        positive.add("A gentle reminder to complete your task. Please let me know if you have any questions!");
        positive.add("Would you be able to finish your task soon? Let me know if there's anything I can do to assist.");
        positive.add("Your task is still pending. Please try to complete it at your earliest convenience.");
        positive.add("When you get the chance, please remember to complete your task. I appreciate your effort!");
    }

    private static void setNegative() {
        negative.add("Your incomplete task is causing delays. Please make sure to finish it as soon as possible.");
        negative.add("This task has been pending for too long. It needs to be completed to avoid further issues.");
        negative.add("Your inaction is affecting progress. Please complete your task to keep things moving.");
        negative.add("Failure to complete this task is creating unnecessary bottlenecks. Please address it immediately.");
        negative.add("Your delay is impacting others. Please complete your task so the team can proceed smoothly.");
        negative.add("This task is overdue. Ignoring it may lead to complications down the line.");
        negative.add("Lack of completion is causing setbacks. Please prioritize this task before it becomes a bigger issue.");
        negative.add("If this task isn’t completed soon, it could lead to serious problems. Please act on it now.");
        negative.add("Ignoring this task is not an option. It needs to be completed to avoid further complications.");
        negative.add("Your lack of follow-through is noticeable. Please complete the task as expected.");
    }

    private static void setResponse() {
        response.put("positive", positive);
        response.put("negative", negative);
    }

    private static void setBlockedSites() {
        blockedSites.add("youtube");
        blockedSites.add("netflix");
        blockedSites.add("amazon prime");
        blockedSites.add("whatsapp");
        blockedSites.add("facebook");
        blockedSites.add("instagram");
        blockedSites.add("youtube music");
    }

    public static HashMap<String, List<String >> getResponse() {
        return response;
    }

    public static List<String> getBlockedSites() {
        return blockedSites;
    }
}
