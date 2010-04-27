/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utils;
import java.util.Random;

/**
 *
 * @author pawelm
 */
public class Rand {

  private static Random rnd=null;

  private static void initialise(){
      if (rnd==null) rnd = new Random();
  }

  public static int GetRandomInt(int maxVal){
     if (rnd==null) initialise();
      return  rnd.nextInt(maxVal);
  }

  public static float GetRandomFloat(){
     if (rnd==null) initialise();
      return rnd.nextFloat();
  }

  public static boolean GetRandomBoolean(){
      if (rnd==null) initialise();
      return rnd.nextBoolean();
  }

  public static boolean GetRandomBooleanFlip(float border)
    {
      if (rnd==null) initialise();
      if (Rand.GetRandomFloat() <= border) return true;
        else return false;
    }

}
