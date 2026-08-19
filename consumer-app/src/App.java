import com.iexceed.stringutils.StringReverser;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter your name: ");
        String name = sc.nextLine();

        String reversed = StringReverser.reverse(name);
        System.out.println("Reversed: " + reversed);
    }
}
