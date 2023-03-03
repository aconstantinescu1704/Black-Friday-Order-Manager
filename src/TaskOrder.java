import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskOrder implements Runnable {
    String name_folder;
    int P;
    ExecutorService tpeOrder;
    AtomicInteger inQueue;
    ExecutorService tpeProduct;
    BufferedReader reader;
    BufferedWriter writer;
    int line_count;
    
    public TaskOrder(String name_folder, int P, ExecutorService tpeOrder,
                 AtomicInteger inQueue, ExecutorService tpeProduct,
                 int line_count) throws IOException {
        this.name_folder = name_folder;
        this.P = P;
        this.tpeOrder = tpeOrder;
        this.inQueue = inQueue;
        this.tpeProduct = tpeProduct;
        this.reader =
            new BufferedReader(new FileReader(this.name_folder + "/orders.txt"));
        this.writer = new BufferedWriter(new FileWriter("orders_out.txt", true));
        this.line_count = line_count;
    }

    @Override
    public void run() {
        int line_count_copy = line_count;
        String line;
        try {
                line = reader.readLine();
                line_count_copy--;
                while (line_count_copy != -1 && line != null) {
                    line = reader.readLine();
                    line_count_copy--;
                }

                if(line != null){
                    String[] words = line.split(",");
                    String id = words[0];
                    int nr_commands = Integer.valueOf(words[1]);

                    // continue to read all orders
                    inQueue.incrementAndGet();
                    tpeOrder.submit(new TaskOrder(name_folder, P, tpeOrder, inQueue, tpeProduct, line_count + P));


                    if(nr_commands != 0){
                        Semaphore semaphore = new Semaphore(1 - nr_commands);
                        for(int i = 1; i <= nr_commands; i++){
                            tpeProduct.submit(new TaskProduct(name_folder, P, id, tpeProduct, semaphore, i, nr_commands));
                        }
                        
                        reader.close();
                        semaphore.acquire();
                        writer.write(id + "," + nr_commands + ",shipped\n");   
                        writer.flush();
                        writer.close();                
                    }

                }
                int left = inQueue.decrementAndGet();
                if (left == 0) {
                    tpeProduct.shutdown();
                    tpeOrder.shutdown();
                }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
