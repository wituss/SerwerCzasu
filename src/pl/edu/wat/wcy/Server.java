package pl.edu.wat.wcy;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;


/**
 * Created by wiciu on 30.05.15.
 */
public class Server implements ActionListener {
    private static final int PORT = 9001;
    private static final long OFFSET = 1000;    //OFFSET w milisekundach
    private static final int THREADS = 20;      //Ilość wątków do obsługi komunikacji

    //synchronizowana lista klientów
    private static Set<Client> clients = Collections.synchronizedSet(new HashSet<Client>());

    JFrame frame = new JFrame("Chatter");

    public static void main(String[] args) throws Exception {
        final Server server = new Server();
        Runtime.getRuntime().addShutdownHook(new shutdownHook());
        NewConnectionListener listener = new NewConnectionListener();
        listener.start();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               //Tutaj powinien startować GUI
            }
        });
        while (true) {
            if (clients.size() > 0) {
                //Metoda updateTime() musi być wywołana przez Button. Narazie zasymulowałem kliknięcie po podłączreniu pierwszego
                //klienta
                updateTime();
                break;
            }
        }
        System.exit(0);
    }

    private static void updateTime() {
        ExecutorService threadPool = Executors.newFixedThreadPool(THREADS);
        List<Callable<RespondObject>> taskList = new ArrayList<Callable<RespondObject>>();
        List<Callable<Boolean>> updateTaskList = new ArrayList<Callable<Boolean>>();
        List<Future<RespondObject>> responds = null;
        synchronized (clients) {
            for (Client client : clients) {
                GetTimeTask task = client.getGetTimeTask();
                if (task != null) {
                    taskList.add(client.getGetTimeTask());
                } else {
                    System.out.println("Client [" + client.getName() + "] connection is out of date, removing from client list");
                    clients.remove(client);
                }
            }
        }
        try {
            responds = threadPool.invokeAll(taskList, 5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (responds != null) {
            long deltaSum = 0;
            int count=0;
            for (Future<RespondObject> response : responds) {
                try {
                    while (!response.isDone()) {
                        if (response.isCancelled()) {
                            break;
                        }
                    }
                    System.out.println("RESPONSE!!! DELTA [" + response.get().getDelta() +
                            "]ms time [" + response.get().getTime() + "]");
                    long delta = response.get().getDelta();
                    if (delta > -OFFSET && delta < OFFSET) {
                        deltaSum += delta;
                        count++;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
            if(count>0) {
                deltaSum = deltaSum / count;
            }else{
                deltaSum = 0;
            }
            synchronized (clients) {
                for (Client client : clients) {
                    SetTimeTask task = client.getSetTimeTask(deltaSum);
                    if (task != null) {
                        updateTaskList.add(task);
                    } else {
                        System.out.println("Client [" + client.getName() + "] connection is out of date, removing from client list");
                        clients.remove(client);
                    }
                }
            }
            try {
                threadPool.invokeAll(updateTaskList, 1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println(e.toString());
    }

    private static class Client {
        private Socket socket;
        private String name;

        public Client(Socket socket) {
            this.socket = socket;
            this.name = socket.getInetAddress().getHostAddress();
        }

        GetTimeTask getGetTimeTask() {
            if (!socket.isConnected())
                return null;
            BufferedReader in = null;
            PrintWriter out = null;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new GetTimeTask(in, out, name);
        }

        SetTimeTask getSetTimeTask(long delta) {
            if (!socket.isConnected())
                return null;
            BufferedReader in = null;
            PrintWriter out = null;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new SetTimeTask(in, out, delta);
        }

        public String getName() {
            return name;
        }
    }

    /**
     * Klasa odpowiedzialna za nasłuchiwanie nowych klientów i dodawanie ich do listy. Puszczamy ją w nowym wątku,
     * żeby działała sobie w tle
     */
    private static class NewConnectionListener extends Thread {
        public void run() {
            System.out.println("Starting server...");
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(PORT);
                System.out.println("Server started. Listening on port: " + PORT);
            } catch (IOException e) {
                System.out.println("Server starting failed! Good bye");
                System.exit(1);
            }

            try {
                while (true) {
                    Client client = new Client(serverSocket.accept());
                    synchronized (clients) {
                        clients.add(client);
                    }
                    System.out.println("Client [" + client.getName() + "] connected.");
                }
            } catch (IOException e) {
                System.out.println("Something go wrong");
            } finally {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    System.out.println("Server stopped improperly");
                }
            }
        }

    }

    //Klasa która będzie mówiła o tym czy program zakończył się prawidłow (zrobię jeśli będę miał czas)
    private static class shutdownHook extends Thread {
        public void run() {
            System.out.println("Application exit");
            //Tutaj pozniej napisze sie obsluge wyjscia z aplikacji
        }
    }
}