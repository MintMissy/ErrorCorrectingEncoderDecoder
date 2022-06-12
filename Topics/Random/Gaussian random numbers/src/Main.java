import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int k = sc.nextInt();
        int n = sc.nextInt();
        double m = sc.nextDouble();

        // your initial seed starts with k
        Random random = new Random(k);

        // variable to check if all N iterations are valid (<=M)
        int check = 0;

        // you need an outer loop because like in #3,
        // if there is an invalid number you have to iterate N times again
        while (true) {

            // this is the N iterations you need
            for (int i = 0; i < n; i++) {

                // at each iteration, you generate a Gaussian number
                double randomNumber = random.nextGaussian();

                // again #3: if the generated number is greater than M, move on the the next seed
                if (randomNumber > m) {
                    random.setSeed(++k);    //moving on to the next seed
                    check = 0;  // don't forget to initialize the check variable too
                    break;
                }
                check++;
            }
            // if all N iterations have passed, that's your answer
            if (check == n) {
                break;
            }

        }
        System.out.println(k);
    }
}