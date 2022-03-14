package Reader;

import java.io.*;
import java.util.ArrayList;

public class Reader {

   public static final String delimiter = ";";
   public static ArrayList<String> finalArr = new ArrayList<>();
   public static Double x0;
   public static Double y0;
   public static Double xt;
   public static Double yt;
   public static Double r;
   public static Double muk;
   public static Double mus;
   public static String heightProfile; // ask Niko for his implementation and leave it as a String for now
   public static int[] sandPitX = new int[2];
   public static int[] sandPitY = new int[2];
   public static Double muks;
   public static Double muss;

   public static void read(String csvFile) {
      try {
         File file = new File(csvFile);
         FileReader fr = new FileReader(file);
         BufferedReader br = new BufferedReader(fr);
         String line = "";
         String temp = "";
         String[] tempArr;
         String temp2 = "";

         while ((line = br.readLine()) != null) {
            tempArr = line.split(delimiter);
            for (String tempStr : tempArr) {
               temp = (tempStr.substring(tempStr.lastIndexOf("=") + 2));
               finalArr.add(temp);

            }
         }
         br.close();
         for (int i = 0; i < finalArr.size(); i++) {
            temp2 = finalArr.get(i);
            System.out.println(temp2);
         }

      } catch (IOException ioe) {
         ioe.printStackTrace();
      }

      x0 = Double.parseDouble(finalArr.get(0));
      y0 = Double.parseDouble(finalArr.get(1));
      xt = Double.parseDouble(finalArr.get(2));
      yt = Double.parseDouble(finalArr.get(3));
      r = Double.parseDouble(finalArr.get(4));
      muk = Double.parseDouble(finalArr.get(5));
      mus = Double.parseDouble(finalArr.get(6));
      heightProfile = finalArr.get(7);
      sandPitX[0] = Integer.parseInt(finalArr.get(8).split("<")[0]); // split put stuff into an array
      sandPitX[1] = Integer.parseInt(finalArr.get(8).split("<")[2]);
      sandPitY[0] = Integer.parseInt(finalArr.get(9).split("<")[0]);
      sandPitY[1] = Integer.parseInt(finalArr.get(9).split("<")[2]);
      muks = Double.parseDouble(finalArr.get(10));
      muss = Double.parseDouble(finalArr.get(11));
   }

   public static void main(String[] args) {
      String csvFile = "C:/Users/leahi/Documents/AAA-Maastricht class/Period 1.3/Project/UserInput.csv";
      Reader.read(csvFile);
   }

}
