import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskProduct implements Runnable {
    String name_folder;
    int P;
    String id;
    ExecutorService tpe;
    BufferedReader reader;
    BufferedWriter writerProd;
    Semaphore semaphore;
    int line_count;
    int nr_commands;
    
    public TaskProduct(String name_folder, int P, String id,  ExecutorService tpe,
                Semaphore semaphore, int line_count, int nr_commands) throws IOException {
        this.name_folder = name_folder;
        this.P = P;
        this.id = id;
        this.tpe = tpe;
        this.reader =
            new BufferedReader(new FileReader(this.name_folder + "/order_products.txt"));;
        this.writerProd = new BufferedWriter(new FileWriter("order_products_out.txt", true));
        this.semaphore = semaphore;
        this.line_count = line_count;
        this.nr_commands = nr_commands;
    }

    @Override
    public void run() {
        int line_count_copy = line_count;
        String line;
        try {
                line = reader.readLine();
                String[] words = line.split(",");
                String id = words[0];
                if(id.equals(this.id)){
                        line_count_copy--;
                }  

                while (line_count_copy != 0 && line != null) {
                    line = reader.readLine();
                    if(line != null) {   
                        words = line.split(",");
                        id = words[0];
                        if(id.equals(this.id)){
                            line_count_copy--;
                        }  
                    }
                }
               
                writerProd.write(line + ",shipped\n");
                writerProd.flush();
                semaphore.release();
                writerProd.close();
                reader.close();   

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}