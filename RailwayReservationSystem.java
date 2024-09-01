import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Ticket {
    String name;
    int age;
    String gender;
    String berth;

    Ticket(String name, int age, String gender, String berth) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.berth = berth;
    }

    @Override
    public String toString() {
        return "Name: " + name + ", Age: " + age + ", Gender: " + gender + ", Berth: " + berth;
    }
}

class RailwayReservationSystem {
    private static final int TOTAL_BERTHS = 1;
    private static final int RAC_BERTHS = 1;
    private static final int WAITING_LIST_LIMIT = 1;

    private List<Ticket> confirmedTickets = new ArrayList<>();
    private List<Ticket> racTickets = new ArrayList<>();
    private List<Ticket> waitingList = new ArrayList<>();

    private int availableConfirmed = TOTAL_BERTHS;
    private int availableRac = RAC_BERTHS;
    private int waitingListCount = 0;

    public void bookTicket(String name, int age, String gender, String berthPreference) {
        if (age < 5) {
            System.out.println("Sorry, tickets cannot be allocated for children below age 5.");
            return;
        }

        if (availableConfirmed > 0) {
            String berth = (age > 60 || (gender.equals("Female") && berthPreference.equals("Lower"))) ? "Lower" : berthPreference;
            confirmedTickets.add(new Ticket(name, age, gender, berth));
            availableConfirmed--;
        } else if (availableRac > 0) {
            racTickets.add(new Ticket(name, age, gender, "Side-Lower"));
            availableRac--;
        } else if (waitingListCount < WAITING_LIST_LIMIT) {
            waitingList.add(new Ticket(name, age, gender, null));
            waitingListCount++;
        } else {
            System.out.println("No tickets available.");
        }
    }

    public void cancelTicket(String name) {
        boolean found = false;
        Ticket ticketToCancel = null;

        // Find and remove the ticket from confirmed tickets
        for (Ticket ticket : confirmedTickets) {
            if (ticket.name.equals(name)) {
                ticketToCancel = ticket;
                confirmedTickets.remove(ticket);
                availableConfirmed++;
                found = true;
                break;
            }
        }

        // Find and remove the ticket from RAC tickets
        if (!found) {
            for (Ticket ticket : racTickets) {
                if (ticket.name.equals(name)) {
                    ticketToCancel = ticket;
                    racTickets.remove(ticket);
                    availableRac++;
                    found = true;
                    break;
                }
            }
        }

        // Confirm a RAC ticket if available
        if (ticketToCancel != null) {
            if (waitingListCount > 0) {
                Ticket waitingTicket = waitingList.remove(0);
                waitingListCount--;
                waitingTicket.berth = "Side-Lower";
                racTickets.add(waitingTicket);
                availableRac--;
            }

            // Confirm a ticket from RAC to the available confirmed berths
            if (availableConfirmed > 0 && !racTickets.isEmpty()) {
                Ticket racTicket = racTickets.remove(0);
                racTicket.berth = "Lower";
                confirmedTickets.add(racTicket);
                availableConfirmed--;
            }
        }
    }

    public void printBookedTickets() {
        System.out.println("Booked Tickets:");
        for (Ticket ticket : confirmedTickets) {
            System.out.println(ticket);
        }
        for (Ticket ticket : racTickets) {
            System.out.println(ticket);
        }
        System.out.println("Total booked tickets: " + (confirmedTickets.size() + racTickets.size()));
    }

    public void printAvailableTickets() {
        System.out.println("Available Tickets:");
        System.out.println("Confirmed Berths Available: " + availableConfirmed);
        System.out.println("RAC Berths Available: " + availableRac);
        System.out.println("Waiting List Slots Available: " + (WAITING_LIST_LIMIT - waitingListCount));
        int totalAvailable = availableConfirmed + availableRac + (WAITING_LIST_LIMIT - waitingListCount);
        System.out.println("Total available tickets: " + totalAvailable);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        RailwayReservationSystem system = new RailwayReservationSystem();

        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1. Book Ticket");
            System.out.println("2. Cancel Ticket");
            System.out.println("3. Print Booked Tickets");
            System.out.println("4. Print Available Tickets");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");
            int option = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (option) {
                case 1:
                    System.out.print("Enter Name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter Age: ");
                    int age = scanner.nextInt();
                    scanner.nextLine();  // Consume newline
                    System.out.print("Enter Gender (Male/Female): ");
                    String gender = scanner.nextLine();
                    System.out.print("Enter Berth Preference (Lower/Upper): ");
                    String berthPreference = scanner.nextLine();
                    system.bookTicket(name, age, gender, berthPreference);
                    break;

                case 2:
                    System.out.print("Enter Name to Cancel: ");
                    name = scanner.nextLine();
                    system.cancelTicket(name);
                    break;

                case 3:
                    system.printBookedTickets();
                    break;

                case 4:
                    system.printAvailableTickets();
                    break;

                case 5:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
}
