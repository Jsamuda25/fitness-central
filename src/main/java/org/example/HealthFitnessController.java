package org.example;

import org.example.model.Admin;
import org.example.model.Member;
import org.example.model.Trainer;
import org.example.model.User.UserType;

import java.util.List;

public class HealthFitnessController {
    private final HealthFitness healthFitness;
    private final View view;

    public HealthFitnessController() {
        healthFitness = new HealthFitness();
        view = new View();
    }

    /**
     * Handles the menu for the member user type
     */
    private void member() {
        Member member = new Member(healthFitness.getCurrentUser());
        while (true) {
            switch (view.memberMenu()) {
                case 1:
                    member.profileManagement();
                    break;
                case 2:
                    memberDashBoardDisplay(member);  // Multi option Dashboard Display Menu
                    break;
                case 3:
                    member.scheduleManagement();
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 4.");
                    break;
            }
        }
    }

    /**
     * Handles the menu for the trainer user type
     */
    private void trainer() {
        while (true) {
            switch (view.trainerMenu()) {
                case 1:
                    Trainer.scheduleManagement(healthFitness.getCurrentUser());
                    break;
                case 2:
                    Trainer.viewMemberProfile();
                    break;
                case 3:
                    Trainer.addExerciseRoutine();
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 3.");
                    break;
            }
        }
    }

    /**
     * Handles the menu for the admin user type
     */
    private void admin() {
        Admin admin = new Admin(healthFitness.getCurrentUser());

        while (true) {
            switch (view.adminMenu()) {
                case 1:
                    adminRoomBooking(admin);  // Multi option Room Booking Menu
                    break;
                case 2:
                    admin.equipmentMaintenanceMonitoring();
                    break;
                case 3:
                    admin.classScheduleUpdating();
                    break;
                case 4:
                    billingAndPaymentProcessing(admin);  // Multi option Billing and Payment Processing Menu
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 5.");
                    break;
            }
        }
    }

    /**
     * Displays the member dashboard
     *
     * @param member the member user
     */
    private void memberDashBoardDisplay(Member member) {
        while (true) {
            switch (view.memberDashboardMenu()) {
                case 1:
                    member.viewExerciseRoutines();
                    break;
                case 2:
                    member.viewFitnessAchievements();
                    break;
                case 3:
                    member.viewHealthStatistics();
                    break;
                case 4:
                    member.viewUpcomingEvents();
                    break;
                case 5:
                    member.viewFavouriteTrainers();
                    break;
                case 6:
                    member.viewTransactionHistory();
                    break;
                case 7:
                    return;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 4.");
                    break;
            }
        }
    }

    /**
     * Handles the menu for billing and payment processing for the admin
     *
     * @param admin the admin user
     */
    private void billingAndPaymentProcessing(Admin admin) {
        while (true) {
            switch (view.billingAndPaymentMenu()) {
                case 1:
                    admin.viewBillingAndPayment();
                    break;
                case 2:
                    admin.processRefunds();
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 3.");
                    break;
            }
        }
    }

    /**
     * Handles the room booking for the admin
     *
     * @param admin the admin user
     */
    private void adminRoomBooking(Admin admin) {
        while (true) {
            switch (view.roomBookingMenu()) {
                case 1:
                    admin.viewRoomBookings();
                    break;
                case 2:
                    admin.cancelRoomBooking();
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 3.");
                    break;
            }
        }
    }

    /**
     * Starts the HealthFitness system
     */
    public void start() {
        view.welcome();
        int choice = view.welcomeMenu();
        boolean success = false;

        while (choice != 3) {
            switch (choice) {
                case 1:
                    success = login();
                    break;
                case 2:
                    success = register();
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 3.");
                    break;
            }
            if (success) {
                userMenu();
            }
            choice = view.welcomeMenu();
        }
        view.close();
    }

    /**
     * Handles the menu for the user type
     */
    private void userMenu() {
        UserType userType = healthFitness.getUserType();
        switch (userType) {
            case ADMIN:
                System.out.println("Admin menu");
                admin();
                break;
            case MEMBER:
                System.out.println("Member menu");
                member();
                break;
            case TRAINER:
                System.out.println("Trainer menu");
                trainer();
                break;
            default:
                System.out.println("Invalid user type");
                break;
        }
        view.logoutMessage();
        healthFitness.logout();
    }

    /**
     * Handles the login process
     *
     * @return true if login is successful, false otherwise
     */
    private boolean login() {
        List<String> loginInfo = view.loginInput();
        boolean success = healthFitness.login(loginInfo);
        if (success) {
            System.out.println("Login successful!");
        } else {
            System.out.println("Login unsuccessful. Please try again.");
            return false;
        }
        return true;
    }

    /**
     * Handles the registration process
     *
     * @return true if registration is successful, false otherwise
     */
    private boolean register() {
        List<String> registerInfo = view.registerInput();
        boolean success = healthFitness.register(registerInfo);
        if (success) {
            System.out.println("Registration successful!");
        } else {
            System.out.println("Registration unsuccessful. Please try again.");
            return false;
        }
        return true;
    }
}
