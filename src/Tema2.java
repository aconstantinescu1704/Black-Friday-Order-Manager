import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;


public class Tema2 {
    public static void main(String[] args) throws IOException {

        String name_folder = args[0];
        int P = Integer.parseInt(args[1]);
        try {
            AtomicInteger inQueue1 = new AtomicInteger(0);
            ExecutorService tpe1 = Executors.newFixedThreadPool(P);
            ExecutorService tpe2 = Executors.newFixedThreadPool(P);
            
            // delete previous found content in file
            PrintWriter printWriter1 = new PrintWriter("order_products_out.txt");
            PrintWriter printWriter2 = new PrintWriter("orders_out.txt");
            printWriter1.print("");
            printWriter1.close();
            printWriter2.print("");
            printWriter2.close();

            //submit P threads at the beggining to avoid sequential processing
            for(int i = 0; i < P; i++){
                inQueue1.incrementAndGet();
                tpe1.submit(new TaskOrder(name_folder, P, tpe1, inQueue1, tpe2, i));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
    }
}
