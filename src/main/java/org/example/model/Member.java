package org.example.model;

import org.example.InputScanner;
import org.example.PostgresConnection;
import org.example.View;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Scanner;

public class Member extends User {

    public static Connection connection = null;

    public Member(User user) {
        super(user);
    }

    public void profileManagement() {
        Scanner scanner = InputScanner.getInstance();
        System.out.println("Welcome to profile management, what information would you like to modify?");
        System.out.println("1. Profile information");
        System.out.println("2. Health metrics");
        System.out.println("3. Fitness goals");
        System.out.println("4. Exit");
        System.out.print("Enter your choice as a number: ");
        int response = scanner.nextInt();

        if (response == 1) {
            updatePersonalInfo();
        } else if (response == 2) {
            selectHealthOption();
        } else if (response == 3) {
            selectFitnessFunction();
        } else {
            System.out.println("Invalid selection");
        }
    }

    //helper function for profileManagement()
    public void updatePersonalInfo() {
        Scanner scanner = InputScanner.getInstance();
        System.out.println("You can change the following details:");
        System.out.println("1. Username");
        System.out.println("2. Password");
        System.out.println("3. Name");
        System.out.println("4. Exit");
        System.out.print("Enter your choice as a number: ");
        int response = scanner.nextInt();

        if (response == 1) {
            modifyUsername();
        } else if (response == 2) {
            modifyPassword();
        } else if (response == 3) {
            modifyName();
        } else if (response == 4) {
        } else {
            System.out.println("Invalid selection");
        }

    }

    public boolean modifyUsername() {

        Scanner scanner = InputScanner.getInstance();
        System.out.print("Input new username: ");
        scanner.nextLine();
        String newUser = scanner.nextLine();
        connection = PostgresConnection.connect();

        try {
            String existQuery = "SELECT username from users WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(existQuery);
            statement.setString(1, newUser);
            ResultSet res = statement.executeQuery();

            if (res.next() && res.getString("username").equals(newUser)) {
                System.out.println("This username is taken, please choose a different one.");
                return false;
            } else {

                String query = "UPDATE users SET username = ? WHERE id = ?";
                statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, newUser);
                statement.setInt(2, getUserID());

                int result = statement.executeUpdate();
                if (result > 0) {
                    try (ResultSet gKeys = statement.getGeneratedKeys()) {
                        if (gKeys.next()) {
                            String username = gKeys.getString(2);
                            System.out.println("Your new username is: " + username + "\n");
                            setUserName(username);
                        }

                    } catch (Exception e) {
                        System.out.println(e);
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }

        return true;

    }

    public boolean modifyPassword() {
        Scanner scanner = InputScanner.getInstance();
        System.out.print("Input new password: ");
        scanner.nextLine();
        String newPass = scanner.nextLine();
        connection = PostgresConnection.connect();

        try {
            String query = "UPDATE users SET password = ? WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, newPass);
            statement.setInt(2, getUserID());

            int result = statement.executeUpdate();
            if (result > 0) {
                try (ResultSet gKeys = statement.getGeneratedKeys()) {
                    if (gKeys.next() && gKeys.getString(3).equals(newPass)) {
                        System.out.println("Your password has been updated\n");
                    }
                } catch (Exception e) {
                    System.out.println(e);
                    return false;
                }
            }

        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return true;
    }

    public boolean modifyName() {
        Scanner scanner = InputScanner.getInstance();
        scanner.nextLine();
        System.out.print("Enter first name: ");
        String first_name = scanner.nextLine().trim();
        System.out.print("Enter last name: ");
        String last_name = scanner.nextLine().trim();

        String new_name = first_name + " " + last_name;
        connection = PostgresConnection.connect();

        try {
            String query = "UPDATE users SET name = ? WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, new_name);
            statement.setInt(2, getUserID());

            int result = statement.executeUpdate();
            if (result > 0) {
                try (ResultSet gKeys = statement.getGeneratedKeys()) {
                    if (gKeys.next() && gKeys.getString(4).equals(new_name)) {
                        System.out.println("Your name has been updated to: " + new_name + "\n");
                    }
                } catch (Exception e) {
                    System.out.println(e);
                    return false;
                }
            }
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }

        return true;

    }

    //helper function for profileManagement()
    public void selectHealthOption() {
        System.out.println("Health Metrics Menu");
        System.out.println("1. View Health Metrics");
        System.out.println("2. Edit Health Metrics");
        System.out.println("3. Add Health Metrics");
        System.out.println("4. Exit");
        System.out.print("Enter choice as integer: ");
        Scanner scanner = InputScanner.getInstance();
        int response = scanner.nextInt();
        if (response == 1) {
            viewHealthMetrics();
        } else if (response == 2) {
            editHealthMetrics();
        } else if (response == 3) {
            addHealthMetrics();
        } else if (response == 4) {
            profileManagement();
        } else {
        }


    }

    public void viewHealthMetrics() {
        System.out.println("Your Health Metrics");
        connection = PostgresConnection.connect();
        String query = "SELECT metric_id, metric_type, value, unit, notes, date_recorded FROM healthmetrics WHERE member_id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, getUserID());
            ResultSet res = statement.executeQuery();

            while (res.next()) {
                System.out.println("---------");
                System.out.println("ID: " + res.getInt("metric_id"));
                System.out.println("Metric Type: " + res.getString("metric_type"));
                System.out.println("Value: " + res.getDouble("value") + res.getString("unit"));
                System.out.println("Note: " + res.getString("notes"));
                System.out.println("Date recorded: " + res.getDate("date_recorded") + "\n");
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void editHealthMetrics() {
        viewHealthMetrics();
        Scanner scanner = InputScanner.getInstance();
        System.out.print("Provide the ID of the metric you would like to edit, otherwise input 0: ");
        int metric_id = scanner.nextInt();
        scanner.nextLine();

        if (metric_id == 0) {
            return;
        }

        System.out.println("Which field would you like to change?");
        System.out.println("1. Metric Type");
        System.out.println("2. Value");
        System.out.println("3. Unit");
        System.out.println("4. Note");
        System.out.println("5. Exit");
        System.out.print("Enter choice as integer: ");

        int response = scanner.nextInt();
        scanner.nextLine();
        if (response == 1) {
            System.out.print("Enter new metric type: ");
            String metric = scanner.nextLine();
            updateMetricType(metric_id, metric);
        } else if (response == 2) {
            System.out.print("Enter new metric value: ");
            Double val = scanner.nextDouble();
            updateMetricValue(metric_id, val);
            scanner.nextLine();

        } else if (response == 3) {
            System.out.print("Enter new metric unit: ");
            String unit = scanner.nextLine();
            updateMetricUnit(metric_id, unit);

        } else if (response == 4) {
            System.out.print("Enter new metric note: ");
            String note = scanner.nextLine();
            updateMetricNote(metric_id, note);
        } else if (response == 5) {
        } else {
        }

    }

    public void addHealthMetrics() {
        Scanner scanner = InputScanner.getInstance();
        scanner.nextLine();
        System.out.print("Enter Metric Type: ");
        String metric = scanner.nextLine();
        System.out.print("Enter value: ");
        Double value = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter unit of measurement: ");
        String unit = scanner.nextLine();
        System.out.print("Enter note: ");
        String note = scanner.nextLine();

        LocalDate date = java.time.LocalDate.now();

        connection = PostgresConnection.connect();

        try {
            String query = "INSERT INTO HealthMetrics(member_id, metric_type, value, date_recorded, unit, notes) VALUES (?, ? , ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, getUserID());
            statement.setString(2, metric);
            statement.setDouble(3, value);
            statement.setDate(4, java.sql.Date.valueOf(date));
            statement.setString(5, unit);
            statement.setString(6, note);
            statement.executeUpdate();
            System.out.println("New metric has been added!\n");
        } catch (Exception e) {
            System.out.println(e);
        }
    }


    public void updateMetricType(int id, String metric_type) {
        connection = PostgresConnection.connect();

        try {
            String query = "UPDATE healthmetrics SET metric_type = ? WHERE metric_id = ? AND member_id = ?";
            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, metric_type);
            statement.setInt(2, id);
            statement.setInt(3, getUserID());

            int result = statement.executeUpdate();

            if (result > 0) {
                try (ResultSet gKeys = statement.getGeneratedKeys()) {
                    if (gKeys.next()) {
                        System.out.println("This metric has been updated.");
                    }
                }
            } else {
                System.out.println("Metric was not updated.\n");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void updateMetricValue(int id, Double value) {

        connection = PostgresConnection.connect();

        try {
            String query = "UPDATE healthmetrics SET value = ? WHERE metric_id = ? AND member_id = ?";
            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setDouble(1, value);
            statement.setInt(2, id);
            statement.setInt(3, getUserID());

            int result = statement.executeUpdate();

            if (result > 0) {
                try (ResultSet gKeys = statement.getGeneratedKeys()) {
                    if (gKeys.next()) {
                        System.out.println("This metric has been updated.");
                    }
                }
            } else {
                System.out.println("Metric was not updated.\n");
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void updateMetricUnit(int id, String unit) {
        connection = PostgresConnection.connect();

        try {
            String query = "UPDATE healthmetrics SET unit = ? WHERE metric_id = ? AND member_id = ?";
            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, unit);
            statement.setInt(2, id);
            statement.setInt(3, getUserID());

            int result = statement.executeUpdate();

            if (result > 0) {
                try (ResultSet gKeys = statement.getGeneratedKeys()) {
                    if (gKeys.next()) {
                        System.out.println("This metric has been updated.");
                    }
                }
            } else {
                System.out.println("Metric was not updated.\n");
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void updateMetricNote(int id, String note) {
        connection = PostgresConnection.connect();

        try {
            String query = "UPDATE healthmetrics SET notes = ? WHERE metric_id = ? AND member_id = ?";
            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, note);
            statement.setInt(2, id);
            statement.setInt(3, getUserID());

            int result = statement.executeUpdate();

            if (result > 0) {
                try (ResultSet gKeys = statement.getGeneratedKeys()) {
                    if (gKeys.next()) {
                        System.out.println("This metric has been updated.");
                    }
                }
            } else {
                System.out.println("Metric was not updated.\n");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void selectFitnessFunction() {
        System.out.println("Fitness Goals Menu");
        System.out.println("1. View Fitness Goals");
        System.out.println("2. Edit Fitness Goal");
        System.out.println("3. Add Fitness Goal");
        System.out.println("4. Exit");
        System.out.print("Enter choice as integer: ");
        Scanner scanner = InputScanner.getInstance();
        int response = scanner.nextInt();

        if (response == 1) {
            viewFitnessGoals();
        } else if (response == 2) {
            updateFitnessGoals();
        } else if (response == 3) {
            addFitnessGoals();
        } else if (response == 4) {
            profileManagement();
        } else {
        }

    }


    public void addFitnessGoals() {
        Scanner scanner = InputScanner.getInstance();
        scanner.nextLine();
        System.out.print("Enter title: ");
        String title = scanner.nextLine();
        System.out.print("Enter description: ");
        String description = scanner.nextLine();
        System.out.print("Enter end date (YYYY-MM-DD): ");
        String date = scanner.nextLine();

        connection = PostgresConnection.connect();

        try {
            String query = "INSERT INTO FitnessGoal(userId, title, value, enddate, completed) VALUES (?, ? , ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, getUserID());
            statement.setString(2, title);
            statement.setString(3, description);
            statement.setDate(4, java.sql.Date.valueOf(date));
            statement.setBoolean(5, false);
            statement.executeUpdate();
            System.out.println("Your goal has been added!\n");
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    //helper function for profileManagement()
    public void updateFitnessGoals() {
        viewFitnessGoals();
        Scanner scanner = InputScanner.getInstance();
        System.out.print("Provide the ID of the goal you would like to edit, otherwise input 0: ");
        int goal_id = scanner.nextInt();
        scanner.nextLine();

        if (goal_id == 0) {
            return;
        }

        System.out.println("What attributes would you like to change?");
        System.out.println("1. Title");
        System.out.println("2. Description");
        System.out.println("3. End date");
        System.out.println("4. Completion");
        System.out.println("5. Exit menu");
        System.out.print("Enter choice as integer: ");
        int response = scanner.nextInt();
        scanner.nextLine();

        if (response == 1) {
            System.out.print("Enter new title: ");
            String title = scanner.nextLine();
            updateTitle(goal_id, title);
            updateFitnessGoals();
        } else if (response == 2) {
            System.out.print("Enter new description: ");
            String des = scanner.nextLine();
            updateDescription(goal_id, des);
            updateFitnessGoals();
        } else if (response == 3) {
            System.out.print("Enter new end date (YYYY-MM-DD):  ");
            String date = scanner.nextLine();
            modifyDate(goal_id, date);
            updateFitnessGoals();
        } else if (response == 4) {
            System.out.println("Enter new status (0 = Incomplete, 1 = Complete): ");
            int status = scanner.nextInt();
            boolean stat = false;

            if (status == 0) {
                stat = false;
            } else if (status == 1) {
                stat = true;
            } else {
                System.out.println("Invalid value for status:");
            }
            updateStatus(goal_id, stat);
            updateFitnessGoals();
        } else if (response == 5) {
            profileManagement();
        } else {
        }
    }

    public void updateTitle(int id, String title) {
        connection = PostgresConnection.connect();

        try {
            String query = "UPDATE fitnessgoal SET title = ? WHERE goalid = ? AND userid = ?";
            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, title);
            statement.setInt(2, id);
            statement.setInt(3, getUserID());

            int result = statement.executeUpdate();

            if (result > 0) {
                try (ResultSet gKeys = statement.getGeneratedKeys()) {
                    if (gKeys.next()) {
                        System.out.println("Your goal has been updated.");
                    }
                }
            } else {
                System.out.println("Goal was not updated.\n");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void updateDescription(int id, String description) {
        connection = PostgresConnection.connect();

        try {
            String query = "UPDATE fitnessgoal SET value = ? WHERE goalid = ? AND userid = ?";
            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, description);
            statement.setInt(2, id);
            statement.setInt(3, getUserID());

            int result = statement.executeUpdate();

            if (result > 0) {
                try (ResultSet gKeys = statement.getGeneratedKeys()) {
                    if (gKeys.next()) {
                        System.out.println("Your goal has been updated.");
                    }
                }
            } else {
                System.out.println("Goal was not updated.\n");
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void modifyDate(int id, String date) {
        connection = PostgresConnection.connect();

        try {
            String query = "UPDATE fitnessgoal SET enddate = ? WHERE goalid = ? AND userid = ?";
            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setDate(1, java.sql.Date.valueOf(date));
            statement.setInt(2, id);
            statement.setInt(3, getUserID());

            int result = statement.executeUpdate();

            if (result > 0) {
                try (ResultSet gKeys = statement.getGeneratedKeys()) {
                    if (gKeys.next()) {
                        System.out.println("Your goal has been updated.");
                    }
                }
            } else {
                System.out.println("Goal was not updated.\n");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void updateStatus(int id, boolean completed) {

        try {
            String query = "UPDATE fitnessgoal SET completed = ? WHERE goalid = ? AND userid = ?";
            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setBoolean(1, completed);
            statement.setInt(2, id);
            statement.setInt(3, getUserID());

            int result = statement.executeUpdate();

            if (result > 0) {
                try (ResultSet gKeys = statement.getGeneratedKeys()) {
                    if (gKeys.next()) {
                        System.out.println("Your goal has been updated.");
                    }
                }
            } else {
                System.out.println("Goal was not updated.\n");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }


    public void viewFitnessGoals() {
        connection = PostgresConnection.connect();
        System.out.println("Your fitness goals: ");
        try {
            String query = "SELECT goalid, title, value, enddate, completed FROM fitnessgoal WHERE userid = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, getUserID());
            ResultSet res = statement.executeQuery();
            while (res.next()) {
                System.out.println("---------");
                System.out.println("Goal id: " + res.getString("goalid"));
                System.out.println("Title: " + res.getString("title"));
                System.out.println("Description: " + res.getString("value"));
                System.out.println("End date: " + res.getDate("enddate"));
                System.out.println("Completed (T/F): " + res.getBoolean("completed") + "\n");
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void scheduleManagement() {

    }

    public void viewExerciseRoutines() {
        System.out.println("Exercise Routines: ");

        try {
            connection = PostgresConnection.connect();
            String query = "SELECT id, name FROM exerciseroutines";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet res = statement.executeQuery();

            while (res.next()) {
                System.out.println("Exercise " + res.getInt("id") + ": " + res.getString("name"));
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        System.out.print("Enter the ID of the exercise routine you would like to view or -1 to go back: ");
        Optional<Integer> equipmentId = View.getIntegerInput();
        while (equipmentId.isEmpty()) {
            System.out.println("Invalid input. Please enter a number.");
            equipmentId = View.getIntegerInput();
        }
        if (equipmentId.get() == -1) return;

        try {
            String query = "SELECT name, instruction FROM exerciseroutines WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, equipmentId.get());
            ResultSet res = statement.executeQuery();

            if (res.next()) {
                System.out.println("Name: " + res.getString("name"));

                // Parse the instructions into a list of strings
                String[] instructions = res.getString("instruction").split(";");
                for (String instruction : instructions) {
                    System.out.print("    ");
                    System.out.println(instruction);
                }
            } else {
                System.out.println("No exercise routine found with that ID.");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void viewFitnessAchievements() {
        // Show the fitness goals marked as completed
        System.out.println("Fitness Achievements: ");
        try {
            connection = PostgresConnection.connect();
            String query = "SELECT title, value, enddate FROM fitnessgoal WHERE userid = ? AND completed = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, getUserID());
            statement.setBoolean(2, true);
            ResultSet res = statement.executeQuery();

            while (res.next()) {
                System.out.println("Title: " + res.getString("title"));
                System.out.println("Description: " + res.getString("value"));
                System.out.println("End date: " + res.getDate("enddate") + "\n");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void viewHealthStatistics() {
        // Get the most recent metric for each routine and display it
        System.out.println("Health Statistics: ");
        System.out.println("---Showing the most recent health metrics---");
        try {
            connection = PostgresConnection.connect();
            String query = "SELECT h.metric_type, avg(h.value), h.unit, h.date_recorded FROM healthmetrics h " + "INNER JOIN " + "(SELECT metric_type, MAX(date_recorded) as date_recorded FROM healthmetrics " + "WHERE member_id = ? GROUP BY metric_type) m " + "ON h.metric_type = m.metric_type AND h.date_recorded = m.date_recorded " + "GROUP BY h.metric_type, h.unit, h.date_recorded";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, getUserID());
            ResultSet res = statement.executeQuery();

            while (res.next()) {
                System.out.println("Metric Type: " + res.getString("metric_type"));
                System.out.println("Value: " + res.getDouble("avg") + " " + res.getString("unit"));
                System.out.println("Last Date Recorded: " + res.getDate("date_recorded") + "\n");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
