import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

class Customer {
    int id;

    public Customer(int id) {
        this.id = id;
    }
}

class Cashier {
    int id;
    int totalTimeProcessed;

    public Cashier(int id) {
        this.id = id;
        this.totalTimeProcessed = 0;
    }
}

class BankSimulator {
    public static void main(String[] args) {
        int lambda = 50; // Promedio de clientes por hora
        int simulationHours = 8;
        int numCashiers = 4;

        Queue<Customer> customerQueue = new LinkedList<>();
        Random random = new Random();

        Cashier[] cashiers = new Cashier[numCashiers];
        for (int i = 0; i < numCashiers; i++) {
            cashiers[i] = new Cashier(i + 1);
        }

        for (int hour = 1; hour <= simulationHours; hour++) {
            System.out.println("Hora " + hour + ":");
            int customersArrived = getPoisson(lambda);
            System.out.println("Clientes llegados: " + customersArrived);

            for (int i = 1; i <= customersArrived; i++) {
                Customer customer = new Customer(i);
                customerQueue.offer(customer);
            }

            int customersProcessed = 0;
            Queue<Customer> nextHourQueue = new LinkedList<>(); // Cola para el próximo ciclo
            while (!customerQueue.isEmpty()) {
                for (Cashier cashier : cashiers) {
                    if (!customerQueue.isEmpty() && cashier.totalTimeProcessed <= 60) {
                        Customer customer = customerQueue.poll();
                        int serviceTime = random.nextInt(10) + 1;
                        if (cashier.totalTimeProcessed + serviceTime <= 60) {
                            System.out.println("Caja " + cashier.id + ": Cliente " + customer.id + " atendido en " + serviceTime + " minutos");
                            cashier.totalTimeProcessed += serviceTime;
                            customersProcessed++;
                        } else {
                            System.out.println("Caja " + cashier.id + ": Cliente " + customer.id + " no atendido (excede el tiempo disponible)");
                            nextHourQueue.offer(customer); // Agregar a la cola del próximo ciclo
                        }
                    }
                }
            }

            // Agregar clientes no atendidos a la cola del próximo ciclo
            while (!customerQueue.isEmpty()) {
                nextHourQueue.offer(customerQueue.poll());
            }

            System.out.println("Clientes atendidos en esta hora: " + customersProcessed);
            System.out.println("Clientes en cola para la próxima hora: " + nextHourQueue.size());
            System.out.println();
            try {
                Thread.sleep(1000); // Espera de 1 segundo (representando 1 minuto)
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Reiniciar tiempos de procesamiento de cajas
            for (Cashier cashier : cashiers) {
                cashier.totalTimeProcessed = 0;
            }

            // Configurar la cola para el próximo ciclo
            customerQueue = nextHourQueue;
            nextHourQueue = new LinkedList<>();
        }
    }

    public static int getPoisson(double lambda) {
        Random random = new Random();
        double L = Math.exp(-lambda);
        int k = 0;
        double p = 1.0;

        do {
            k++;
            p *= random.nextDouble();
        } while (p > L);

        return k - 1;
    }
}
