package com.company;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the user name");
        String userName = scanner.nextLine();
        userName = trimString(userName);

        System.out.println("Enter the source branch name");
        String sourceBranchName = scanner.nextLine();
        sourceBranchName = trimString(sourceBranchName);

        System.out.println("Enter the target branch name");
        String targetBranchName = scanner.nextLine();
        targetBranchName = trimString(targetBranchName);

        File devOpsCheckFile = new File("C:\\Users\\" + userName + "\\Devops");
        File projectNamesFile = new File("C:\\Users\\"+ userName +"\\Devops\\ProjectNames.txt");
        File commandsFile = new File("C:\\Users\\"+ userName +"\\Devops\\Commands.txt");

        validateRequiredFiles(devOpsCheckFile, projectNamesFile, commandsFile, userName);

        List<String> commandsList = new ArrayList<>();

        getCommands(commandsList, commandsFile, userName, sourceBranchName, targetBranchName);

        createBranches(projectNamesFile, commandsList);

    }

    private static void validateRequiredFiles(File devOpsCheckFile, File projectNamesFile, File commandsFile, String userName) {
        if (!devOpsCheckFile.isDirectory()) {
            System.out.println("Devops folder missing in " + userName + " folder...");
            System.exit(0);
        }

        if(!projectNamesFile.exists()) {
            System.out.println("ProjectNames.txt file missing in Devops folder...");
            System.exit(0);
        }

        if(!commandsFile.exists()) {
            System.out.println("Commands.txt file missing in Devops folder...");
            System.exit(0);
        }
    }

    private static void getCommands(List<String> commandsList, File commandsFile, String userName, String sourceBranchName, String targetBranchName) {
        try {

            Scanner commandsFileSC = new Scanner(commandsFile);
            while (commandsFileSC.hasNextLine()) {

                String nextCommand = commandsFileSC.nextLine();
                if(nextCommand.contains("#")){
                    continue;
                }
                String inputCommand = trimString(nextCommand);
                String changedCommand = StringUtils.replace(inputCommand, "uuu", userName);
                changedCommand = StringUtils.replace(changedCommand, "tttbbb", targetBranchName);
                changedCommand = StringUtils.replace(changedCommand, "sssbbb", sourceBranchName);
                commandsList.add(changedCommand);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createBranches(File projectNamesFile, List<String> commandsList) {

        try {
            Scanner projectNameSC = new Scanner(projectNamesFile);
            while (projectNameSC.hasNextLine()) {

                String projectName = trimString(projectNameSC.nextLine());
                List<String> updatedCommandsList = commandsList.stream().map(cmd -> StringUtils.replace(cmd, "ppp", projectName)).collect(Collectors.toList());

                String combinedCommand = StringUtils.join(updatedCommandsList
                        , " && ");

                System.out.println("Working on project - " + projectName);
                ProcessBuilder processBuilder = new ProcessBuilder();
                processBuilder.command("cmd.exe", "/c", combinedCommand);
               // processBuilder.command("cmd.exe", "/c", "echo hello");

                Process process = processBuilder.start();

/*                StringBuilder output = new StringBuilder();

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line + "\n");
                }*/

                int exitVal = process.waitFor();
                if (exitVal == 0) {
                    //System.out.println(output);
                    System.out.println("Success!!");
                    //  System.exit(0);
                } else {
//                    System.out.println(output);
                    System.out.println("Unsuccessful!!!");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private static String trimString(String input) {
        return input.trim();
    }
}
