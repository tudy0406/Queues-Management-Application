Queues Management Application
📋 Project Description

A JavaFX application that simulates a queue-based system using threads and synchronization mechanisms.
Developed for the Fundamental Programming Techniques course.

The system minimizes the total client waiting time by dynamically assigning clients to queues.
The simulation tracks client wait times, queue evolution, and computes useful statistics.
🚀 Features

    Generate clients with random arrival and service times

    Assign clients to the queue with the minimum waiting time

    Multi-threaded processing (one thread per queue)

    Synchronized operations for thread safety

    Real-time simulation displayed via JavaFX GUI

    Logs queue states and client transitions to a .txt file

    Displays:

        Average waiting time

        Average service time

        Peak simulation hour

🛠️ Technologies Used

    Java 17+

    JavaFX (GUI)

    Java Concurrency (Threads, Synchronization)

    Object-Oriented Programming

    Layered Architecture

📂 Project Structure

    Client — represents a client with arrival and service times.

    Queue — represents a service queue handled by its own thread.

    SimulationManager — coordinates client dispatching and simulation time.

    GUIController — JavaFX controller for user interaction and simulation visualization.

    Logger — handles event logging to a .txt file.

📈 UML Diagrams

Included diagrams in .drawio format:

    Use Case Diagram

    Package Diagram

    Class Diagram

🧪 How to Run

    Clone the repository:

    git clone https://github.com/tudy0406/Queues-Management-Application.git

    Open the project in IntelliJ IDEA or any Java IDE.

    Make sure JavaFX is properly set up in your IDE.

    Run the Main class to start the application.

⚙️ Input Parameters

When starting a simulation, the user must input:

    Number of clients (N)

    Number of queues (Q)

    Simulation time limit

    Minimum and maximum client arrival times

    Minimum and maximum client service times

📜 Example Log Output

The application generates a log showing the real-time status of:

    Waiting clients

    Active queues

    Client assignments

    Final statistics after simulation ends
